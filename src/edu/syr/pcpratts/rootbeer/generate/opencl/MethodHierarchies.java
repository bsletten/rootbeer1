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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import soot.SootClass;
import soot.SootMethod;

/**
 * Represents all the versions of methods in a class Hierarchy
 * @author pcpratts
 */
public class MethodHierarchies {

  private Set<MethodHierarchy> mHierarchies;
  
  public MethodHierarchies(){
    mHierarchies = new LinkedHashSet<MethodHierarchy>();
  }
  
  public void addMethod(SootMethod method){
    MethodHierarchy new_hierarchy = new MethodHierarchy(method);
    if(mHierarchies.contains(new_hierarchy) == false)
      mHierarchies.add(new_hierarchy);
  }
  
  public List<OpenCLMethod> getMethods(){
    List<OpenCLMethod> ret = new ArrayList<OpenCLMethod>();
    //for each method    
    for(MethodHierarchy method_hierarchy : mHierarchies){
      //get the list of classes in the hierarchy
      List<OpenCLMethod> methods = method_hierarchy.getMethods();
      for(OpenCLMethod method : methods){ 
        ret.add(method);
      }
    }   
    return ret;
  }
  
  public List<OpenCLPolymorphicMethod> getPolyMorphicMethods(){
    List<OpenCLPolymorphicMethod> ret = new ArrayList<OpenCLPolymorphicMethod>();
    //for each method    
    for(MethodHierarchy method_hierarchy : mHierarchies){
      if(method_hierarchy.isPolyMorphic()){
        ret.add(method_hierarchy.getOpenCLPolyMorphicMethod());
      }
    }   
    return ret;
  }
  
  private class MethodHierarchy {
    
    private String mMethodSubsignature;
    private List<SootClass> mClassHierarchy;
    
    public MethodHierarchy(SootMethod method){
      mMethodSubsignature = method.getSubSignature();
      mClassHierarchy = OpenCLScene.v().getClassHierarchy(method.getDeclaringClass());
    }
    
    public List<OpenCLMethod> getMethods(){
      List<OpenCLMethod> ret = new ArrayList<OpenCLMethod>();
      for(SootClass soot_class : mClassHierarchy){
        SootMethod soot_method = null;
        try {
          soot_method = soot_class.getMethod(mMethodSubsignature);
        } catch(Exception ex){
          continue;
        }
        OpenCLMethod method = new OpenCLMethod(soot_method, soot_class);
        ret.add(method);
      }      
      return ret;
    }
        
    public boolean isPolyMorphic(){
      if(mClassHierarchy.size() > 1)
        return true;
      return false;
    }
    
    public OpenCLPolymorphicMethod getOpenCLPolyMorphicMethod(){
      for(SootClass soot_class : mClassHierarchy){
        try {
          SootMethod soot_method = soot_class.getMethod(mMethodSubsignature);
          return new OpenCLPolymorphicMethod(soot_method);
        } catch(RuntimeException ex){
          continue;
        }
      }
      throw new RuntimeException("Cannot find class: "+mMethodSubsignature);
    }
    
    @Override
    public boolean equals(Object o){
      if(o instanceof MethodHierarchy == false)
        return false;
      MethodHierarchy other = (MethodHierarchy) o;
      if(mMethodSubsignature.equals(other.mMethodSubsignature) == false)
        return false;
      if(mClassHierarchy == other.mClassHierarchy == false)
        return false;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 5;
      hash = 19 * hash + (this.mMethodSubsignature != null ? this.mMethodSubsignature.hashCode() : 0);
      hash = 19 * hash + (this.mClassHierarchy != null ? this.mClassHierarchy.hashCode() : 0);
      return hash;
    }
  }
}
