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

import edu.syr.pcpratts.jpp.cfile.CStatement;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SemiExpression implements Iterator<CStatement> {

  private Iterator<Token> m_LowerLevel;
  private List<Token> m_NextNext;
  private boolean m_SeekingNewLine;
  
  private List<Token> m_Ret;
  
  public SemiExpression(Iterator<Token> iter){
    m_LowerLevel = iter;
  }

  public boolean hasNext() {
    return m_LowerLevel.hasNext();
  }

  public CStatement next() {
    if(m_NextNext != null)
      return new CStatement(m_NextNext);

    m_Ret = new ArrayList<Token>();
    while(true){
      Token curr = m_LowerLevel.next();

      if(curr.isNewLine() && m_SeekingNewLine == false){
        if(m_LowerLevel.hasNext() == false){
          return new CStatement(m_Ret);
        }
        continue;
      }

      if(m_SeekingNewLine){
        if(curr.isNewLine()){
          m_SeekingNewLine = false;
          curr.setImmutable();
          m_Ret.add(curr);
          return new CStatement(m_Ret);
        } else {
          m_Ret.add(curr);
        }
      } else if(curr.isPreprocessor()){
        if(m_Ret.isEmpty() == false){
          m_NextNext = new ArrayList<Token>();
          m_NextNext.add(curr);
          return new CStatement(m_Ret);
        } else {
          m_NextNext = null;
          m_Ret.add(curr);
          return new CStatement(m_Ret);          
        }
      } else if(isEndString(curr)){
        m_Ret.add(curr);
        return new CStatement(m_Ret);
      } else {
        m_Ret.add(curr);
      }
      if(m_LowerLevel.hasNext() == false){
        return new CStatement(m_Ret);
      }
    }
  }

  private boolean isEndString(Token token) {
    String str = token.getString();
    if(str.equals("{"))
      return true;
    if(str.equals("}"))
      return true;
    if(str.equals(";"))
      return true;
    if(str.equals(":")){
      if(hitClass())
        return false;
      if(prevIsAccessSpec())
        return true;  
      if(labeledStatement())
        return true;
    }
    return false;
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private boolean hitClass() {
    for(Token tok : m_Ret){
      String str = tok.getString();
      if(str.equals("class"))
        return true;
      if(str.equals("struct"))
        return true;
      if(str.equals("typedef"))
        return true;
      if(str.equals("union"))
        return true;
      //asm statements can mess us up
      if(str.equals("asm"))
        return true;
    }
    return false;
  }

  private boolean prevIsAccessSpec() {
    String prev = m_Ret.get(m_Ret.size()-1).getString();
    if(prev.equals("private"))
      return true;
    if(prev.equals("protected"))
      return true;
    if(prev.equals("public"))
      return true;
    return false;
  }

  private boolean labeledStatement() {
    if(m_Ret.size() == 1)
      return true;
    String first = m_Ret.get(0).getString();
    if(first.equals("case"))
      return true;
    if(first.equals("default"))
      return true;
    return false;
  }

}
