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

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLType;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.util.Chain;

public class FieldReadWriteInspector {

  private Set<SootField> mAllFields;
  private Set<SootField> mReadOnGpuFields;
  private Set<SootField> mWrittenOnGpuFields;
  private Set<Local> mWrittenOnGpuArrayLocals;
  private SootClass mRuntimeBasicBlock;
  private Set<SootMethod> mMethodsInspected;
  private Set<String> mWritenOnGpuFieldsClassesChecked;

  public FieldReadWriteInspector(SootClass runtime_basic_block){
    mRuntimeBasicBlock = runtime_basic_block;
    mReadOnGpuFields = new HashSet<SootField>();
    mWrittenOnGpuFields = new HashSet<SootField>();
    mWrittenOnGpuArrayLocals = new HashSet<Local>();
    mAllFields = new HashSet<SootField>();
    mMethodsInspected = new HashSet<SootMethod>();
    mWritenOnGpuFieldsClassesChecked = new HashSet<String>();

    SootMethod root_method = mRuntimeBasicBlock.getMethodByName("run");
    inspectMethod(root_method);
    addParentFields();
  }

  public FieldReadWriteInspector(SootMethod root_method){
    mReadOnGpuFields = new HashSet<SootField>();
    mWrittenOnGpuFields = new HashSet<SootField>();
    mWrittenOnGpuArrayLocals = new HashSet<Local>();
    mAllFields = new HashSet<SootField>();
    mMethodsInspected = new HashSet<SootMethod>();
    mWritenOnGpuFieldsClassesChecked = new HashSet<String>();
    
    inspectMethod(root_method);
    addParentFields();
  }

  /**
   * Returns the fields read in the bytecode
   * @param ocl_field
   * @return
   */
  public boolean fieldIsReadOnGpu(OpenCLField ocl_field){
    SootField soot_field = ocl_field.getSootField();
    if(mReadOnGpuFields.contains(soot_field))
      return true;
    return false;
  }
  
  /**
   * Returns the fields written to in the bytecode
   * @param ocl_field
   * @return
   */
  public boolean fieldIsWrittenOnGpu(OpenCLField ocl_field){
    mWritenOnGpuFieldsClassesChecked.clear();
    SootField soot_field = ocl_field.getSootField();
    return fieldIsWrittenOnGpu(soot_field);
  }

  private boolean fieldIsWrittenOnGpu(SootField soot_field){
    if(mWrittenOnGpuFields.contains(soot_field))
      return true;
    
    if(soot_field.getDeclaringClass().getName().equals("java.lang.String"))
      return true;
    
    OpenCLType type = new OpenCLType(soot_field.getType());
    if(type.isRefType() == false)
      return false;

    Type soot_type = soot_field.getType();
    if(soot_type instanceof ArrayType){
      ArrayType atype = (ArrayType) soot_type;
      Type base_type = atype.baseType;
      type = new OpenCLType(base_type);
      if(type.isRefType() == false)
        return false;
      soot_type = base_type;
    }

    SootClass soot_class = RootbeerScene.v().getClass(soot_type.toString());
    if(mWritenOnGpuFieldsClassesChecked.contains(soot_type.toString()))
      return false;
    mWritenOnGpuFieldsClassesChecked.add(soot_type.toString());

    List<SootClass> soot_classes = OpenCLScene.v().getClassHierarchy(soot_class);
    for(SootClass curr : soot_classes){
      Chain<SootField> fields = curr.getFields();
      for(SootField field : fields){
        if(fieldIsWrittenOnGpu(field))
          return true;
      }
    }
    return false;
  }

  public boolean localRepresentingArrayIsWrittenOnGpu(Local local){
    return mWrittenOnGpuArrayLocals.contains(local);
  }

  private void addReadField(SootField field){
    mReadOnGpuFields.add(field);
  }

  private void addWriteField(SootField field){
    mWrittenOnGpuFields.add(field);
  }

  private SootField findFieldMakingArray(Body body, Value base){
    PatchingChain<Unit> units = body.getUnits();
    Iterator<Unit> iter = units.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      if(next instanceof AssignStmt == false)
        continue;
      AssignStmt assign = (AssignStmt) next;
      Value v = assign.getLeftOp();
      if(v.equals(base) == false)
        continue;

      Value rhs = assign.getRightOp();
      if(rhs instanceof FieldRef){
        FieldRef field_ref = (FieldRef) rhs;
        return field_ref.getField();
      } else if(rhs instanceof ArrayRef){
        ArrayRef array_ref = (ArrayRef) rhs;
        return findFieldMakingArray(body, array_ref.getBase());
      }

      return findFieldMakingArray(body, rhs);
    }
    return null;
  }

  private void inspectMethod(SootMethod method) {
    if(mMethodsInspected.contains(method))
      return;
    mMethodsInspected.add(method);

    Body body;
    try {
      body = method.getActiveBody();
    } catch(RuntimeException ex){
      //no body for method...
      System.out.println("No body for method: "+method.toString());
      return;
    }

    List<ValueBox> use_boxes = body.getUseBoxes();
    for(ValueBox use_box : use_boxes){
      Value use = use_box.getValue();
      if(use instanceof FieldRef){
        FieldRef field_ref = (FieldRef) use;
        SootField field = field_ref.getField();
        mAllFields.add(field);
        if(field.getType() instanceof ArrayType){
          if(!arrayIsReadFrom(body, use))
            continue;
        }
        addReadField(field_ref.getField());
      }
    }

    List<ValueBox> def_boxes = body.getDefBoxes();
    for(ValueBox def_box : def_boxes){
      Value def = def_box.getValue();
      if(def instanceof FieldRef){
        FieldRef field_ref = (FieldRef) def;
        SootField field = field_ref.getField();
        addWriteField(field);
        mAllFields.add(field);
      } else if (def instanceof ArrayRef){
        ArrayRef array_ref = (ArrayRef) def;
        SootField field = findFieldMakingArray(body, array_ref.getBase());
        if(field != null){
          addWriteField(field);
          Value base = array_ref.getBase();
          if(base instanceof Local){
            Local local_base = (Local) base;
            mWrittenOnGpuArrayLocals.add(local_base);
          }
        }
      }
    }

    for(ValueBox use_box : use_boxes){
      Value use = use_box.getValue();
      if(use instanceof InvokeExpr){
        InvokeExpr invoke_expr = (InvokeExpr) use;
        inspectMethod(invoke_expr.getMethod());
      }
    }
  }

  private boolean arrayIsReadFrom(Body body, Value array_field_ref) {
    PatchingChain<Unit> units = body.getUnits();
    Iterator<Unit> iter = units.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      if(next instanceof AssignStmt == false)
        continue;
      AssignStmt assign = (AssignStmt) next;
      Value v = assign.getRightOp();
      if(v.equals(array_field_ref) == false)
        continue;

      Value lhs = assign.getLeftOp();
      if(arrayIsReadFromStep2(body, lhs))
        return true;
      return arrayIsReadFrom(body, lhs);
    }
    return false;
  }

  private boolean arrayIsReadFromStep2(Body body, Value array_base){
    PatchingChain<Unit> units = body.getUnits();
    Iterator<Unit> iter = units.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      if(next instanceof AssignStmt == false)
        continue;
      AssignStmt assign = (AssignStmt) next;
      Value v = assign.getRightOp();
      if(v instanceof ArrayRef == false)
        continue;
      ArrayRef array_ref = (ArrayRef) v;
      if(array_ref.getBase().equals(array_base))
        return true;
    }
    return false;
  }

  private void addParentFields() {
    addParentFields(mWrittenOnGpuFields);
    addParentFields(mReadOnGpuFields);
  }

  private void addParentFields(Set<SootField> mReadOrWriteFields) {
    int prev_size = Integer.MAX_VALUE;
    int curr_size = mReadOrWriteFields.size();
    do {
      Iterator<SootField> all_iter = mAllFields.iterator();
      while(all_iter.hasNext()){
        SootField next = all_iter.next();
        Type type = next.getType();
        if(type instanceof RefType == false)
          continue;
        RefType ref_type = (RefType) type;
        SootClass field_class = ref_type.getSootClass();
        Iterator<SootField> read_or_write_iter = mReadOrWriteFields.iterator();
        while(read_or_write_iter.hasNext()){
          SootField read_or_write_field = read_or_write_iter.next();
          if(read_or_write_field.getDeclaringClass().equals(field_class)){
            mReadOrWriteFields.add(next);
            break;
          }
        }
      }
      prev_size = curr_size;
      curr_size = mReadOrWriteFields.size();
    } while(prev_size != curr_size);
  }

}
