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
import edu.syr.pcpratts.rootbeer.generate.bytecode.FieldReadWriteInspector;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.SootClass;
import soot.SootField;

public class FieldCloner {

  private List<TreeNode> m_Hierarchy;
  private List<CompositeField> m_Fields;
  private Set<String> m_ProcessNodeVisited;
  
  public void setup(Map<String, OpenCLClass> classes) {
    m_ProcessNodeVisited = new HashSet<String>();
    createHierarchy(classes);
    m_Fields = new ArrayList<CompositeField>();
    for(TreeNode node : m_Hierarchy){
      CompositeField composite = new CompositeField();
      processNode(node, composite);
      if(composite.getClasses().isEmpty()){
        composite.getClasses().add(node.getSootClass());          
      }
      m_Fields.add(composite);
    }
  }
  
  public List<CompositeField> getCompositeFields(){
    return m_Fields; 
  }
        
  private void createHierarchy(Map<String, OpenCLClass> classes){
    ReverseClassHierarchy creator = new ReverseClassHierarchy(classes);
    m_Hierarchy = creator.get();
  }
  
  private void processNode(TreeNode node, CompositeField composite) {
    OpenCLClass ocl_class = node.getOpenCLClass();
    List<OpenCLField> ref_fields = ocl_class.getInstanceRefFields();
    for(OpenCLField field : ref_fields){ 
      processNodeField(node, field, true, composite);
    }    
    List<OpenCLField> static_ref_fields = ocl_class.getStaticRefFields();
    for(OpenCLField field : static_ref_fields){ 
      processNodeField(node, field, true, composite);
    }
    List<OpenCLField> non_ref_fields = ocl_class.getInstanceNonRefFields();
    for(OpenCLField field : non_ref_fields){ 
      processNodeField(node, field, false, composite);
    }
    List<OpenCLField> static_non_ref_fields = ocl_class.getStaticNonRefFields();
    for(OpenCLField field : static_non_ref_fields){ 
      processNodeField(node, field, false, composite);
    }
    for(TreeNode child : node.getChildren()){
      processNode(child, composite);
    }
  }
  
  private void processNodeField(TreeNode node, OpenCLField field, boolean ref_field, CompositeField composite){
    
    SootClass soot_class = node.getSootClass();    
    SootField soot_field = field.getSootField();
    
    OpenCLField new_field = new OpenCLField(soot_field, soot_class);
    
    if(isCloned(soot_class, soot_field)){
      new_field.setClone(field);
    } else {
      soot_field = soot_class.getFieldByName(soot_field.getName());
      new_field = new OpenCLField(soot_field, soot_class);
      field = new_field;
    }
    
    String hash = soot_class.getName()+"."+soot_field.getName();
    if(m_ProcessNodeVisited.contains(hash) == false){
      m_ProcessNodeVisited.add(hash);
      if(ref_field){
        composite.addRefField(new_field, soot_class);
      } else {
        composite.addNonRefField(new_field, soot_class);
      }
    }
    
    for(TreeNode child : node.getChildren()){
      processNodeField(child, field, ref_field, composite);
    }
  }

  /**
   * if the class has a field by the name, it is not cloned
   * @param soot_class
   * @param soot_field
   * @return 
   */
  private boolean isCloned(SootClass soot_class, SootField soot_field) {
    try {
      soot_class.getFieldByName(soot_field.getName());
      return false;
    } catch(Exception ex){
      return true;
    }
  }
  
}
