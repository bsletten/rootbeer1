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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

abstract class AbstractTokenizer implements IteratorEx<Token> {

  protected ConcreteIteratorEx<Token> m_LowerLevel;
  protected List<Token> m_Tokens;

  public AbstractTokenizer(){
    m_Tokens = new LinkedList<Token>();
  }

  public boolean hasNext() {
    if(m_Tokens.isEmpty() == false){
      return true;
    }
    if(m_LowerLevel.hasNext() == false)
      return false;
    parseNextToken();
    if(m_Tokens.isEmpty() == false){
      return true;
    } else {
      return false;
    }
  }

  public Token peek(int tokens){
    return m_LowerLevel.peek(tokens);
  }
  
  public Token next() {
    Token ret = m_Tokens.get(0);
    m_Tokens.remove(0);
    return ret;
  }

  public void putback(Token token){
    List<Token> new_tokens = new ArrayList<Token>();
    new_tokens.add(token);
    for(Token tok : m_Tokens)
      new_tokens.add(tok);
    m_Tokens = new_tokens;
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  protected abstract void parseNextToken();

  protected List<Token> readOnePastClosingParen(){
    List<Token> ret = new ArrayList<Token>();
    int stack_count = 0;
    String start = "(";
    String end = ")";
    while(true){
      if(m_LowerLevel.hasNext() == false)
        return ret;
      Token curr = m_LowerLevel.next();
      ret.add(curr);
      if(curr.getString().equals(start)){
        stack_count++;
      } else if(curr.getString().equals(end)){
        stack_count--;
        if(stack_count == 0){
          if(m_LowerLevel.hasNext() == false)
            return ret;
          Token one_past = m_LowerLevel.next();
          ret.add(one_past);
          return ret;
        }
      }
    }
  }
}
