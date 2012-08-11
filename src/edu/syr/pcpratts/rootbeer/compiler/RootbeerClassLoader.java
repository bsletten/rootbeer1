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

import edu.syr.pcpratts.rootbeer.compiler.ClassRemapping;
import edu.syr.pcpratts.rootbeer.compiler.FindRunOnGpuClasses;
import edu.syr.pcpratts.rootbeer.compiler.SpecialInvokeFixup;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.util.SignatureUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.Body;
import soot.PatchingChain;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;

public class RootbeerClassLoader {
  
  private List<String> m_ClassLoadList;
  private List<String> m_VisitedClasses;
  private List<String> m_MethodLoadList;
  private Set<String> m_VisitedMethods;
  private List<String> m_AppClasses;
  
  public RootbeerClassLoader(){
    m_ClassLoadList = new ArrayList<String>();  
    m_VisitedClasses = new ArrayList<String>();
    m_MethodLoadList = new ArrayList<String>();
    m_VisitedMethods = new HashSet<String>();
  }
    
  /**
   * 
   * @param jar_filename
   * @return the run_on_gpu classes
   * @throws Exception 
   */
  public List<String> load(String jar_filename) throws Exception {    
    System.out.println("Initializing RootbeerScene...");
    RootbeerScene.v().init(jar_filename);
    
    m_AppClasses = RootbeerScene.v().getApplicationClasses();

    System.out.println("Finding RunOnGpu Classes...");
    FindRunOnGpuClasses finder1 = new FindRunOnGpuClasses();
    List<String> run_on_gpu = finder1.get(m_AppClasses);

    System.out.println("Loading Scene...");
    for(String cls : run_on_gpu){
      loadMethod(cls, "void gpuMethod()", true);
    } 
    
    while(m_MethodLoadList.isEmpty() == false){
      String method_sig = m_MethodLoadList.get(0);
      m_MethodLoadList.remove(0);
      if(m_VisitedMethods.contains(method_sig))
        continue;
      m_VisitedMethods.add(method_sig);
      
      SignatureUtil util = new SignatureUtil();
      loadMethod(util.classFromMethodSig(method_sig), util.methodSubSigFromMethodSig(method_sig), true);
    }
    
    ClassRemapping remapping = new ClassRemapping();
    m_VisitedClasses.addAll(remapping.getRuntimeClasses());
    
    System.out.println("Loading all methods in visited classes...");    
    for(int i = 0; i < m_VisitedClasses.size(); ++i){
      String cls = m_VisitedClasses.get(i);
      if(cls.equals("java.lang.Object"))
        continue;
      SootClass soot_class = RootbeerScene.v().getClass(cls);
      List<SootMethod> methods = soot_class.getMethods();
      for(SootMethod method : methods){
        if(m_VisitedMethods.contains(method.getSignature()))
          continue;
        loadMethod(cls, method.getSubSignature(), false);
      }
    } 
    
    for(String cls : m_AppClasses){
      SootClass soot_class = RootbeerScene.v().getClass(cls);  
      soot_class.setApplicationClass();
    }
    
    return run_on_gpu;
  }
    
  private void loadAllClasses(String cls){    
    if(m_VisitedClasses.contains(cls))
      return;
    m_VisitedClasses.add(cls);
    loadClass(cls);
    
    while(m_ClassLoadList.isEmpty() == false){
      String cls2 = m_ClassLoadList.get(0);
      m_ClassLoadList.remove(0);
      if(m_VisitedClasses.contains(cls2))
        continue;
      m_VisitedClasses.add(cls2);
      loadClass(cls2);
    }
  }
  
  private void loadMethod(String cls, String method, boolean load_classes_for_method){
    loadAllClasses(cls);
    
    SootClass soot_class = RootbeerScene.v().getClass(cls);
    SootMethod soot_method = null; 
        
    while(true){
      try {
        soot_method = soot_class.getMethod(method);
        if(load_classes_for_method)
          loadClassesForMethod(soot_method);
        break;
      } catch(Exception ex){
        if(soot_class.hasOuterClass()){
          loadAllClasses(soot_class.getOuterClass().getName());
        }
        if(soot_class.hasSuperclass()) {
          loadAllClasses(soot_class.getSuperclass().getName());
          soot_class = soot_class.getSuperclass();
        }
      }
    }
    
    loadBody(soot_method); 
  }
  
  private void loadBody(SootMethod soot_method){
    if(soot_method.isConcrete()){
      Body body = RootbeerScene.v().getBody(soot_method);
      loadFieldRefsForBody(body);
      List<String> methods = findMethodCallsForBody(body);
      for(String sig : methods){
        m_MethodLoadList.add(sig);
      }
      SpecialInvokeFixup fixer = new SpecialInvokeFixup();
      RootbeerScene.v().setBody(soot_method, fixer.fixup(body));
    }
  }
  
  public List<String> findMethodCallsForBody(Body body) {
    List<String> methods = new ArrayList<String>();    
    PatchingChain<Unit> chain = body.getUnits();
    Iterator<Unit> iter = chain.iterator();
    while(iter.hasNext()){
      Unit unit = iter.next();
      List<ValueBox> vboxes = unit.getUseAndDefBoxes();
      for(ValueBox vbox : vboxes){
        Value value = vbox.getValue();
        if(value instanceof InvokeExpr == false)
          continue;
        InvokeExpr expr = (InvokeExpr) value;
        String sig = expr.getMethodRef().getSignature();
        if(methods.contains(sig) == false)
          methods.add(sig);
      }
    }
    return methods;
  }
  
  private void loadClass(String cls) {
    SootClass soot_class = RootbeerScene.v().getClass(cls);
    addHierarchy(soot_class);
  }
  
  private void addHierarchy(SootClass soot_class) {
    Iterator<SootClass> iter = soot_class.getInterfaces().iterator();
    while(iter.hasNext()){      
      m_ClassLoadList.add(iter.next().getName());
    }
    if(soot_class.hasSuperclass()){
      m_ClassLoadList.add(soot_class.getSuperclass().getName());
    }
    if(soot_class.hasOuterClass()){
      m_ClassLoadList.add(soot_class.getOuterClass().getName());
    }
  }
  
  public List<String> getVisitedClasses() {
    List<String> ret = new ArrayList<String>();
    Iterator<String> iter = m_VisitedClasses.iterator();
    while(iter.hasNext()){
      String cls = iter.next();
      ret.add(cls);
    }
    return ret;
  }
  
  public List<String> getClassesToCopy(){
    List<String> ret = new ArrayList<String>();
    List<String> visited = getVisitedClasses();
    for(String str : m_AppClasses){
      if(visited.contains(str) == false)
        ret.add(str);
    }
    return ret;
  }
  
  private void loadClassesForMethod(SootMethod soot_method) {
    loadClassesForType(soot_method.getReturnType());
    List<Type> param_types = soot_method.getParameterTypes();
    for(Type param_type : param_types){
      loadClassesForType(param_type);
    }
  }

  private void loadClassesForType(Type type) {
    if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      loadAllClasses(ref_type.getSootClass().getName());
    } if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      loadClassesForType(array_type.baseType);
    }
  }

  private void loadFieldRefsForBody(Body body) {
    PatchingChain<Unit> chain = body.getUnits();
    Iterator<Unit> iter = chain.iterator();
    while(iter.hasNext()){
      Unit unit = iter.next();
      List<ValueBox> vboxes = unit.getUseAndDefBoxes();
      for(ValueBox vbox : vboxes){
        Value value = vbox.getValue();
        if(value instanceof FieldRef == false)
          continue;
        FieldRef ref = (FieldRef) value;
        loadClassesForType(ref.getField().getType());
      }
    }
  }
  
  public static void main(String[] args){
    try {
      RootbeerClassLoader pre_compile = new RootbeerClassLoader();
      //pre_compile.run("../Rootbeer-Test2/dist/Rootbeer-Test2.jar");
      pre_compile.load("../../FruitingCompute/ofcoarse/ofcoarse-1.0.jar");
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
