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

import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.util.List;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;

/**
 * Represents an OpenCL function that dispatches to the real OpenCL function
 * implementing the behavior of a certain classes version of a virtual method.
 * @author pcpratts
 */
public class OpenCLPolymorphicMethod {
  private final SootMethod mSootMethod;

  //for hashcode
  private List<SootClass> mHierarchy;

  public OpenCLPolymorphicMethod(SootMethod soot_method){
    mSootMethod = soot_method;
  }

  public String getMethodPrototype(){
    if(mSootMethod.getName().equals("<init>"))
      return "";
    return getMethodDecl()+";\n";
  }

  private String getMethodDecl(){
    List<SootClass> hierarchy = getHierarchy();
    SootClass soot_class = hierarchy.get(0);
    OpenCLMethod ocl_method = new OpenCLMethod(mSootMethod, soot_class);

    StringBuilder ret = new StringBuilder();
    ret.append(Tweaks.v().getDeviceFunctionQualifier()+" ");
    ret.append(ocl_method.getReturnString());
    ret.append(" invoke_"+ocl_method.getPolymorphicName());
    ret.append(ocl_method.getArgumentListStringPolymorphic());
    return ret.toString();
  }

  public String getMethodBody(){
    if(mSootMethod.getName().equals("<init>"))
      return "";
    List<SootClass> hierarchy = getHierarchy();
    SootClass first_soot_class = hierarchy.get(0);

    StringBuilder ret = new StringBuilder();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    //write function signature
    ret.append(getMethodDecl());
    ret.append("{\n");

    if(mSootMethod.isStatic()){
      if(mSootMethod.getReturnType() instanceof VoidType == false){
        ret.append("return ");
      }
      String invoke_string = getStaticInvokeString(first_soot_class);
      ret.append(invoke_string+"\n");
    } else {
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = -2;\n");
      ret.append("return ");
      if(mSootMethod.getReturnType() instanceof VoidType == false)
        ret.append("-1");
      ret.append(";\n");
      ret.append("}\n");
      ret.append(address_qual+" char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n");
      if(sizeHierarchy(hierarchy) == 1){
        SootClass sclass = getSingleMethodInHierarchy(hierarchy);
        String invoke_string = getInvokeString(sclass);
        if(mSootMethod.getReturnType() instanceof VoidType == false){
          ret.append("return ");
        }
        ret.append(invoke_string+"\n");
      } else {
        ret.append("GC_OBJ_TYPE_TYPE derived_type = edu_syr_pcpratts_gc_get_type(thisref_deref);\n");
        ret.append("if(0){}\n");
        int count = 0;
        for(SootClass sclass : hierarchy){
          if(sootClassHasMethod(sclass) == false)
            continue;
          ret.append("else if(derived_type == "+OpenCLScene.v().getClassType(sclass)+"){\n");
          if(mSootMethod.getReturnType() instanceof VoidType == false){
            ret.append("return ");
          }
          String invoke_string = getInvokeString(sclass);
          ret.append(invoke_string+"\n");
          ret.append("}\n");
          count++;
        }
      }
    }
    ret.append("return ");
    if(mSootMethod.getReturnType() instanceof VoidType == false)
      ret.append("-1");
    ret.append(";\n");
    ret.append("}\n");
    return ret.toString();
  }

  //used to invoke polymorphic method inside this function
  private String getInvokeString(SootClass soot_class){
    if(mSootMethod.getName().equals("<init>"))
      return "";
    OpenCLMethod ocl_method = new OpenCLMethod(mSootMethod, soot_class);
    String ret = ocl_method.getPolymorphicName() + "(";

    //write the gc_info and thisref
    ret += "gc_info, thisref";
    List args = mSootMethod.getParameterTypes();
    if(args.size() != 0)
      ret += ", ";

    for(int i = 0; i < args.size(); ++i){
      ret += "parameter" + Integer.toString(i);
      if(i < args.size() - 1)
        ret += ", ";
    }
    ret += ", exception);";
    return ret;
  }

  private String getStaticInvokeString(SootClass soot_class) {
    if(mSootMethod.getName().equals("<init>"))
      return "";
    OpenCLMethod ocl_method = new OpenCLMethod(mSootMethod, soot_class);
    String ret = ocl_method.getPolymorphicName() + "(";

    //write the gc_info and thisref
    ret += "gc_info";
    List args = mSootMethod.getParameterTypes();
    if(args.size() != 0)
      ret += ", ";

    for(int i = 0; i < args.size(); ++i){
      ret += "parameter" + Integer.toString(i);
      if(i < args.size() - 1)
        ret += ", ";
    }
    ret += ", exception);";
    return ret;
  }

  private List<SootClass> getHierarchy(){
    SootClass soot_class = mSootMethod.getDeclaringClass();
    return OpenCLScene.v().getClassHierarchy(soot_class);
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof OpenCLPolymorphicMethod == false)
      return false;
    OpenCLPolymorphicMethod other = (OpenCLPolymorphicMethod) o;
    if(mSootMethod.getName().equals(other.mSootMethod.getName()) == false)
      return false;    
    if(getHierarchy().equals(other.getHierarchy()))
      return true;
    return false;
  }

  @Override
  public int hashCode() {
    mHierarchy = getHierarchy();
    int hash = 5;
    hash = 53 * hash + (this.mSootMethod != null ? this.mSootMethod.hashCode() : 0);
    hash = 53 * hash + (this.mHierarchy != null ? this.mHierarchy.hashCode() : 0);
    return hash;
  }

  private boolean sootClassHasMethod(SootClass sclass) {
    try {
      SootMethod soot_method = sclass.getMethod(mSootMethod.getSubSignature());
      return true;
    } catch(Exception ex){
      return false;
    }
  }

  private int sizeHierarchy(List<SootClass> hierarchy) {
    int ret = 0;
    for(SootClass sclass : hierarchy){
      if(sootClassHasMethod(sclass) == false)
        continue;
      ret++;
    }
    return ret;
  }

  private SootClass getSingleMethodInHierarchy(List<SootClass> hierarchy) {
    for(SootClass sclass : hierarchy){
      if(sootClassHasMethod(sclass) == false)
        continue;
      return sclass;
    }
    return null;
  }

}
