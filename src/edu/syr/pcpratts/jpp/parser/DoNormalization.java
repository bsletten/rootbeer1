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

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Adds parens to do while loops if they aren't there.
 * Assumes that BasicBlockNormalize has already normallized
 * the other control statements.
 * @author pcpratts
 */
public class DoNormalization extends AbstractTokenizer {

  private List<String> m_ScopeStack;
  private Token m_NextNext;

  public DoNormalization(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
    m_ScopeStack = new ArrayList<String>();
  }

  @Override
  protected void parseNextToken() {
    Token curr;
    if(m_NextNext != null){
      curr = m_NextNext;
      m_NextNext = null;
    } else {
      curr =  m_LowerLevel.next();
    }
    if(curr.getString().equals("do") && m_LowerLevel.hasNext()){
      m_Tokens.add(curr);
      Token next = m_LowerLevel.next();
      if(next.getString().equals("{") == false){
        m_Tokens.add(new Token("{"));
        m_ScopeStack.add(" ");
      } else {
        m_ScopeStack.add("{");
      }
      m_NextNext = next;

    } else if(curr.getString().equals("while")){

      List<Token> past = readOnePastClosingParen();
      if(past.isEmpty())
        throw new RuntimeException("this code doesn't compile");

      Token last = past.get(past.size()-1);
      if(last.getString().equals(";")){
        if(m_ScopeStack.isEmpty() == false){
          String top = m_ScopeStack.get(m_ScopeStack.size()-1);
          m_ScopeStack.remove(m_ScopeStack.size()-1);
          if(top.equals(" ")){
            m_Tokens.add(new Token("}"));
          }
        }
      }

      m_Tokens.add(curr);
      m_Tokens.addAll(past);

    } else {
      m_Tokens.add(curr);
    }
  }


}
