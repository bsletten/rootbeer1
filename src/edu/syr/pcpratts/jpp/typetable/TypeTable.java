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

package edu.syr.pcpratts.jpp.typetable;

import edu.syr.pcpratts.jpp.cfile.CType;
import edu.syr.pcpratts.jpp.cfile.CClass;
import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CFileVisitor;
import edu.syr.pcpratts.jpp.cfile.internal.CFileFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeTable extends CFileVisitor {

  private List<CType> m_Types;
  private Set<String> m_VisitedTypes;
  
  public TypeTable(CFile cfile) {
    super(true);
    
    addBuiltIns();
    
    m_VisitedTypes = new HashSet<String>();
    
    visit(cfile);
  }  

  @Override
  protected void visitItem(CFileItem next) {
    if(next instanceof CClass){
      CClass cclass = (CClass) next;
      CType type = cclass.getType();
      String type_string = type.toString();
      if(m_VisitedTypes.contains(type_string) == false){
        m_VisitedTypes.add(type_string);
        m_Types.add(type);
      }
    } 
  }
  
  public List<CType> get(){
    return m_Types;
  }
  
  private void addBuiltIns() {
    m_Types = new ArrayList<CType>();
    BuiltInTypes builtins = new BuiltInTypes();
    m_Types.addAll(builtins.get());
  }

  public static void main(String[] args){
    CFileFactory factory = new CFileFactory();
    try {
      String filename = "testFiles"+File.separator+"graph.h";
      CFile cfile = factory.create(filename);
      TypeTable table = new TypeTable(cfile);
      List<CType> types = table.get();
      for(CType type : types){
        System.out.println(type.toString());
      }
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
