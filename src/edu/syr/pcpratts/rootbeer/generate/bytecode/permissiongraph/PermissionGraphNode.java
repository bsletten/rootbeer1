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

import java.util.ArrayList;
import java.util.List;
import soot.SootClass;

public class PermissionGraphNode {

  private SootClass m_Class;
  private List<SootClass> m_Children;
  
  public PermissionGraphNode(SootClass soot_class){
    m_Class = soot_class;
    m_Children = new ArrayList<SootClass>();
  }

  public void addChild(SootClass soot_class) {
    m_Children.add(soot_class);
  }
  
  public List<SootClass> getChildren(){
    return m_Children;
  }
  
  public SootClass getSootClass(){
    return m_Class;
  }
}
