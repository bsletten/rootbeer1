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

import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.generate.bytecode.Constants;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OffsetCalculator;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import soot.SootClass;
import soot.SootField;

public class OpenCLClass {
  private final Set<OpenCLMethod> m_Methods;
  private final Set<OpenCLField> mFields;
  private final SootClass m_SootClass;
  private List<OpenCLField> mInstanceRefFields;
  private List<OpenCLField> mInstanceNonRefFields;
  private List<OpenCLField> mAllUsedInstanceFields;
  
  private List<OpenCLField> mStaticRefFields;
  private List<OpenCLField> mStaticNonRefFields;
  private List<OpenCLField> mAllUsedStaticFields;
  
  private boolean mFoundAllUsedMethodsAndFields;
  private int mStaticFieldSize;

  public OpenCLClass(SootClass soot_class){
    m_Methods = new LinkedHashSet<OpenCLMethod>();
    mFields = new LinkedHashSet<OpenCLField>();
    m_SootClass = soot_class;
    mInstanceRefFields = null;
    mInstanceNonRefFields = null;
    mAllUsedInstanceFields = null;
    mStaticRefFields = null;
    mStaticNonRefFields = null;
    mAllUsedStaticFields = null;
    mFoundAllUsedMethodsAndFields = false;
  }
  
  public SootClass getSootClass(){
    return m_SootClass;
  }

  public void calculateStaticFieldSize(){
    mStaticFieldSize = 0;
    determineFieldTypes();
    for(OpenCLField field : mAllUsedInstanceFields){
      SootField soot_field = field.getSootField();
      if(soot_field.isStatic() == false)
        continue;
      OpenCLType type = new OpenCLType(soot_field.getType());
      mStaticFieldSize += type.getSize();
    }
  }

  public int getStaticFieldSize(){
    return mStaticFieldSize;
  }

  public int getSize(){
    try {
      OffsetCalculator calc = OpenCLScene.v().getOffsetCalculator(m_SootClass);
      return calc.getSize(m_SootClass);
    } catch(RuntimeException ex){
      return 0;
    }
  }
  
  public String getName(){
    OpenCLType ocl_type = new OpenCLType(m_SootClass.getType());
    return ocl_type.getDerefString();
  }
  
  public String getJavaName(){
    return m_SootClass.getName();
  }
  
  public void addMethod(OpenCLMethod method){
    m_Methods.add(method);
  }

  void addField(OpenCLField ocl_field) {
    mFields.add(ocl_field);
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof OpenCLClass == false)
      return false;
    OpenCLClass other = (OpenCLClass) o;
    if(m_SootClass.equals(other.m_SootClass))
      return true;
    return false;
  }

  @Override
  public String toString(){
    return getName();
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 83 * hash + (this.m_SootClass != null ? this.m_SootClass.hashCode() : 0);
    return hash;
  }

  private void determineFieldTypes() {
    //caching
    if(mInstanceRefFields != null)
      return;

    mInstanceRefFields = new ArrayList<OpenCLField>();
    mInstanceNonRefFields = new ArrayList<OpenCLField>();
    mAllUsedInstanceFields = new ArrayList<OpenCLField>();

    mStaticRefFields = new ArrayList<OpenCLField>();
    mStaticNonRefFields = new ArrayList<OpenCLField>();
    mAllUsedStaticFields = new ArrayList<OpenCLField>();
    
    for(OpenCLField field : mFields){
      OpenCLType type = field.getType();
      if(type.isRefType()){
        if(field.isInstance()){
          mInstanceRefFields.add(field);
        } else {
          mStaticRefFields.add(field);
        }
      } else {
        if(field.isInstance()){
          mInstanceNonRefFields.add(field);
        } else {
          mStaticNonRefFields.add(field);
        }
      }
    }

    mAllUsedInstanceFields.addAll(mInstanceRefFields);
    mAllUsedInstanceFields.addAll(mInstanceNonRefFields);
    
    mAllUsedStaticFields.addAll(mStaticRefFields);
    mAllUsedStaticFields.addAll(mStaticNonRefFields);
  }
  
  void findAllUsedMethodsAndFields(){
    if(mFoundAllUsedMethodsAndFields)
      return;
    mFoundAllUsedMethodsAndFields = true;
    List<OpenCLMethod> old_methods = new ArrayList<OpenCLMethod>();
    old_methods.addAll(m_Methods);
    for(OpenCLMethod ocl_method : old_methods){
      ocl_method.findAllUsedMethodsAndFields();
    }
  }

  void findAllUsedArrayTypes(){
    for(OpenCLMethod ocl_method : m_Methods){
      ocl_method.findAllUsedArrayTypes();
    }
  }

  public int getRefFieldsSize() {
    determineFieldTypes();
    return mInstanceRefFields.size();
  }

  public List<OpenCLField> getAllUsedInstanceFields(){
    return mAllUsedInstanceFields;
  }

  boolean isLibraryClass() {
    return false;
  }

  public List<OpenCLField> getInstanceRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(mInstanceRefFields);
  }

  public List<OpenCLField> getInstanceNonRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(mInstanceNonRefFields);
  }
  
  public List<OpenCLField> getStaticRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(mStaticRefFields);
  }
  
  public List<OpenCLField> getStaticNonRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(mStaticNonRefFields);
  }
  
  public List<OpenCLField> getAllUsedStaticFields(){
    return mAllUsedStaticFields;
  }

  public Set<OpenCLMethod> getMethods() {
    return m_Methods;
  }

  public OpenCLField getField(String name) {
    determineFieldTypes();
    for(OpenCLField field : mAllUsedInstanceFields){
      if(field.getName().equals(name))
        return field;
    }
    for(OpenCLField field : mAllUsedStaticFields){
      if(field.getName().equals(name))
        return field;
    }
    return null;
  }
}
