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

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.CompositeField;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.FieldCloner;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import soot.RefType;
import soot.SootClass;
import soot.Value;

public class StaticOffsets {
  
  private Map<Integer, SortableField> m_OffsetToFieldMap;
  private Map<OpenCLField, Integer> m_FieldToOffsetMap;
  private Map<SootClass, Integer> m_ClassToOffsetMap;
  private Map<SootClass, List<OpenCLField>> m_StaticFields;
  private int m_EndIndex;
  private int m_LockStart;
  private int m_ZerosSize;
  
  public StaticOffsets(){   
    m_OffsetToFieldMap = new HashMap<Integer, SortableField>();
    m_FieldToOffsetMap = new HashMap<OpenCLField, Integer>();
    m_ClassToOffsetMap = new HashMap<SootClass, Integer>();
    m_StaticFields = new HashMap<SootClass, List<OpenCLField>>();
    buildMaps(); 
  }
  
  public OpenCLField getField(int index){
    return m_OffsetToFieldMap.get(index).m_Field;
  }
  
  public int getIndex(OpenCLField field){
    return m_FieldToOffsetMap.get(field);
  }
  
  public int getIndex(SootClass soot_class){
    return m_ClassToOffsetMap.get(soot_class); 
  }

  public int getEndIndex(){
    return m_EndIndex;
  }
  
  public List<OpenCLField> getStaticFields(SootClass soot_class){
    List<OpenCLField> ret = m_StaticFields.get(soot_class);
    if(ret == null){
      ret = new ArrayList<OpenCLField>();
    }
    return ret;
  }
  
  private void buildMaps() {
    FieldCloner cloner = new FieldCloner();
    cloner.setup(OpenCLScene.v().getClassMap());
    List<CompositeField> composites = cloner.getCompositeFields();
    List<SortableField> sortable_fields = new ArrayList<SortableField>();
    for(CompositeField composite : composites){
      List<SootClass> classes = composite.getClasses();
      for(SootClass soot_class : classes){      
        sortable_fields.addAll(convert(composite.getRefFieldsByClass(soot_class), soot_class));
        sortable_fields.addAll(convert(composite.getNonRefFieldsByClass(soot_class), soot_class));

        List<OpenCLField> static_fields = new ArrayList<OpenCLField>();
        static_fields.addAll(staticFilter(composite.getRefFieldsByClass(soot_class)));
        static_fields.addAll(staticFilter(composite.getNonRefFieldsByClass(soot_class)));
        m_StaticFields.put(soot_class, static_fields);
      }
    }
    SortableField[] array = new SortableField[sortable_fields.size()];
    array = sortable_fields.toArray(array);
    Arrays.sort(array);
    int index = 0;
    for(SortableField field : array){
      m_OffsetToFieldMap.put(index, field);
      m_FieldToOffsetMap.put(field.m_Field, index);
      int size = field.m_Field.getSize();
      index += size;
    }
    int leftover = index % 4;
    if(leftover != 0){
      index += leftover;
    }
    m_LockStart = index;
    Map<String, OpenCLClass> map = OpenCLScene.v().getClassMap();
    Iterator<String> cls_iter = map.keySet().iterator();
    while(cls_iter.hasNext()){
      String curr = cls_iter.next();
      SootClass soot_class = map.get(curr).getSootClass();
      m_ClassToOffsetMap.put(soot_class, index);
      index += 4;
    }
    m_EndIndex = index;
    int mod = m_EndIndex % 16;
    m_ZerosSize = 0;
    if(mod != 0){
      m_EndIndex += (16 - mod);
      m_ZerosSize += (16 - mod);
    }
    //give room for junk space
    m_EndIndex += 16;
    m_ZerosSize += 16;
  }
  
  public int getZerosSize(){
    return m_ZerosSize;
  }

  private List<SortableField> convert(List<OpenCLField> fields, SootClass soot_class) {
    fields = staticFilter(fields);
    List<SortableField> ret = new ArrayList<SortableField>();
    for(OpenCLField field : fields){
      ret.add(new SortableField(field, soot_class));
    }
    return ret;
  }
  
  private List<OpenCLField> staticFilter(List<OpenCLField> fields){
    List<OpenCLField> ret = new ArrayList<OpenCLField>();
    for(OpenCLField field : fields){
      if(field.isInstance() == false)
        ret.add(field);
    }
    return ret;
  }

  public int getClassSize() {
    return m_ClassToOffsetMap.size();
  }

  public int getLockStart() {
    return m_LockStart;
  }
  
  private class SortableField implements Comparable<SortableField> {
    public OpenCLField m_Field;
    public SootClass m_SootClass;
    
    public SortableField(OpenCLField field, SootClass soot_class){
      m_Field = field;
      m_SootClass = soot_class;
    }

    public int compareTo(SortableField o) {
      int this_size = m_Field.getSize();
      int o_size = o.m_Field.getSize();
      return Integer.valueOf(o_size).compareTo(Integer.valueOf(this_size));
    }
  }
}
