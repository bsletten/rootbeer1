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
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Good tutorial on C preprocessor
 * http://www.cprogramming.com/tutorial/cpreprocessor.html
 * @author pcpratts
 */
public class Preprocessor {

  private StringBuilder m_Builder;
  private List<IfState> m_IfState;
  private CFileItem m_CurrentItem;
  private IncludeExpander m_IncludeExpander;
  private MacroExpander m_MExpander;
  private PutbackIterator<Token> m_Iterator;

  private enum IfState {
    True,
    False
  }

  public Preprocessor(IncludePath include_path){
    m_IncludeExpander = new IncludeExpander(include_path);
    m_IfState = new ArrayList<IfState>();
  }

  public Reader process(String filename) throws FileNotFoundException {
    ParserFactory factory = new ParserFactory();

    //Reader pass1 = m_IncludeExpander.process(filename);
    PutbackIterator<Token> iter1 = factory.createPreprocFromFilename(filename);

    m_MExpander = new MacroExpander();
    Reader pass2 = m_MExpander.process(iter1);

    PutbackIterator<Token> iter2 = factory.createPreprocFromReader(pass2);
    processIteration(iter2);

    return new StringReader(m_Builder.toString());
  }

  private void processIteration(PutbackIterator<Token> iter){
    m_Iterator = iter;
    m_Builder = new StringBuilder();
    while(iter.hasNext()){
      Token next = iter.next();
      processItem(next);
    }
  }

  private void processItem(Token next) {
    if(next.getString().equals("#include")){
      eatLine();
    } else if(next.getString().equals("#ifdef")){
      eatLine();
    } else if(next.getString().equals("#ifndef")){
      eatLine();
    } else if(next.getString().equals("#endif")){
      eatLine();
    } else if(next.getString().equals("#elif")){
      eatLine();
    } else if(next.getString().equals("#if")){
      eatLine();
    } else if(next.getString().equals("#else")){
      eatLine();
    } else if (next.getString().equals("#line")) {
      eatLine();
    } else {
      handleOthers(next);
    }
  }

  private void handleOthers(Token next) {
    m_Builder.append(next+" ");
  }

  private void eatLine() {
    while(m_Iterator.hasNext()){
      Token next = m_Iterator.next();
      if(next.isNewLine())
        return;
    }
  }
}
