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

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import java.util.ArrayList;
import java.util.List;
import soot.SootClass;

public class TreeNode {

  private SootClass m_Curr;
  private OpenCLClass m_CurrOcl;
  private List<TreeNode> m_Children;

  public TreeNode(SootClass root, OpenCLClass ocl_class){
    m_Curr = root;
    m_CurrOcl = ocl_class;
    m_Children = new ArrayList<TreeNode>();
  }

  public void addChild(SootClass child, OpenCLClass ocl_class){
    TreeNode new_node = new TreeNode(child, ocl_class);
    m_Children.add(new_node);
  }
  
  public SootClass getSootClass(){
    return m_Curr;
  }
  
  public OpenCLClass getOpenCLClass(){
    return m_CurrOcl;
  }
  
  public List<TreeNode> getChildren(){
    return m_Children;
  }

  public TreeNode find(SootClass node){
    if(m_Curr.getName().equals(node.getName()))
      return this;
    for(TreeNode child : m_Children){
      TreeNode ret = child.find(node);
      if(ret != null)
        return ret;
    }
    return null;
  }

  public void print(){
    System.out.println("curr: "+m_Curr.getName());
    for(TreeNode child : m_Children){
      System.out.println("child: "+child.m_Curr.getName());
    }
    for(TreeNode child : m_Children){
      child.print();
    }
  }
}