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

import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CFileVisitor;
import edu.syr.pcpratts.jpp.cfile.Pass;
import edu.syr.pcpratts.jpp.cfile.internal.CFileFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.typetable.TypeTable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExtendedCFileFactory {

  private TypeTable m_TypeTable;
  
  private ExtendedCFileFactory(TypeTable type_table) {
    m_TypeTable = type_table;
  }

  public CFile create(CFile cfile){
     
    List<CFileVisitor> passes = new ArrayList<CFileVisitor>();
    passes.add(new TypeAnnotatorPass(m_TypeTable));
    passes.add(new MethodDeclFinderPass());
    
    for(CFileVisitor pass : passes){
      pass.visit(cfile);
    }

    return cfile;
  }
  
  public static void main(String[] args){
    try {
      CFileFactory factory1 = new CFileFactory();
      String filename = "testFiles"+File.separator+"test4.cpp";
      CFile cfile = factory1.create(filename);
      
      TypeTable type_table = new TypeTable(cfile);
      ExtendedCFileFactory factory2 = new ExtendedCFileFactory(type_table);
      cfile = factory2.create(cfile);
      
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
