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

package edu.syr.pcpratts.jpp.parser;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ForSemiExpression implements Iterator<CFileItem>{

  private Iterator<CStatement> m_LowerLevel;
  private int m_BraceCount;

  public ForSemiExpression(Iterator<CStatement> iter){
    m_LowerLevel = iter;
    m_BraceCount = 0;
  }
  
  public boolean hasNext() {
    return m_LowerLevel.hasNext();
  }

  public CStatement next() {
    List<Token> tokens = new ArrayList<Token>();
    CStatement stmt = m_LowerLevel.next();
    List<Token> curr = stmt.getTokens();
    tokens.addAll(curr);
    if(forTokenList(curr)){
      addTokens(tokens);
      addTokens(tokens);
    }
    CStatement ret = new CStatement(tokens);
    m_BraceCount = ret.braceCount(m_BraceCount);
    return ret;
  }

  private void addTokens(List<Token> existing){
    if(m_LowerLevel.hasNext() == false)
      return;
    existing.addAll(m_LowerLevel.next().getTokens());
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private boolean forTokenList(List<Token> curr) {
    if(curr.isEmpty())
      return false;
    Token first = curr.get(0);
    if(first.getString().equals("for"))
      return true;
    return false;
  }

}
