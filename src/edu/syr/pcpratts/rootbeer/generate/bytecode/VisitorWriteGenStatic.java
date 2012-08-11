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
import edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph.PermissionGraph;
import edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph.PermissionGraphNode;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.util.Stack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.BooleanType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.SootClass;
import soot.Type;
import soot.VoidType;
import soot.jimple.ClassConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StringConstant;

public class VisitorWriteGenStatic extends AbstractVisitorGen {
  
  private Local m_Mem;
  private StaticOffsets m_StaticOffsets;
  private Set<String> m_AttachedWriters;
    
  public VisitorWriteGenStatic(BytecodeLanguage bcl, FieldReadWriteInspector inspector){
    super(inspector);
    
    m_Bcl.push(bcl);
    m_StaticOffsets = new StaticOffsets();
    m_AttachedWriters = new HashSet<String>();
  }
  
  public void makeMethod(){
    BytecodeLanguage bcl = m_Bcl.top();    
    bcl.startMethod("doWriteStaticsToHeap", VoidType.v());
    
    m_ThisRef = bcl.refThis();
    m_GcObjVisitor.push(m_ThisRef);
    m_Mem = bcl.refInstanceField(m_ThisRef, "mMem");  
    m_CurrMem.push(m_Mem);
    
    BclMemory bcl_mem = new BclMemory(bcl, m_Mem);
    bcl_mem.useInstancePointer();
    bcl_mem.mallocWithSize(IntConstant.v(m_StaticOffsets.getEndIndex()));
    PermissionGraph graph = new PermissionGraph();   
    List<PermissionGraphNode> roots = graph.getRoots();
    for(PermissionGraphNode node : roots){
      SootClass soot_class = node.getSootClass();
      if(soot_class.isApplicationClass()){
        attachAndCallWriter(soot_class, node.getChildren());
      } else {
        doWriter(soot_class);
      }
    }
    
    bcl_mem.useStaticPointer();
    bcl_mem.setAddress(LongConstant.v(m_StaticOffsets.getLockStart()));
    //write the lock objects for all the classes
    int count = m_StaticOffsets.getClassSize();
    for(int i = 0; i < count; ++i){
      bcl_mem.writeInt(-1);
    }
    int zeros = m_StaticOffsets.getZerosSize();
    for(int i = 0; i < zeros; ++i){
      bcl_mem.writeByte((byte) 0);
    }
    bcl_mem.useInstancePointer();
    
    bcl.returnVoid();    
    bcl.endMethod();
    
    m_GcObjVisitor.pop();
  }
  
  private void attachAndCallWriter(SootClass soot_class, List<SootClass> children){    
    String class_name = soot_class.getName();
    if(m_ClassesToIgnore.contains(class_name))
      return;
    
    attachWriter(soot_class, children);
    callWriter(soot_class);
  }
  
  private void callWriter(SootClass soot_class){    
    BytecodeLanguage bcl = m_Bcl.top();
    String method_name = getWriterName(soot_class);
    SootClass mem = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.pushMethod(soot_class, method_name, VoidType.v(), mem.getType(), m_GcObjVisitor.top().getType());
    bcl.invokeStaticMethodNoRet(m_CurrMem.top(), m_GcObjVisitor.top());
  }
  
  private String getWriterName(SootClass soot_class){
    return "edu_syr_pcpratts_writeStaticsToHeap"+JavaNameToOpenCL.convert(soot_class.getName())+OpenCLScene.v().getIdent();
  }

  private void attachWriter(SootClass soot_class, List<SootClass> children){    
        
    String method_name = getWriterName(soot_class);
    if(m_AttachedWriters.contains(method_name))
      return;
    m_AttachedWriters.add(method_name);
        
    BytecodeLanguage bcl = new BytecodeLanguage();
    m_Bcl.push(bcl);
    bcl.openClass(soot_class);
    SootClass mem = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.startStaticMethod(method_name, VoidType.v(), mem.getType(), m_GcObjVisitor.top().getType());
    
    Local memory = bcl.refParameter(0);
    Local gc_visit = bcl.refParameter(1);
    m_CurrMem.push(memory);
    m_GcObjVisitor.push(gc_visit);
    
    doWriter(soot_class);
    
    for(SootClass child : children){
      attachAndCallWriter(child, new ArrayList<SootClass>());
    }
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_Bcl.pop();
    m_GcObjVisitor.pop();
    m_CurrMem.pop();
  }
  
  private void doWriter(SootClass soot_class){  
    BytecodeLanguage bcl = m_Bcl.top();
    Local memory = m_CurrMem.top();
    Local gc_visit = m_GcObjVisitor.top();
    
    List<OpenCLField> static_fields = m_StaticOffsets.getStaticFields(soot_class);
    
    BclMemory bcl_mem = new BclMemory(bcl, memory);
    SootClass obj = RootbeerScene.v().getClass("java.lang.Object");
    for(OpenCLField field : static_fields){
      Local field_value;
      if(soot_class.isApplicationClass()){
        field_value = bcl.refStaticField(soot_class.getType(), field.getName());
      } else {
        SootClass string = RootbeerScene.v().getClass("java.lang.String");
        SootClass cls = RootbeerScene.v().getClass("java.lang.Class");
        bcl.pushMethod(gc_visit, "readStaticField", obj.getType(), cls.getType(), string.getType());
        field_value = bcl.invokeMethodRet(gc_visit, ClassConstant.v(toConstant(soot_class.getName())), StringConstant.v(field.getName()));
      }
      if(field.getType().isRefType()){
        bcl.pushMethod(gc_visit, "writeToHeap", LongType.v(), obj.getType(), BooleanType.v());
        Local ref = bcl.invokeMethodRet(gc_visit, field_value, IntConstant.v(1));
        bcl_mem.useStaticPointer();
        bcl_mem.setAddress(LongConstant.v(m_StaticOffsets.getIndex(field)));
        bcl_mem.writeRef(ref);
        bcl_mem.useInstancePointer();
      } else {
        bcl_mem.useStaticPointer();
        bcl_mem.setAddress(LongConstant.v(m_StaticOffsets.getIndex(field)));
        bcl_mem.writeVar(field_value);
        bcl_mem.useInstancePointer();
      }
    } 
  }

  private String toConstant(String name) {
    return name.replace(".", "/");
  }
  
}
