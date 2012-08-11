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

package edu.syr.pcpratts.rootbeer.generate.opencl.fields;

import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.generate.bytecode.StaticOffsets;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLType;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.CompositeField;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OffsetCalculator;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import soot.Local;
import soot.Modifier;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.Value;
import soot.jimple.toolkits.typing.fast.Integer127Type;

public class OpenCLField {
  private final SootField m_SootField;
  private final SootClass m_SootClass;
  private boolean m_CheckAlignment;
  private boolean m_Cloned;
  private OpenCLField m_CloneSource;
  private Map<Integer, List<SootClass>> m_Offsets;

  public OpenCLField(SootField soot_field, SootClass soot_class) {
    m_SootField = soot_field;
    m_SootClass = soot_class;
    m_CheckAlignment = false;
    m_Cloned = false;    
  }
  
  public void setClone(OpenCLField source){
    m_Cloned = true;
    m_CloneSource = source;
  }
  
  public boolean isCloned(){
    return m_Cloned;
  }  
  
  public OpenCLField getCloneSource(){
    return m_CloneSource;
  }
  
  public String getName(){
    return m_SootField.getName();
  }

  public SootField getSootField(){
    return m_SootField;
  }

  private String getFullName(){
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(m_SootClass);
    return ocl_class.getName()+"_"+getName();
  }
  
  public OpenCLType getClassType(){
    Type soot_type = m_SootClass.getType();
    return new OpenCLType(soot_type);
  }

  public OpenCLType getType(){
    Type soot_type = m_SootField.getType();
    return new OpenCLType(soot_type);
  }

  private List<String> getDecls(){
    List<String> ret = new ArrayList<String>();
    String type_string = getType().getRefString();
    String device_function_qual = Tweaks.v().getDeviceFunctionQualifier();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    if(m_SootField.isStatic() == false){
      //instance getter
      ret.add(device_function_qual+" "+type_string+" instance_getter_"+getFullName()+"("+address_qual+" char * gc_info, int thisref, int * exception)");
      //instance setter
      ret.add(device_function_qual+" void instance_setter_"+getFullName()+"("+address_qual+" char * gc_info, int thisref, "+type_string+" parameter0, int * exception)");
    } else {
      //static getter
      ret.add(device_function_qual+" "+type_string+" static_getter_"+getFullName()+"("+address_qual+" char * gc_info, int * exception)");
      //static setter
      ret.add(device_function_qual+" void static_setter_"+getFullName()+"("+address_qual+" char * gc_info, "+type_string+" parameter0, int * expcetion)");
    }
    return ret;
  }

  public String getGetterSetterPrototypes(){
    StringBuilder ret = new StringBuilder();
    List<String> decls = getDecls();
    for(String decl : decls){
      ret.append(decl+";\n");
    }
    return ret.toString();
  }
  
  public int getSize(){
    return getType().getSize();
  }

  private void calculateOffsets(CompositeField composite){    
    OffsetCalculator calc = new OffsetCalculator(composite);
    m_Offsets = new TreeMap<Integer, List<SootClass>>();
    for(SootClass sclass : composite.getClasses()){
      int field_offset = calc.getOffset(this, sclass);
      List<SootClass> classes;
      if(m_Offsets.containsKey(field_offset)){
        classes = m_Offsets.get(field_offset);
      } else {
        classes = new ArrayList<SootClass>();
        m_Offsets.put(field_offset, classes);
      }
      classes.add(sclass);
    }
  }
  
  private int getOnlyOffset(){
    Iterator<Integer> iter = m_Offsets.keySet().iterator();
    while(iter.hasNext()){
      int ret = iter.next();
      return ret;
    }
    return -1;
  }
  
  private String getGetterSetterBodiesInstance(CompositeField composite, boolean writable,
    FieldTypeSwitch type_switch){
    
    StringBuilder ret = new StringBuilder();
    String var_type_string = getType().getRefString();
    List<String> decls = getDecls();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    
    String cast_string = getCastString();
    
    calculateOffsets(composite);
    String type = getType().getRefString();
    //instance getter
    ret.append(decls.get(0)+"{\n");
    int field_offset = getOnlyOffset();
    //ret.append("if("+thisref+" & 0x1000000000000000L){\n");
    //ret.append("  thisref &= 0x0fffffffffffffffL;\n");
    //ret.append("  thisref += "+field_offset+";\n");
    //ret.append("  return edu_syr_pcpratts_cache_get_"+type+"(thisref);\n");
    //ret.append("} else {\n");   
    ret.append("if(thisref == -1){\n");
    ret.append("  *exception = "+Constants.NullPointerNumber+";\n");
    ret.append("  return 0;\n");
    ret.append("}\n");
    ret.append(address_qual+" char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n");
    if(composite.getClasses().size() == 1){
      SootClass sclass = composite.getClasses().get(0);
      ret.append("return *(("+address_qual+" "+cast_string+" *) &thisref_deref["+Integer.toString(field_offset)+"]);\n");
    } else {
      ret.append("GC_OBJ_TYPE_TYPE derived_type = edu_syr_pcpratts_gc_get_type(thisref_deref);\n");
      ret.append("int offset;\n");
      ret.append("offset = "+type_switch.typeSwitchName(m_Offsets)+"(derived_type);\n");
      ret.append("return *(("+address_qual+" "+cast_string+" *) &thisref_deref[offset]);\n");
    }
    //ret.append("}\n");
    ret.append("}\n");
    //instance setter
    ret.append(decls.get(1)+"{\n");
    ret.append("if(thisref == -1){\n");
    ret.append("  *exception = "+Constants.NullPointerNumber+";\n");
    ret.append("  return;\n");
    ret.append("}\n");
    ret.append(address_qual+" char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n");    
    if(composite.getClasses().size() == 1){
      SootClass sclass = composite.getClasses().get(0);  
      if(getType().isRefType()){
        ret.append("edu_syr_pcpratts_gc_assign_global(gc_info, ("+address_qual+" "+cast_string+" *) &thisref_deref["+Integer.toString(field_offset)+"], parameter0);\n");
      } else {
        ret.append("*(("+address_qual+" "+cast_string+" *) &thisref_deref["+Integer.toString(field_offset)+"]) = parameter0;\n");
      }
    } else {
      ret.append("GC_OBJ_TYPE_TYPE derived_type = edu_syr_pcpratts_gc_get_type(thisref_deref);\n");
      ret.append("int offset;\n");
      ret.append("offset = "+type_switch.typeSwitchName(m_Offsets)+"(derived_type);\n");     
      if(getType().isRefType()){
        ret.append("edu_syr_pcpratts_gc_assign_global(gc_info, ("+address_qual+" "+cast_string+" *) &thisref_deref[offset], parameter0);\n");
      } else {
        ret.append("*(("+address_qual+" "+cast_string+" *) &thisref_deref[offset]) = parameter0;\n");
      }
    }
    ret.append("}\n");

    return ret.toString();
  }
  
  private String getGetterSetterBodiesStatic() {
    StringBuilder ret = new StringBuilder();
    List<String> decls = getDecls();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    String var_type_string = getType().getRefString();
    StaticOffsets static_offsets = new StaticOffsets();
    int offset = static_offsets.getIndex(this);
    
    String cast_string = getCastString();
    
    ret.append(decls.get(0)+"{\n");
    ret.append(address_qual+" char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, 0);\n");
    ret.append("return *(("+address_qual+" "+cast_string+" *) &thisref_deref["+offset+"]);\n");
    ret.append("}\n");
    
    ret.append(decls.get(1)+"{\n");    
    ret.append(address_qual+" char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, 0);\n");
    ret.append("*(("+address_qual+" "+cast_string+" *) &thisref_deref["+offset+"]) = parameter0;\n");
    ret.append("}\n");
    return ret.toString();
  }
  
  public String getGetterSetterBodies(CompositeField composite, boolean writable,
    FieldTypeSwitch type_switch){
    
    if(m_SootField.isStatic()){
      return getGetterSetterBodiesStatic();
    } else {
      return getGetterSetterBodiesInstance(composite, writable, type_switch);
    }    
  }
  
  private String checkAlignmentString(int field_offset){
    String ret = "";
    ret += "int addr = thisref_deref + "+field_offset+";\n";
    ret += "if(addr % "+getSize()+" != 0){\n";
    ret += "  printf(\"misaligned field: "+m_SootField.toString()+"\\n\");\n";
    ret += "}\n";
    return ret;
  }
  
  public String getStaticGetterInvoke(){
    return "static_getter_"+getFullName()+"(gc_info, exception)";
  }

  public String getStaticSetterInvoke(){
    return "static_setter_"+getFullName()+"(gc_info";
  }

  public String getInstanceGetterInvoke(Value base){
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("how do we handle when a base is not a loca?");
    Local local = (Local) base;
    return "instance_getter_"+getFullName()+"(gc_info, "+local.getName()+", exception)";
  }

  public String getInstanceSetterInvoke(Value base){
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("how do we handle when a base is not a loca?");
    Local local = (Local) base;
    return "instance_setter_"+getFullName()+"(gc_info, "+local.getName();
  }
  
  public String getInstanceSetterInvokeWithoutThisref(){
    return "instance_setter_"+getFullName()+"(gc_info, ";
  }

  @Override
  public String toString(){
    return getType().getDerefString()+" "+getName();
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof OpenCLField == false)
      return false;
    OpenCLField other = (OpenCLField) o;
    if(m_SootField.equals(other.m_SootField) && m_SootClass.equals(other.m_SootClass))
      return true;
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + (this.m_SootField != null ? this.m_SootField.hashCode() : 0);
    hash = 37 * hash + (this.m_SootClass != null ? this.m_SootClass.hashCode() : 0);
    return hash;
  }

  public boolean isInstance() {
    if(m_SootField.isStatic())
      return false;
    return true;
  }

  private String getCastString() {
    String ret = getType().getRefString();;
    if(ret.equals("boolean")){
      ret = "char";
    }
    if(ret.equals("byte")){
      ret = "char";
    }
    return ret;
  }
}
