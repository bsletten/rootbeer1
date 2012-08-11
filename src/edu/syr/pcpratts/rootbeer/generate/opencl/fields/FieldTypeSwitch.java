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

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.SootClass;

public class FieldTypeSwitch {

  private Map<String, String> m_BodyToNameMap;
  private int m_CurrFunctionNum;
  
  public FieldTypeSwitch(){
    m_BodyToNameMap = new HashMap<String, String>();
    m_CurrFunctionNum = 0;
  }
  
  public String getFunctions() {
    StringBuilder ret = new StringBuilder();
    Iterator<String> iter = m_BodyToNameMap.keySet().iterator();
    while(iter.hasNext()){
      String body = iter.next();
      String function_name = m_BodyToNameMap.get(body);
      String qual = Tweaks.v().getDeviceFunctionQualifier();
      ret.append(qual+" int "+function_name+"(int type){\n");
      ret.append(body);
      ret.append("}\n");
    }
    return ret.toString();
  }

  String typeSwitchName(Map<Integer, List<SootClass>> offsets) {
    String body = produceBody(offsets);
    if(m_BodyToNameMap.containsKey(body)){
      return m_BodyToNameMap.get(body);
    } else {
      String base_name = "edu_syr_pcpratts_type_switch";
      base_name += m_CurrFunctionNum;
      m_CurrFunctionNum++;
      m_BodyToNameMap.put(body, base_name);
      return base_name;
    }
  }

  private String produceBody(Map<Integer, List<SootClass>> offsets) {
    int[] sorted_keys = sortKeys(offsets);
    StringBuilder ret = new StringBuilder();
    ret.append("int offset;\n");
    ret.append("switch(type){\n");
    for(int key : sorted_keys){
      List<SootClass> classes = offsets.get(key);
      classes = sortClasses(classes);
      for(SootClass sclass : classes){
        ret.append(" case "+OpenCLScene.v().getClassType(sclass)+":\n");
      }
      ret.append("  offset = "+key+";\n");
      ret.append("  break;\n");
    }
    ret.append("default:\n");
    ret.append("  offset = -1;\n");
    ret.append("  break;\n");
    ret.append("}\n");
    ret.append("return offset;\n");
    return ret.toString();
  }

  private int[] sortKeys(Map<Integer, List<SootClass>> offsets) {
    int[] array = new int[offsets.size()];
    int index = 0;
    Iterator<Integer> iter = offsets.keySet().iterator();
    while(iter.hasNext()){
      int key = iter.next();
      array[index] = key;
      index++;
    }
    Arrays.sort(array);
    return array;
  }

  private List<SootClass> sortClasses(List<SootClass> classes) {
    WrappedClass[] wrapped_classes = new WrappedClass[classes.size()];
    int index = 0;
    for(SootClass sclass : classes){
      WrappedClass wclass = new WrappedClass(sclass);
      wrapped_classes[index] = wclass;
      index++;
    }
    Arrays.sort(wrapped_classes);
    List<SootClass> ret = new ArrayList<SootClass>();
    for(WrappedClass wrapped : wrapped_classes){
      ret.add(wrapped.getSootClass());
    }    
    return ret;
  }
  
  private class WrappedClass implements Comparable<WrappedClass>{

    private SootClass m_SootClass;
    private int m_DerivedType;
    
    public WrappedClass(SootClass sclass){
      m_SootClass = sclass;
      m_DerivedType = OpenCLScene.v().getClassType(sclass);
    }
    
    public int compareTo(WrappedClass o) {
      return Integer.valueOf(m_DerivedType).compareTo(Integer.valueOf(o.m_DerivedType));
    }
    
    public SootClass getSootClass(){
      return m_SootClass;
    }
  }
}
