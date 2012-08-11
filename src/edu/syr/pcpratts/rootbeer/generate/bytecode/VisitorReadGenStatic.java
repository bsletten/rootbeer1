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
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.VoidType;
import soot.jimple.LongConstant;

public class VisitorReadGenStatic extends AbstractVisitorGen {
  
  private Local m_Mem;
  private List<RefType> m_OrderedHistory;
  private Set<RefType> m_WriteToHeapMethodsMade;
  private Set<String> m_AttachedReaders;
  private StaticOffsets m_StaticOffsets;
  
  public VisitorReadGenStatic(BytecodeLanguage bcl, FieldReadWriteInspector inspector){
    super(inspector);
    m_Bcl.push(bcl);
    
    m_OrderedHistory = OpenCLScene.v().getRefTypeOrderedHistory();
    m_WriteToHeapMethodsMade = new HashSet<RefType>();
    m_AttachedReaders = new HashSet<String>();
    m_ObjSerializing = new Stack<Local>();
    m_StaticOffsets = new StaticOffsets();
    m_FieldInspector = inspector;
    m_CurrMem = new Stack<Local>();
  }
  
  public void makeMethod(){
   
    BytecodeLanguage bcl = m_Bcl.top();
    
    bcl.startMethod("doReadStaticsFromHeap", VoidType.v());

    m_ThisRef = bcl.refThis();
    m_Mem = bcl.refInstanceField(m_ThisRef, "mMem"); 
    m_CurrMem.push(m_Mem);
    m_GcObjVisitor.push(m_ThisRef);
    
    PermissionGraph graph = new PermissionGraph();   
    List<PermissionGraphNode> roots = graph.getRoots();
    for(PermissionGraphNode node : roots){
      SootClass soot_class = node.getSootClass();
      if(soot_class.isApplicationClass()){
        attachAndCallReader(soot_class, node.getChildren());
      }
    }
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_CurrMem.pop();
    m_GcObjVisitor.pop();    
  }

  private String getReaderName(SootClass soot_class){
    return "edu_syr_pcpratts_readStaticsFromHeap"+JavaNameToOpenCL.convert(soot_class.getName())+OpenCLScene.v().getIdent();
  }
  
  private void attachReader(SootClass soot_class, List<SootClass> children){    
    String method_name = getReaderName(soot_class);
    if(m_AttachedReaders.contains(method_name))
      return;
    m_AttachedReaders.add(method_name);
    
    List<OpenCLField> static_fields = m_StaticOffsets.getStaticFields(soot_class);
    
    BytecodeLanguage bcl = new BytecodeLanguage();
    m_Bcl.push(bcl);
    bcl.openClass(soot_class);
    SootClass mem = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.startStaticMethod(method_name, VoidType.v(), mem.getType(), m_ThisRef.getType());
    
    Local memory = bcl.refParameter(0);
    Local gc_visit = bcl.refParameter(1);
    m_GcObjVisitor.push(gc_visit);
    m_CurrMem.push(memory);
    
    BclMemory bcl_mem = new BclMemory(bcl, memory);
    for(OpenCLField field : static_fields){
      if(m_FieldInspector.fieldIsWrittenOnGpu(field) == false)
        continue;
      
      int index = m_StaticOffsets.getIndex(field);
      bcl_mem.setAddress(LongConstant.v(index));
      if(field.getType().isRefType()){
        readRefField(field);
      } else {
        readNonRefField(field);
      }      
    }
    
    for(SootClass child : children){
      attachAndCallReader(child, new ArrayList<SootClass>());
    }
    
    bcl.returnVoid();
    bcl.endMethod();
    
    m_GcObjVisitor.pop();
    m_CurrMem.pop();
    m_Bcl.pop();
  }

  private void attachAndCallReader(SootClass soot_class, List<SootClass> children) {
    String class_name = soot_class.getName();
    if(m_ClassesToIgnore.contains(class_name))
      return;
    
    attachReader(soot_class, children);
    callReader(soot_class);
  }

  private void callReader(SootClass soot_class) {    
    BytecodeLanguage bcl = m_Bcl.top();
    String method_name = getReaderName(soot_class);
    SootClass mem = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    bcl.pushMethod(soot_class, method_name, VoidType.v(), mem.getType(), m_ThisRef.getType());
    bcl.invokeStaticMethodNoRet(m_CurrMem.top(), m_GcObjVisitor.top());
  }
}
