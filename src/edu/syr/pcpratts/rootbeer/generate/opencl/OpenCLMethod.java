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

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.StaticOffsets;
import edu.syr.pcpratts.rootbeer.generate.opencl.body.MethodJimpleValueSwitch;
import edu.syr.pcpratts.rootbeer.generate.opencl.body.OpenCLBody;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;

/**
 * Represents an OpenCL function. 
 * @author pcpratts
 */
public class OpenCLMethod {
  private final SootMethod m_SootMethod;
  private SootClass m_SootClass;
  private Set<String> m_DontMangleMethods;
  
  public OpenCLMethod(SootMethod soot_method, SootClass soot_class){
    m_SootMethod = soot_method;
    m_SootClass = soot_class;
    createDontMangleMethods();
  }
  
  public String getReturnString(){
    StringBuilder ret = new StringBuilder();
    if(isConstructor()){
      ret.append("int");
    } else {
      OpenCLType return_type = new OpenCLType(m_SootMethod.getReturnType());
      ret.append(return_type.getRefString());
    }
    return ret.toString();
  }
  
  private String getRestOfArgumentListStringInternal(){
    StringBuilder ret = new StringBuilder();
    List args = m_SootMethod.getParameterTypes();
    
    if(args.size() != 0)
      ret.append(", ");
    
    for(int i = 0; i < args.size(); ++i){
      Type curr_arg = (Type) args.get(i);
      OpenCLType parameter_type = new OpenCLType(curr_arg);
      ret.append(parameter_type.getRefString());
      ret.append(" parameter" + Integer.toString(i));
      if(i < args.size()-1)
        ret.append(", ");
    }
    ret.append(", int * exception");
    ret.append(")");
    return ret.toString();
  }
  
  private String getArgumentListStringInternal(boolean override_ctor){
    StringBuilder ret = new StringBuilder();
    ret.append("(");

    String address_space_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    if(isConstructor() == true){
      ret.append(address_space_qual+" char * gc_info");
    } else if((isConstructor() == false || override_ctor == true) && m_SootMethod.isStatic() == false){
      ret.append(address_space_qual+" char * gc_info, int thisref");
    } else {
      ret.append(address_space_qual+" char * gc_info");
    }
    
    ret.append(getRestOfArgumentListStringInternal());
    return ret.toString();
  }

  public String getArgumentListString(boolean ctor_body){
    if(ctor_body){
      String address_space_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
      String ret = "("+address_space_qual+" char * gc_info, int thisref";
      ret += getRestOfArgumentListStringInternal();
      return ret;
    } else {
      return getArgumentListStringInternal(false);
    }
  }

  public String getArgumentListStringPolymorphic(){
    return getArgumentListStringInternal(true);
  }

  private String getMethodDecl(boolean ctor_body){
    StringBuilder ret = new StringBuilder();
    ret.append(Tweaks.v().getDeviceFunctionQualifier()+" ");
    if(ctor_body){
      ret.append("void");
    } else {
      ret.append(getReturnString());
    }
    ret.append(" ");
    ret.append(getPolymorphicNameInternal(ctor_body));
    ret.append(getArgumentListString(ctor_body));
    return ret.toString();
  }
  
  public String getMethodPrototype(){
    String ret = getMethodDecl(false)+";\n";
    if(isConstructor()){
      ret += getMethodDecl(true)+";\n";
    }
    return ret;
  }

  private boolean isLinux(){
    String s = File.separator;
    if(s.equals("/")){
      return true;
    }
    return false;
  }
  
  private String synchronizedEnter(){
    String ret = "";
    ret += "int id = getThreadId();\n";
    StaticOffsets static_offsets = new StaticOffsets();
    int junk_index = static_offsets.getEndIndex() - 4;
    int mystery_index = junk_index - 4;
    if(m_SootMethod.isStatic()){
      int offset = static_offsets.getIndex(m_SootClass);
      ret += "char * mem = edu_syr_pcpratts_gc_deref(gc_info, 0);\n";
      ret += "char * trash = mem + "+junk_index+";\n";
      ret += "char * mystery = mem + "+mystery_index+";\n";
      ret += "mem += "+offset+";\n";
    } else {
      ret += "char * mem = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n";
      ret += "char * trash = edu_syr_pcpratts_gc_deref(gc_info, 0) + "+junk_index+";\n";
      ret += "char * mystery = trash - 4;\n";
      ret += "mem += 12;\n";
    }
    ret += "int count = 0;\n";
    ret += "int old;\n";
    ret += "while(count < 100){\n";
    ret += "  old = atomicCAS((int *) mem, -1 , id);\n";
    ret += "  *((int *) trash) = old;\n";
    if(isLinux()){
      ret += "  if(old == -1 || old == id){\n";
    } else {
      ret += "  if(old != -1 && old != id){\n";
      ret += "    count++;\n";
      ret += "    if(count > 99 && *((int *) mystery) == 0){\n";
      ret += "      count = 0;\n";
      ret += "    }\n";
      ret += "  } else {\n"; 
    }
    return ret;
  }
  
  public String getMethodBody(){
    StringBuilder ret = new StringBuilder();
    if(shouldEmitBody()){
      ret.append(getMethodDecl(false)+"{\n");
      try {
        if(methodIsRuntimeBasicBlockRun() == false){
          if(isSynchronized()){
            ret.append(synchronizedEnter()); 
          }
          OpenCLBody ocl_body = new OpenCLBody(m_SootMethod, isConstructor());
          ret.append(ocl_body.getBody());
          if(isSynchronized()){
            if(isLinux()){
              ret.append("  } else {");
              ret.append("    count++;\n");
              ret.append("    if(count > 99 && *((int *) mystery) == 0){\n");
              ret.append("      count = 0;\n");
              ret.append("    }\n");
              ret.append("  }\n"); 
              ret.append("}\n"); 
            } else {
              ret.append("  }\n");
              ret.append("}\n");
            }
          }
          if(returnsAValue()){
            ret.append("return 0;");
          }
        }
      } catch(RuntimeException ex){
        System.out.println("Error creating method body: "+m_SootMethod.getSignature());
        OpenCLMethod ocl_method = new OpenCLMethod(m_SootMethod, m_SootClass);
        if(ocl_method.returnsAValue())
          ret.append("return 0;\n");
        else
          ret.append("\n");
      }
      ret.append("}\n");
      if(isConstructor()){
        ret.append(getMethodDecl(true)+"{\n"); 
        OpenCLBody ocl_body = new OpenCLBody(RootbeerScene.v().getBody(m_SootMethod));
        ret.append(ocl_body.getBody());
        ret.append("}\n");
      }
    }
    return ret.toString();
  }
  
  public String getConstructorBodyInvokeString(SpecialInvokeExpr arg0){
    StringBuilder ret = new StringBuilder();

    ret.append(getPolymorphicNameInternal(true) +"(");
    List args = arg0.getArgs();
    List<String> args_list = new ArrayList<String>();
    args_list.add("gc_info");
    args_list.add("thisref");
    
    for(int i = 0; i < args_list.size() - 1; ++i){
      ret.append(args_list.get(i));
      ret.append(",\n ");
    }
    if(args_list.size() > 0){
      ret.append(args_list.get(args_list.size()-1));
      if(args.size() > 0)
        ret.append(",\n ");
    }
    
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    for(int i = 0; i < args.size(); ++i){
      Value arg = (Value) args.get(i);
      arg.apply(quick_value_switch);
      if(i < args.size() - 1)
        ret.append(",\n ");
    }
    ret.append(", exception");
    ret.append(")");
    
    return ret.toString();
  }

  public String getInstanceInvokeString(InstanceInvokeExpr arg0){
    Value base = arg0.getBase();
    Type base_type = base.getType();
    List<SootClass> hierarchy;
    if(base_type instanceof ArrayType){
      hierarchy = new ArrayList<SootClass>();
      SootClass obj = RootbeerScene.v().getClass("java.lang.Object");
      hierarchy.add(obj);
    } else if (base_type instanceof RefType){
      RefType ref_type = (RefType) base_type;
      hierarchy = OpenCLScene.v().getClassHierarchy(ref_type.getSootClass());
    } else {
      throw new UnsupportedOperationException("how do we handle this case?");
    }

    if(hierarchy.size() == 1 || isConstructor() || arg0 instanceof SpecialInvokeExpr){
      return writeInstanceInvoke(arg0, "", m_SootClass);
    } else if(hierarchy.size() == 0){
      System.out.println("size = 0");
      return null;
    } else {
      return writeInstanceInvoke(arg0, "invoke_", hierarchy.get(0));
    } 
  }

  public String getStaticInvokeString(StaticInvokeExpr expr){
    StringBuilder ret = new StringBuilder();

    ret.append(getPolymorphicName()+"(");
    List args = expr.getArgs();
    List<String> args_list = new ArrayList<String>();
    args_list.add("gc_info");

    for(int i = 0; i < args_list.size() - 1; ++i){
      ret.append(args_list.get(i));
      ret.append(", ");
    }
    if(args_list.size() > 0){
      ret.append(args_list.get(args_list.size()-1));
      if(args.size() > 0)
        ret.append(", ");
    }
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    for(int i = 0; i < args.size(); ++i){
      Value arg = (Value) args.get(i);
      arg.apply(quick_value_switch);
      if(i < args.size() - 1)
        ret.append(", ");
    }
    ret.append(", exception");
    ret.append(")");
    return ret.toString();
  }

  private String writeInstanceInvoke(InstanceInvokeExpr arg0, String method_prefix, SootClass soot_class){
    OpenCLMethod corrected_this = new OpenCLMethod(m_SootMethod, soot_class);
    StringBuilder ret = new StringBuilder();
    Value base = arg0.getBase();
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("How do we handle an invoke on a non loca?");
    Local local = (Local) base;
    if(isConstructor()){
      ret.append("edu_syr_pcpratts_gc_assign (gc_info, \n&"+local.getName()+", ");
    }

    ret.append(method_prefix+corrected_this.getPolymorphicName()+"(");
    List args = arg0.getArgs();
    List<String> args_list = new ArrayList<String>();
    args_list.add("gc_info");
    
    //write the thisref
    if(isConstructor() == false)
      args_list.add(local.getName());

    for(int i = 0; i < args_list.size() - 1; ++i){
      ret.append(args_list.get(i));
      ret.append(",\n ");
    }
    if(args_list.size() > 0){
      ret.append(args_list.get(args_list.size()-1));
      if(args.size() > 0)
        ret.append(",\n ");
    }
    
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    for(int i = 0; i < args.size(); ++i){
      Value arg = (Value) args.get(i);
      arg.apply(quick_value_switch);
      if(i < args.size() - 1)
        ret.append(",\n ");
    }
    ret.append(", exception");
    ret.append(")");
    
    if(isConstructor()){
      ret.append(")");
    }

    return ret.toString();
  }

  public boolean isConstructor(){
    String method_name = m_SootMethod.getName();
    if(method_name.equals("<init>"))
      return true;
    return false;
  }
  
  public String getPolymorphicName(){
    return getPolymorphicNameInternal(false);
  }
  
  private String getPolymorphicNameInternal(boolean ctor_body){
    String ret = getBaseMethodName();
    if(ctor_body){
      ret += "_body";  
    }
    if(m_DontMangleMethods.contains(ret) == false)
      ret += NameMangling.v().mangleArgs(m_SootMethod);
    return ret;
  }

  private String getBaseMethodName(){
    OpenCLClass ocl_class = new OpenCLClass(m_SootClass);

    String method_name = m_SootMethod.getName();
    //here I use a certain uuid for init so there is low chance of collisions
    method_name = method_name.replace("<init>", "init"+OpenCLScene.v().getUuid());

    String ret = ocl_class.getName()+"_"+method_name;
    return ret;
  }
  
  private boolean shouldEmitBody(){
    String ret = getBaseMethodName();
    if(m_DontMangleMethods.contains(ret))
      return false;
    return true;
  }
  
  @Override
  public String toString(){
    return getPolymorphicName();
  }

  public void findAllUsedMethodsAndFields() {
    Body body;
    try {
      body = RootbeerScene.v().getBody(m_SootMethod);
      if(body == null)
        return;
    } catch(RuntimeException ex){
      //if there is not body, return.
      return;
    }
    PatchingChain<Unit> units = body.getUnits();
    Iterator<Unit> iter = units.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      List<ValueBox> boxes = next.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        FindMethodsFieldsAndArrayTypes.methods(value);
        FindMethodsFieldsAndArrayTypes.fields(value);
      }
    }
  }
  public List<SootClass> getHierarchy(){
    SootClass soot_class = m_SootMethod.getDeclaringClass();
    return OpenCLScene.v().getClassHierarchy(soot_class);
  }
  
  public void findAllUsedArrayTypes() {
    SootClass soot_class = m_SootMethod.getDeclaringClass();

    Body body;
    try {
      body = m_SootMethod.getActiveBody();
    } catch(RuntimeException ex){
      //if there is no body, return.
      return;
    }
    PatchingChain<Unit> units = body.getUnits();
    Iterator<Unit> iter = units.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      List<ValueBox> boxes = next.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        FindMethodsFieldsAndArrayTypes.arrayTypes(value);
      }
    }
  }

  private boolean methodIsRuntimeBasicBlockRun() {
    if(m_SootClass.getName().equals("edu.syr.pcpratts.javaautogpu.runtime.RuntimeBasicBlock") == false)
      return false;
    if(m_SootMethod.getName().equals("run") == false)
      return false;
    return true;
  }

  public boolean returnsAValue() {
    if(isConstructor())
      return true;
    Type t = m_SootMethod.getReturnType();
    if(t instanceof VoidType)
      return false;
    return true;
  }

  public boolean isSynchronized() {
    return m_SootMethod.isSynchronized();
  }
  
  private void createDontMangleMethods() {
    m_DontMangleMethods = new HashSet<String>();
    m_DontMangleMethods.add("java_lang_StrictMath_exp");
    m_DontMangleMethods.add("java_lang_StrictMath_log");
    m_DontMangleMethods.add("java_lang_StrictMath_log10");
    m_DontMangleMethods.add("java_lang_StrictMath_sqrt");
    m_DontMangleMethods.add("java_lang_StrictMath_cbrt");
    m_DontMangleMethods.add("java_lang_StrictMath_IEEEremainder");    
    m_DontMangleMethods.add("java_lang_StrictMath_ceil");
    m_DontMangleMethods.add("java_lang_StrictMath_floor");
    m_DontMangleMethods.add("java_lang_StrictMath_sin");
    m_DontMangleMethods.add("java_lang_StrictMath_cos");
    m_DontMangleMethods.add("java_lang_StrictMath_tan");
    m_DontMangleMethods.add("java_lang_StrictMath_asin");
    m_DontMangleMethods.add("java_lang_StrictMath_acos");
    m_DontMangleMethods.add("java_lang_StrictMath_atan");
    m_DontMangleMethods.add("java_lang_StrictMath_atan2");
    m_DontMangleMethods.add("java_lang_StrictMath_pow");
    m_DontMangleMethods.add("java_lang_StrictMath_sinh");
    m_DontMangleMethods.add("java_lang_StrictMath_cosh");
    m_DontMangleMethods.add("java_lang_StrictMath_tanh");
    m_DontMangleMethods.add("java_lang_Double_doubleToLongBits");
    m_DontMangleMethods.add("java_lang_Double_longBitsToDouble");
    m_DontMangleMethods.add("java_lang_System_arraycopy");
    m_DontMangleMethods.add("java_lang_Throwable_fillInStackTrace");
    m_DontMangleMethods.add("java_lang_Throwable_getStackTraceDepth");
    m_DontMangleMethods.add("java_lang_Throwable_getStackTraceElement");
    m_DontMangleMethods.add("java_lang_Object_clone");
    m_DontMangleMethods.add("java_lang_OutOfMemoryError_initab850b60f96d11de8a390800200c9a66");
    m_DontMangleMethods.add("edu_syr_pcpratts_rootbeer_runtime_RootbeerGpu_isOnGpu");
    m_DontMangleMethods.add("edu_syr_pcpratts_rootbeer_runtime_RootbeerGpu_getThreadId");
  }


}