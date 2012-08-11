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

package edu.syr.pcpratts.jpp.cfile.internal;

import edu.syr.pcpratts.jpp.cfile.Pass;
import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CFileFactory {

  public CFile create(String filename) throws FileNotFoundException {

    ParserFactory factory = new ParserFactory();
    PutbackIterator<CFileItem> iter = factory.createCppFromFilename(filename);
    return create(iter);
    
  }

  public CFile create(PutbackIterator<CFileItem> iter){

    CFile prev = new CFile(iter);
    CFile next = null;
    
    Pass pass0 = new TemplateDetectionPass();
    next = pass0.execute(prev.iterator());
    FixupTokens fixer = new FixupTokens();
    prev = fixer.fixup(next);

    List<Pass> passes = new ArrayList<Pass>();
    //passes.add(new NamespacePass());
    //passes.add(new TemplateAttachmentPass());
    //passes.add(new ClassPass());
    passes.add(new MethodPass());
    //passes.add(new ClassesMethodsPass());

    for(Pass pass : passes){
      next = pass.execute(prev.iterator());
      prev = next;
    }

    return prev;
  }

}
