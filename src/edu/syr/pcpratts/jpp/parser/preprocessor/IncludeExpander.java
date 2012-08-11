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

package edu.syr.pcpratts.jpp.parser.preprocessor;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class IncludeExpander {

  private IncludePath m_IncludePath;
  private StringBuilder m_Builder;
  private boolean m_IncludedFile;

  public IncludeExpander(IncludePath path){
    m_IncludePath = path;
  }
/*
  public Reader process(String filename) throws FileNotFoundException {
    m_IncludePath.startFile(filename);

    ParserFactory factory = new ParserFactory();
    PutbackIterator<CFileItem> iter = factory.createPreprocFromFilename(filename);

    do {
      processIteration(iter);
      iter = factory.createPreprocFromReader(new StringReader(m_Builder.toString()));
    } while(m_IncludedFile);

    return new StringReader(m_Builder.toString());
  }

  private void processIteration(PutbackIterator<CFileItem> iter){
    m_Builder = new StringBuilder();
    m_IncludedFile = false;
    while(iter.hasNext()){
      CFileItem next = iter.next();
      processItem(next);
    }
  }

  private void processItem(CFileItem next) {
    List<Token> tokens = next.getTokens();
    String first = getFirstString(tokens);
    if(first.startsWith("#include ")){
      
      //boolean handled = handleInclude(tokens);
      //if(!handled){
      //  m_Builder.append(next.toString()+"\n");
      //}
      
    } else {
      m_Builder.append(next.toString()+"\n");
    }
  }

  private String getFirstString(List<Token> tokens) {
    if(tokens.isEmpty())
      return "";
    Token first = tokens.get(0);
    return first.getString();
  }


  private boolean handleInclude(List<Token> tokens) {
    if(tokens.size() < 2){
      return false;
    }
    if(m_IncludedFile){
      return false;
    }
    m_IncludedFile = true;

    //tokens look like:
    //#include < hello / list >
    //#include "hello/list"
    String second = tokens.get(1).getString();
    String filename = "";
    boolean library_file;
    if(second.equals("<")){
      for(int i = 2; i < tokens.size()-1; ++i){
        filename += tokens.get(i).getString();
      }
      library_file = true;
    } else {
      RemoveStringSurrounding remover = new RemoveStringSurrounding();
      filename = remover.remove(second);
      library_file = false;
    }
    String absolute_path = m_IncludePath.getAbsolutePath(filename, library_file);
    try {
      ParserFactory factory = new ParserFactory();
      PutbackIterator<CFileItem> iter = factory.createPreprocFromFilename(absolute_path);
      while(iter.hasNext()){
        CFileItem item = iter.next();
        m_Builder.append(item.toString()+"\n");
      }
      return true;
    } catch(FileNotFoundException ex){
      throw new RuntimeException(ex);
    }
  }
  */
}
