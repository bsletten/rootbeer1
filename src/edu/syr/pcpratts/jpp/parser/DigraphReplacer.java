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

import java.util.Iterator;

public class DigraphReplacer extends AbstractTokenizer {

  public DigraphReplacer(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
  }

  @Override
  protected void parseNextToken() {
    Token token = m_LowerLevel.next();
    token = replace(token);
    m_Tokens.add(token);
  }

  private Token replace(Token token) {
    String str = token.getString();
    if(str.equals("<:"))
      return new Token("[");
    if(str.equals(":>"))
      return new Token("]");
    if(str.equals("<%"))
      return new Token("{");
    if(str.equals("%>"))
      return new Token("}");
    if(str.equals("%:"))
      return new Token("#");
    return token;
  }
}
