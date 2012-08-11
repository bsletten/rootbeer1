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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * If a basic block does not have braces, add them and make sure
 * the block is split
 * @author pcpratts
 */
public class BasicBlockNormalize extends AbstractTokenizer {

  private List<String> m_ScopeStack;
  private Token m_Last;

  public BasicBlockNormalize(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
    m_ScopeStack = new LinkedList<String>();
  }

  @Override
  protected void parseNextToken() {
    Token curr;
    if(m_Last != null){
      curr = m_Last;
      m_Last = null;
    } else {
      curr =  m_LowerLevel.next();
    }
    if(matchingControlSimple(curr.getString())){
      List<Token> full_statement = readOnePastClosingParen();
      if(full_statement.size() < 1)
        throw new RuntimeException("this code doesn't compile");
      Token last = full_statement.get(full_statement.size()-1);
      if(curr.getString().equals("while") && last.getString().equals(";")){
        //do nothing
      } else if(last.getString().equals("{") == false){
        full_statement.remove(full_statement.size()-1);
        full_statement.add(new Token("{"));
        m_Last = last;
        m_ScopeStack.add(" ");
      } else {
        m_ScopeStack.add("{");
        m_Last = null;
      }
      m_Tokens.add(curr);
      m_Tokens.addAll(full_statement);
    } else if(matchingControlElse(curr.getString())){
      Token next = m_LowerLevel.peek(0);
      if(next == null)
        return;
      if(next.getString().equals("{") == false){
        m_ScopeStack.add(" ");
        m_LowerLevel.putback(new Token("{"));
        m_Tokens.add(curr);
      } else {
        m_ScopeStack.add("{");
        m_Last = null;
        m_Tokens.add(curr);
      }
    } else if (curr.getString().equals(";")) {
      m_Tokens.add(curr);
      while(true){
        if(m_ScopeStack.isEmpty())
          return;
        String top = m_ScopeStack.get(m_ScopeStack.size()-1);
        m_ScopeStack.remove(m_ScopeStack.size()-1);
        if(top.equals(" ")){
          m_Tokens.add(new Token("}"));
        } else {
          return;
        }
      }
    } else {
      m_Tokens.add(curr);
    }
  }

  private boolean matchingControlSimple(String str) {
    if(str.equals("if"))
      return true;
    if(str.equals("while"))
      return true;
    if(str.equals("for"))
      return true;
    return false;
  }

  private boolean matchingControlElse(String str) {
    if(str.equals("else")){
      Token next = m_LowerLevel.peek(0);
      if(next == null)
        return false;
      if(next.getString().equals("if"))
        return false;
      return true;
    }
    return false;
  }

}
