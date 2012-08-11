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

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.SootClass;

public class ReverseClassHierarchy {
  private List<TreeNode> m_Hierarchy;
  private Map<String, OpenCLClass> m_Classes;
  
  public ReverseClassHierarchy(Map<String, OpenCLClass> classes){
    m_Hierarchy = new ArrayList<TreeNode>();
    m_Classes = classes;
    
    addClass("java.lang.String");
    
    Set<String> key_set = classes.keySet();
    Set<String> visited = new HashSet<String>();
    for(String key : key_set){
      OpenCLClass ocl_class = classes.get(key);
      SootClass soot_class = RootbeerScene.v().getClass(ocl_class.getJavaName());
      if(soot_class.hasSuperclass() == false)
        continue;
      SootClass parent = soot_class.getSuperclass();
      if(parent.getName().equals("java.lang.Object")){
        TreeNode tree = new TreeNode(soot_class, ocl_class);
        m_Hierarchy.add(tree);
        visited.add(key);
      }
    }
    
    boolean modified;
    do {      
      modified = false;
      
      key_set = classes.keySet();
      for(String key : key_set){
        if(visited.contains(key))
          continue;
        OpenCLClass ocl_class = classes.get(key);
        SootClass soot_class = RootbeerScene.v().getClass(ocl_class.getJavaName());
        if(soot_class.hasSuperclass() == false)
          continue;
        SootClass parent = soot_class.getSuperclass();
        TreeNode node = getNode(parent);
        if(node == null)
          continue;
        node.addChild(soot_class, ocl_class);
        modified = true;
        visited.add(key);
      }
      
    } while(modified);
  }
  
  public List<TreeNode> get(){
    return m_Hierarchy;
  }
  
  private TreeNode getNode(SootClass soot_class){
    for(TreeNode root : m_Hierarchy){
      TreeNode ret = root.find(soot_class);
      if(ret != null)
        return ret;
    }
    return null;
  }

  private void addClass(String cls) {
    SootClass soot_class = RootbeerScene.v().getClass(cls);
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);
    m_Classes.put(cls, ocl_class);
  }
}
