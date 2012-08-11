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
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MacroExpander {

  private Set<String> m_SingleDefineSymbols;
  private Map<String, String> m_DoubleDefineSymbols;
  private List<Macro> m_Macros;
  private StringBuilder m_Builder;
  private CFileItem m_CurrentItem;
  private PutbackIterator<Token> m_Iterator;

  public MacroExpander(){
    m_SingleDefineSymbols = new HashSet<String>();
    m_DoubleDefineSymbols = new HashMap<String, String>();
    m_Macros = new ArrayList<Macro>();
  }

  Reader process(PutbackIterator<Token> iter1) {
    m_Iterator = iter1;
    m_Builder = new StringBuilder();
    while(m_Iterator.hasNext()){
      Token next = m_Iterator.next();
      processItem(next);
    }
    return new StringReader(m_Builder.toString());
  }

  public Set<String> getSingleDefineSymbols(){
    return m_SingleDefineSymbols;
  }

  public Map<String, String> getDoubleDefineSymbols(){
    return m_DoubleDefineSymbols;
  }

  private void processItem(Token next) {
    if(next.getString().equals("#define")){
      eatLine();
    } else if(next.getString().equals("#undef")){
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
