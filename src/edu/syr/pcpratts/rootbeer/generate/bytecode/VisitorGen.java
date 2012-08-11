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
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLType;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.BooleanType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.IntConstant;
import soot.jimple.NullConstant;

public class VisitorGen extends AbstractVisitorGen {

  SootClass mRuntimeBasicBlock;
  private String mClassName;
  private Set<Type> mGetSizeMethodsMade;
  private Set<String> mSentinalCtorsCreated;

  //Locals from code generation
  private Local mParam0;
  private Local mRef;
  private Local mStartIndex;
  private Local mEndIndex;
  private Local mCore;

  public VisitorGen(FieldReadWriteInspector field_inspector, SootClass runtime_basic_block){
    super(field_inspector);
    mRuntimeBasicBlock = runtime_basic_block;
    mGetSizeMethodsMade = new HashSet<Type>();
    mSentinalCtorsCreated = new HashSet<String>();
  }

  public void generate(){
    m_Bcl.push(new BytecodeLanguage());
    makeSentinalCtors();
    makeGcObjectVisitor();
    addGetVisitorMethodToRuntimeBasicBlock();
  }

  private void makeGcObjectVisitor() {
    makeGcObjectClass();
    makeCtor();
    makeWriteStaticsToHeapMethod();
    makeReadStaticsFromHeapMethod();
    makeGetSizeMethod();
    makeGetLengthMethod();
    makeWriteToHeapMethod();
    makeReadFromHeapMethod();
  }

  public int getId(SootClass soot_class){
    return OpenCLScene.v().getClassType(soot_class);
  }

  private void makeGcObjectClass() {
    String base_name = mRuntimeBasicBlock.getName();
    mClassName = base_name+"GcObjectVisitor";
    m_Bcl.top().makeClass(mClassName, "edu.syr.pcpratts.rootbeer.runtime.Serializer");
  }
  /*
  public abstract void doWriteArrayToHeap(Object o, long ref, int start_index, int end_index, int core);
  public abstract void doReadArrayFromHeap(Object o, long ref, int start_index, int end_index, int core);
   */
    
  private void makeGetLengthMethod(){    
    SootClass object_soot_class = RootbeerScene.v().getClass("java.lang.Object");
    m_Bcl.top().startMethod("doGetSize", IntType.v(), object_soot_class.getType());
    m_ThisRef = m_Bcl.top().refThis();
    mParam0 = m_Bcl.top().refParameter(0);
    
    List<Type> all_possible_types = OpenCLScene.v().getOrderedHistory();
    for(Type type : all_possible_types){
      makeGetSizeMethodForType(type);
    }

    m_Bcl.top().returnValue(IntConstant.v(0));
    m_Bcl.top().endMethod();
  }

  private void makeGetSizeMethod(){
    SootClass object_soot_class = RootbeerScene.v().getClass("java.lang.Object");
    m_Bcl.top().startMethod("getArrayLength", IntType.v(), object_soot_class.getType());
    m_ThisRef = m_Bcl.top().refThis();
    mParam0 = m_Bcl.top().refParameter(0);
        
    List<Type> all_possible_types = OpenCLScene.v().getOrderedHistory();
    for(Type type : all_possible_types){
      makeGetLengthMethodForType(type);
    }

    m_Bcl.top().returnValue(IntConstant.v(0));
    m_Bcl.top().endMethod();
  }
  
  private void makeGetLengthMethodForType(Type type){
    if(type instanceof ArrayType == false)
      return;
    
    String label = getNextLabel();
    m_Bcl.top().ifInstanceOfStmt(mParam0, type, label);
    Local object_to_write_from = m_Bcl.top().cast(type, mParam0);
    Local length = m_Bcl.top().lengthof(object_to_write_from);
    m_Bcl.top().returnValue(length);
    m_Bcl.top().label(label);
  }

  private void makeGetSizeMethodForType(Type type) {
    if(mGetSizeMethodsMade.contains(type))
      return;
    mGetSizeMethodsMade.add(type);
    
    if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      SootClass soot_class = ref_type.getSootClass();
      if(soot_class.getName().equals("java.lang.Object"))
        return; 
      if(differentPackageAndPrivate(ref_type)){
        return;  
      }
    }
    
    if(typeIsPublic(type) == false)
      return;
    
    String label = getNextLabel();
    m_Bcl.top().ifInstanceOfStmt(mParam0, type, label);
        
    if(type instanceof ArrayType){
      ArrayType atype = (ArrayType) type;
      SootClass constants_soot_class = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.generate.bytecode.Constants");
      Local size = m_Bcl.top().refStaticField(constants_soot_class.getType(), "ArrayOffsetSize");
      Local element_size = m_Bcl.top().local(IntType.v());
      OpenCLType ocl_type = new OpenCLType(atype.baseType);
      if(atype.numDimensions == 1)
        m_Bcl.top().assign(element_size, IntConstant.v(ocl_type.getSize()));
      else
        m_Bcl.top().assign(element_size, IntConstant.v(4));
      Local object_to_write_from = m_Bcl.top().cast(type, mParam0);
      Local length = m_Bcl.top().lengthof(object_to_write_from);
      m_Bcl.top().mult(element_size, length);
      m_Bcl.top().plus(size, element_size);
      m_Bcl.top().returnValue(size);
    }else if(type instanceof RefType) {
      RefType rtype = (RefType) type;
      OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(rtype.getSootClass());
      int size = ocl_class.getSize();
      m_Bcl.top().returnValue(IntConstant.v(size));
    }
    m_Bcl.top().label(label);
    
  }
  
  private void makeWriteToHeapMethod() {
    VisitorWriteGen write_gen = new VisitorWriteGen(OpenCLScene.v().getOrderedHistory(), 
      mClassName, m_Bcl.top(), m_FieldInspector);
    write_gen.makeWriteToHeapMethod();
  }
      
  private void makeReadFromHeapMethod() {
    VisitorReadGen read_gen = new VisitorReadGen(OpenCLScene.v().getOrderedHistory(), 
      mClassName, m_Bcl.top(), m_FieldInspector);
    read_gen.makeReadFromHeapMethod();
  }

  private void makeWriteStaticsToHeapMethod() {
    VisitorWriteGenStatic static_write_gen = new VisitorWriteGenStatic(m_Bcl.top(), m_FieldInspector);
    static_write_gen.makeMethod();
  }

  private void makeReadStaticsFromHeapMethod() {
    VisitorReadGenStatic static_read_gen = new VisitorReadGenStatic(m_Bcl.top(), m_FieldInspector);
    static_read_gen.makeMethod();
  }
  
  private void addGetVisitorMethodToRuntimeBasicBlock() {
    m_Bcl.top().openClass(mRuntimeBasicBlock);
    SootClass gc_object_visitor_soot_class = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.Serializer");
    SootClass mem_cls = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    m_Bcl.top().startMethod("getSerializer", gc_object_visitor_soot_class.getType(), mem_cls.getType(), mem_cls.getType());
    Local param0 = m_Bcl.top().refParameter(0);
    Local param1 = m_Bcl.top().refParameter(1);
    Local ret = m_Bcl.top().newInstance(mClassName, param0, param1);
    m_Bcl.top().returnValue(ret);
    m_Bcl.top().endMethod();
  }

  private void makeCtor() {
    SootClass mem_cls = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");

    m_Bcl.top().startMethod("<init>", VoidType.v(), mem_cls.getType(), mem_cls.getType());
    Local this_ref = m_Bcl.top().refThis();
    Local param0 = m_Bcl.top().refParameter(0);
    Local param1 = m_Bcl.top().refParameter(1);
    m_Bcl.top().pushMethod("edu.syr.pcpratts.rootbeer.runtime.Serializer", "<init>", VoidType.v(), mem_cls.getType(), mem_cls.getType());
    m_Bcl.top().invokeMethodNoRet(this_ref, param0, param1);
    m_Bcl.top().returnVoid();
    m_Bcl.top().endMethod();
  }

  private void generateSentinalCtor(RefType ref_type) {
    SootClass soot_class = ref_type.getSootClass();
    if(mSentinalCtorsCreated.contains(soot_class.getName()))
      return;
    mSentinalCtorsCreated.add(soot_class.getName());
    
    soot_class = RootbeerScene.v().getClass(soot_class.getName());
    if(soot_class.isLibraryClass())
      return;
    
    if(soot_class.declaresMethod("void <init>(edu.syr.pcpratts.rootbeer.runtime.Sentinal)")){
      return; 
    }
    
    SootClass parent_class = soot_class.getSuperclass();
    parent_class = RootbeerScene.v().getClass(parent_class.getName());

    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(soot_class);
    bcl.startMethod("<init>", VoidType.v(), RefType.v("edu.syr.pcpratts.rootbeer.runtime.Sentinal"));
    Local thisref = bcl.refThis();

    String parent_name = parent_class.getName();
    if(parent_class.isLibraryClass()){    
      if(parent_class.declaresMethod("void <init>()")){
        bcl.pushMethod(parent_name, "<init>", VoidType.v());
        bcl.invokeMethodNoRet(thisref);
      } else {
        System.out.println("Library class "+parent_name+" on the GPU does not have a void constructor");
        System.exit(-1);
      }
    } else {
      bcl.pushMethod(parent_name, "<init>", VoidType.v(), RefType.v("edu.syr.pcpratts.rootbeer.runtime.Sentinal"));
      bcl.invokeMethodNoRet(thisref, NullConstant.v());
    }
    bcl.returnVoid();
    bcl.endMethod();
  }

  private void makeSentinalCtors() {
    List<Type> orderedHistory = OpenCLScene.v().getOrderedHistory();
    for(int i = orderedHistory.size() - 1; i >= 0; --i){
     Type type = orderedHistory.get(i);
      if(type instanceof RefType){
        RefType ref_type = (RefType) type;
        AcceptableGpuTypes accept = new AcceptableGpuTypes();
        if(accept.shouldGenerateCtor(ref_type.getClassName()))
          generateSentinalCtor(ref_type);
      }
    }
  }

  String getClassName() {
    return mClassName;
  }  
}
