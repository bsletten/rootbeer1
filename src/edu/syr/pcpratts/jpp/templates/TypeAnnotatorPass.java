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

package edu.syr.pcpratts.jpp.templates;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CFileVisitor;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.typetable.TypeTable;

public class TypeAnnotatorPass extends CFileVisitor {

  private TypeTable m_TypeTable;  
  
  TypeAnnotatorPass(TypeTable type_table) {
    super(false);
    
    m_TypeTable = type_table;
  }

  @Override
  protected void visitItem(CFileItem item) {
    if(item instanceof CStatement == false)
      return;
    
    CStatement stmt = (CStatement) item;
    System.out.println(stmt.toString());
      
  }

}
