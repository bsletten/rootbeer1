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

import edu.syr.pcpratts.rootbeer.compiler.ClassRemapping;
import edu.syr.pcpratts.rootbeer.compiler.IgnorePackages;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.TreeNode;
import edu.syr.pcpratts.rootbeer.util.SignatureUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.RefType;
import soot.SootClass;
import soot.Type;

public class SortedTypeHistory {

  private List<TreeNode> m_Hierarchy;
  private boolean m_JustReturned;

  public List<Type> sort(List<Type> types){
    m_Hierarchy = new ArrayList<TreeNode>();
    
    MultiDimensionalArrayTypeCreator creator = new MultiDimensionalArrayTypeCreator();
    types = creator.create(types);
    types = findExtraRefTypes(types);
    
    List<Type> array_types = getArrayTypes(types);
    List<Type> ref_types = getRefTypes(types);
    doSort(ref_types);
    
    List<Type> hierarchy = createSingleHierarchy();
    hierarchy = trimDuplicates(hierarchy); 
    hierarchy = reachableFilter(hierarchy);
    
    array_types.addAll(hierarchy);
    
    return array_types;
  }
  
  private List<Type> findExtraRefTypes(List<Type> types){
    List<Type> ret = new ArrayList<Type>();
    for(Type type : types){
      ret.add(type);
      if(type instanceof ArrayType == false)
        continue;
      ArrayType array_type = (ArrayType) type;
      if(array_type.baseType instanceof RefType)
        ret.add(array_type.baseType);
    }
    return ret;
  }
  
  private SootClass getClass(Type type){
    RefType ref_type = (RefType) type;
    return RootbeerScene.v().getClass(ref_type.getClassName());
  }

  private void doSort(List<Type> types) {
    
    Set<String> visited = new HashSet<String>();
    for(Type type : types){
      
      SootClass soot_class = getClass(type);
      if(soot_class.hasSuperclass() == false)
        continue;
      SootClass parent = soot_class.getSuperclass();
      if(parent.getName().equals("java.lang.Object")){
        TreeNode tree = new TreeNode(soot_class, null);
        m_Hierarchy.add(tree);
        visited.add(soot_class.getName());
      }
    }
    
    boolean modified;
    do {      
      modified = false;
      
      for(Type type : types){
        SootClass soot_class = getClass(type);
        if(visited.contains(soot_class.getName()))
          continue;
        if(soot_class.hasSuperclass() == false)
          continue;
        SootClass parent = soot_class.getSuperclass();
        TreeNode node = getNode(parent);
        if(node == null)
          continue;
        node.addChild(soot_class, null);
        modified = true;
        visited.add(soot_class.getName());
      }
      
    } while(modified);
        
  }

  private TreeNode getNode(SootClass soot_class){
    for(TreeNode root : m_Hierarchy){
      TreeNode ret = root.find(soot_class);
      if(ret != null)
        return ret;
    }
    return null;
  }

  private List<Type> getArrayTypes(List<Type> types) {
    List<Type> ret = new ArrayList<Type>();
    for(Type type : types){
      if(type instanceof ArrayType)
        ret.add(type);
    }
    return ret;
  }

  private List<Type> getRefTypes(List<Type> types) {    
    List<Type> ret = new ArrayList<Type>();
    for(Type type : types){
      if(type instanceof RefType)
        ret.add(type);
    }
    return ret;
  }

  private List<Type> createSingleHierarchy() {
    List<Type> ret = new ArrayList<Type>();
    List<Integer> lengths = new ArrayList<Integer>();
    for(int i = 0; i < m_Hierarchy.size(); ++i){
      TreeNode node = m_Hierarchy.get(i);
      int len = getLen(node, 1);
      lengths.add(len);
    }
    while(true){
      int max_len_index = getMaxLenIndex(lengths);
      if(max_len_index == -1){   
        return ret;
      }
      
      int len = lengths.get(max_len_index);
      TreeNode curr = m_Hierarchy.get(max_len_index);
      SootClass soot_class = getClassAtLen(curr, 1, len);
      if(soot_class == null){        
        --len;
        lengths.set(max_len_index, len);
      } else if(len == 1) {
        len = -1;
        lengths.set(max_len_index, len);
        ret.add(soot_class.getType());      
      } else {
        ret.add(soot_class.getType());      
      }
    }
  }
  
  private void printHierarchy(){
    for(TreeNode node : m_Hierarchy){  
      System.out.println("New root:");
      node.print();
    }
  }
  
  private SootClass getClassAtLen(TreeNode curr, int curr_len, int desired_len){
    if(curr_len == desired_len){
      m_JustReturned = true;
      return curr.getSootClass();
    }
    List<TreeNode> children = curr.getChildren();
    for(int i = 0; i < children.size(); ++i){
      TreeNode child = children.get(i);
      SootClass ret = getClassAtLen(child, curr_len+1, desired_len);
      if(m_JustReturned){
        children.remove(i);
        m_JustReturned = false;
      }      
      if(ret != null)
        return ret;
    }
    return null;
  }
  
  private int getLen(TreeNode curr, int len){
    List<TreeNode> children = curr.getChildren();
    if(children.isEmpty())
      return len;
    int max_len = len;
    for(TreeNode child : children){
      int new_len = getLen(child, len+1);
      if(new_len > max_len)
        max_len = new_len;
    }
    return max_len;
  }

  private int getMaxLenIndex(List<Integer> lengths) {
    int max = -1;
    int max_index = -1;
    for(int i = 0; i < lengths.size(); ++i){
      int len = lengths.get(i);
      if(len < 1)
        continue;      
      if(len > max){
        max = len;
        max_index = i;
      }
    }
    return max_index;
  }

  private void printTypes(List<Type> ret) {
    for(Type curr : ret){
      RefType ref_type = (RefType) curr;
      System.out.println(ref_type);
    }    
  }

  private List<Type> trimDuplicates(List<Type> hierarchy) {
    List<Type> ret = new ArrayList<Type>();
    Set<String> visited = new HashSet<String>();
    for(Type curr : hierarchy){
      String name = ((RefType) curr).getClassName();
      if(visited.contains(name))
        continue;
      visited.add(name);
      ret.add(curr);
    }
    return ret;
  }

  private List<Type> reachableFilter(List<Type> hierarchy) {
    ClassRemapping remapping = new ClassRemapping();
    List<String> remap_used = remapping.getUsed();
    
    List<Type> ret = new ArrayList<Type>();
    List<String> reachable_methods = RootbeerScene.v().getReachableMethods();
    for(Type type : hierarchy){
      if(type instanceof RefType == false){
        ret.add(type);
        continue;
      }
      RefType ref_type = (RefType) type;
      if(containsSignature(ref_type, reachable_methods)){
        ret.add(ref_type);
      } else if(containsClass(ref_type, remap_used)){
        ret.add(ref_type);
      }
    }
    return ret;
  }

  private boolean containsSignature(RefType ref_type, List<String> reachable_methods) {
    SootClass soot_class = ref_type.getSootClass();
    for(String method_sig : reachable_methods){
      SignatureUtil util = new SignatureUtil();
      String cls = util.classFromMethodSig(method_sig);
      if(soot_class.getName().equals(cls)){
        return true;
      }
    }
    return false;
  }

  private boolean containsClass(RefType ref_type, List<String> remap_used) {
    SootClass soot_class = ref_type.getSootClass();
    return remap_used.contains(soot_class.getName());
  }
}
