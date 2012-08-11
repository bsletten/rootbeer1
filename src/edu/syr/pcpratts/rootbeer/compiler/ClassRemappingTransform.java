/*
 * This file is part of Rootbeer.
 * 
 * Rootbeer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Rootbeer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Rootbeer.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package edu.syr.pcpratts.rootbeer.compiler;

import edu.syr.pcpratts.rootbeer.util.SignatureUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CastExpr;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;

public class ClassRemappingTransform {

  private ClassRemapping m_ClassRemapping;
  private Map<SootField, List<FieldRef>> m_FieldsToFix; 
  private Set<String> m_Modified;
  private String m_CurrClass;
  private boolean m_HasNext;
  private boolean m_AppClass;
  
  public ClassRemappingTransform(boolean map_runtime){
    m_ClassRemapping = new ClassRemapping();
    if(!map_runtime){
      m_ClassRemapping.loadMap();
    }
    m_FieldsToFix = new HashMap<SootField, List<FieldRef>>();
    m_Modified = new HashSet<String>();
    m_HasNext = true;
  }
  
  public void reset(){
    m_HasNext = false;
  }
  
  public void run(List<String> reachable_methods){
    SignatureUtil sig_util = new SignatureUtil();
    for(String method : reachable_methods){
      String cls_name = sig_util.classFromMethodSig(method);
      SootClass soot_class = RootbeerScene.v().getClass(cls_name);
      m_AppClass = soot_class.isApplicationClass();
      m_CurrClass = cls_name;
      String sub_sig = sig_util.methodSubSigFromMethodSig(method);
      SootMethod soot_method = soot_class.getMethod(sub_sig);
      visit(soot_method);
    }
  }
  
  public void run(String cls) {
    SootClass soot_class = RootbeerScene.v().getClass(cls);
    run(cls, soot_class.isApplicationClass());
  }
  
  private void run(String cls, boolean app_class){
    m_CurrClass = cls;
    SootClass soot_class = RootbeerScene.v().getClass(cls);
    m_AppClass = app_class;
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      visit(method);
    }
  }
  
  public void finishClone(){
    List<String> cloned = m_ClassRemapping.getCloned();
    for(String cls : cloned){
      run(cls, true);
    }
    fixFields();
  }
  
  public Set<String> getModifiedClasses(){
    return m_Modified;
  }
  
  public void setModified(){
    m_Modified.add(m_CurrClass);
  }
  
  public void fixFields(){
    Iterator<SootField> iter = m_FieldsToFix.keySet().iterator();
    while(iter.hasNext()){
      SootField curr = iter.next();
      SootField orig = curr;
      SootClass field_cls = curr.getDeclaringClass();
      if(shouldMap(field_cls)){
        SootClass new_field_cls = getMapping(field_cls);
        curr = new_field_cls.getFieldByName(curr.getName());        
      }
      Type type = curr.getType();
      if(type instanceof RefType){
        RefType ref_type = (RefType) type;
        if(shouldMap(ref_type.getSootClass())){
          SootClass new_class = getMapping(ref_type.getSootClass());
          curr.setType(new_class.getType());
        }
      } else if(type instanceof ArrayType){
        ArrayType array_type = (ArrayType) type;
        RefType ref_type = (RefType) array_type.baseType;
        if(shouldMap(ref_type.getSootClass())){
          SootClass new_class = getMapping(ref_type.getSootClass());
          ArrayType new_type = ArrayType.v(new_class.getType(), array_type.numDimensions);
          curr.setType(new_type);
        }
      }
      List<FieldRef> refs = m_FieldsToFix.get(orig);
      for(FieldRef ref : refs){
        ref.setFieldRef(curr.makeRef());
      }
    }
  }
  
  private void visit(SootMethod method) {    
    Body body = RootbeerScene.v().getBody(method);
    if(body == null)
      return;
    fixArguments(method);
    Iterator<Unit> iter = body.getUnits().iterator();
    while(iter.hasNext()){
      Unit curr = iter.next();
      List<ValueBox> boxes = curr.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        value = mutate(value);
        box.setValue(value);
      }
    }
  }

  private Value mutate(Value value) {
    if(value instanceof FieldRef){
      FieldRef ref = (FieldRef) value;  
      SootField field = ref.getField();
      Type type = field.getType();
      if(type instanceof RefType){
        RefType ref_type = (RefType) type;
        SootClass soot_class = ref_type.getSootClass();
        if(shouldMap(soot_class)){
          addField(field, ref);
        }
      } else if(type instanceof ArrayType){
        ArrayType array_type = (ArrayType) type;
        Type base_type = array_type.baseType;
        if(base_type instanceof RefType){
          RefType ref_type = (RefType) base_type;
          SootClass soot_class = ref_type.getSootClass();
          if(shouldMap(soot_class)){
            addField(field, ref);
          }
        }
      } 
      SootClass soot_class = field.getDeclaringClass();
      if(shouldMap(soot_class)){
        addField(field, ref); 
      }      
      return value;
    } else if(value instanceof InvokeExpr){
      InvokeExpr expr = (InvokeExpr) value;
      SootMethodRef ref = expr.getMethodRef();
      SootClass soot_class = ref.declaringClass();
      if(shouldMap(soot_class)){
        SootClass new_class = getMapping(soot_class);
        SootMethod new_method = new_class.getMethod(ref.getSubSignature());
        fixArguments(new_method);
        expr.setMethodRef(new_method.makeRef());
      } else {
        if(soot_class.declaresMethod(ref.getSubSignature())){
          SootMethod method = soot_class.getMethod(ref.getSubSignature());
          fixArguments(method);
        }     
      }
      ref = remapRef(ref);
      try {
        if(shouldMap(soot_class)){
          soot_class = getMapping(soot_class);
        }
        SootMethod method = soot_class.getMethod(ref.getSubSignature());
        expr.setMethodRef(method.makeRef());
      } catch(Exception ex){
        //ex.printStackTrace();
      }
      return value;
    } else if(value instanceof NewExpr){
      NewExpr expr = (NewExpr) value;
      RefType base_type = expr.getBaseType();
      SootClass soot_class = base_type.getSootClass();
      if(shouldMap(soot_class)){
        SootClass new_class = getMapping(soot_class);
        expr.setBaseType(new_class.getType());
      }
      return value;
    } else if(value instanceof NewArrayExpr){
      NewArrayExpr expr = (NewArrayExpr) value;
      Type base_type = expr.getBaseType();
      base_type = fixType(base_type);
      expr.setBaseType(base_type);
      return value;      
    } else if(value instanceof NewMultiArrayExpr){
      NewMultiArrayExpr expr = (NewMultiArrayExpr) value;
      ArrayType array_type = expr.getBaseType();
      Type base_type = array_type.baseType;
      if(base_type instanceof RefType){
        RefType ref_type = (RefType) base_type;
        SootClass soot_class = ref_type.getSootClass();
        if(shouldMap(soot_class)){
          SootClass new_class = getMapping(soot_class);
          ArrayType new_type = ArrayType.v(new_class.getType(), array_type.numDimensions);
          expr.setBaseType(new_type);
        }
      }
      return value;
    } else if(value instanceof CastExpr){
      CastExpr expr = (CastExpr) value;
      Type cast_type = expr.getCastType();
      cast_type = fixType(cast_type);
      expr.setCastType(cast_type);
      return value;
    } else if(value instanceof ParameterRef){
      ParameterRef ref = (ParameterRef) value;
      Type new_type = fixType(ref.getType());
      return new ParameterRef(new_type, ref.getIndex());
    } else if(value instanceof ThisRef){
      ThisRef ref = (ThisRef) value;
      Type new_type = fixType(ref.getType());
      return new ThisRef((RefType) new_type);
    }else if(value instanceof Local){
      Local local = (Local) value;
      Type type = local.getType();
      local.setType(fixType(type));
      return value;
    } else {
      return value;
    }
  }

  private boolean shouldMap(SootClass soot_class) {
    if(m_AppClass){
      if(m_ClassRemapping.containsKey(soot_class.getName())){
        setModified();
        return true;
      } else {
        return false;
      }
    } else {
      if(m_ClassRemapping.containsKey(soot_class.getName()) && !m_ClassRemapping.cloned(m_CurrClass)){
        m_HasNext = true;
        m_ClassRemapping.cloneClass(m_CurrClass);
      }
      return false;
    }
  }
  
  private SootMethodRef remapRef(SootMethodRef ref) {
    Type return_type = fixType(ref.returnType());
    List params = fixParameterList(ref.parameterTypes());
    SootMethod method = new SootMethod(ref.name(), params, return_type);
    SootClass decl_class = ref.declaringClass();
    if(shouldMap(decl_class)){
      decl_class = getMapping(decl_class);
    }
    method.setDeclaringClass(decl_class);
    return method.makeRef();
  }
  
  public ClassRemapping getClassRemapping(){
    return m_ClassRemapping;
  }

  private SootClass getMapping(SootClass soot_class) {
    String new_class = m_ClassRemapping.get(soot_class.getName());
    return RootbeerScene.v().getClass(new_class);
  }

  private void addField(SootField field, FieldRef ref) {
    List<FieldRef> refs;
    if(m_FieldsToFix.containsKey(field)){
      refs = m_FieldsToFix.get(field);
    } else {
      refs = new ArrayList<FieldRef>();
      m_FieldsToFix.put(field, refs);
    }
    refs.add(ref);
  }

  private void fixArguments(SootMethod method) {
    Type ret_type = method.getReturnType();
    method.setReturnType(fixType(ret_type));
    List param_types = method.getParameterTypes();
    List new_types = fixParameterList(param_types);
    method.setParameterTypes(new_types);    
  }
  
  private List fixParameterList(List param_types){
    List ret = new ArrayList();
    for(int i = 0; i < param_types.size(); ++i){
      Type type = (Type) param_types.get(i);
      ret.add(fixType(type));
    }  
    return ret;
  }
  
  private Type fixType(Type type){
    if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      SootClass soot_class = ref_type.getSootClass();
      if(shouldMap(soot_class)){
        SootClass new_class = getMapping(soot_class);
        return new_class.getType();
      } else {
        return type;
      }
    } else if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      Type base = fixType(array_type.baseType);
      return ArrayType.v(base, array_type.numDimensions);
    } else {
      return type;
    }
  }

  public boolean hasNext() {
    return m_HasNext;
  }

}
