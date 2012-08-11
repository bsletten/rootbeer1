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

import edu.syr.pcpratts.rootbeer.generate.bytecode.Constants;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import soot.SootClass;

public class OffsetCalculator {
 
  private Map<SootClass, Map<OpenCLField, Integer>> m_OffsetMap;
  private Map<OpenCLField, Integer> m_CurrOffsetSubMap;
  private int m_CurrOffset;  
  
  public OffsetCalculator(CompositeField composite) {
    m_OffsetMap = new HashMap<SootClass, Map<OpenCLField, Integer>>();
    //classes are sorted with base at index 0, most derived at index size-1
    List<SootClass> classes = composite.getClasses();
    for(int i = classes.size()-1; i >= 0; --i){
      SootClass curr_key_class = classes.get(i);
      m_CurrOffsetSubMap = new HashMap<OpenCLField, Integer>();
      m_CurrOffset = Constants.SizeGcInfo;
      for(int j = i; j >= 0; --j){
        SootClass curr_field_class = classes.get(j);
        List<OpenCLField> fields = composite.getRefFieldsByClass(curr_field_class);
        fields = trimFields(fields, curr_field_class, true);
        calculateForSet(fields);
        alignCurrOffset();
      }
      for(int j = i; j >= 0; --j){
        SootClass curr_field_class = classes.get(j);
        List<OpenCLField> fields = composite.getNonRefFieldsByClass(curr_field_class);
        fields = trimFields(fields, curr_field_class, false);
        calculateForSet(fields);
        alignCurrOffset();
      }
      m_OffsetMap.put(curr_key_class, m_CurrOffsetSubMap);
    }
  }
  
  private void alignCurrOffset(){    
    //non_ref starts on alignment of 8
    int mod = m_CurrOffset % 8;
    if(mod != 0)
      m_CurrOffset += (8 - mod);
  }
  
  private void calculateForSet(List<OpenCLField> sorted){
    if(sorted.isEmpty())
      return;
    
    OpenCLField first = sorted.get(0);
    
    int prev_size = first.getSize();
    
    for(OpenCLField field : sorted){
      
      if(field.isCloned())
        continue;
      
      if(prev_size != field.getSize()){
        int mod = m_CurrOffset % prev_size;
        if(mod != 0)
          m_CurrOffset += (prev_size - mod);
        prev_size = field.getSize();
      }
      m_CurrOffsetSubMap.put(field, m_CurrOffset);
      m_CurrOffset += field.getSize();
    }
  }

  public int getOffset(OpenCLField field, SootClass soot_class) {
    Map<OpenCLField, Integer> offset_map = m_OffsetMap.get(soot_class);
    if(field.isCloned()){
      if(offset_map.containsKey(field.getCloneSource())){
        return offset_map.get(field.getCloneSource());
      } else {
        return -1;
      }
    } else {
      if(offset_map.containsKey(field)){
        return offset_map.get(field);
      } else {
        return -1;
      }
    }
  }

  public int getSize(SootClass soot_class) {
    int ret = Constants.SizeGcInfo;
    Map<OpenCLField, Integer> offset_map = m_OffsetMap.get(soot_class);
    OpenCLField max_field = null;
    int max_offset = Integer.MIN_VALUE;
    Iterator<OpenCLField> iter = offset_map.keySet().iterator();
    while(iter.hasNext()){
      OpenCLField field = iter.next();
      int offset = offset_map.get(field);
      if(offset > max_offset){
        max_offset = offset;
        max_field = field;
      }
    }
    if(max_field == null)
      return ret;
    ret = max_offset;
    ret += max_field.getSize();
    int mod = ret % 16;
    if(mod != 0){
      ret += (16 - mod);
    }
    return ret;
  }

  private List<OpenCLField> trimFields(List<OpenCLField> org_fields, SootClass curr_field_class, boolean ref_fields) {
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(curr_field_class);
    List<OpenCLField> used_fields;
    if(ref_fields){
      used_fields = ocl_class.getInstanceRefFields();  
    } else {
      used_fields = ocl_class.getInstanceNonRefFields();
    }
    List<OpenCLField> ret = new ArrayList<OpenCLField>();
    for(OpenCLField field : org_fields){
      if(listContains(used_fields, field.getName()))
        ret.add(field);
    }    
    return ret;
  }
  
  private boolean listContains(List<OpenCLField> lst, String name){
    for(OpenCLField field : lst){
      if(field.getName().equals(name))
        return true;
    }
    return false;
  }
  
}
