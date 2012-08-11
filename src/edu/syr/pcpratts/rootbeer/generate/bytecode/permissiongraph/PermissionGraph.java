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

package edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph;

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.ArrayList;
import java.util.List;
import soot.RefType;
import soot.SootClass;

public class PermissionGraph {

  private List<PermissionGraphNode> m_Roots;
  
  public PermissionGraph(){
    m_Roots = new ArrayList<PermissionGraphNode>();
    build(OpenCLScene.v().getRefTypeOrderedHistory());
  }

  private void build(List<RefType> history) {
    for(RefType type : history){
      SootClass soot_class = type.getSootClass();
      if(soot_class.isPublic()){
        m_Roots.add(new PermissionGraphNode(soot_class));
      }
    }
    for(RefType type : history){
      SootClass soot_class = type.getSootClass();
      if(soot_class.isPublic() == false){
        PermissionGraphNode root = findRoot(soot_class);
        root.addChild(soot_class);
      }
    }
  }
  
  public List<PermissionGraphNode> getRoots(){
    return m_Roots;
  }

  private PermissionGraphNode findRoot(SootClass soot_class) {
    String pkg = soot_class.getJavaPackageName();
    for(PermissionGraphNode node : m_Roots){
      String root_pkg = node.getSootClass().getJavaPackageName();
      if(pkg.equals(root_pkg))
        return node;
    }
    throw new RuntimeException("can't find root");
  }
}
