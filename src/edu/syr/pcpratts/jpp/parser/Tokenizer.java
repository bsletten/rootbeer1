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
import java.util.LinkedList;
import java.util.List;

/**
 * Split the 'Everything else' from StringAndCommentTokenizer into
 * space separated tokens.  Leave strings and comments as is
 * @author pcpratts
 */
public class Tokenizer extends AbstractTokenizer {

  public Tokenizer(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
    m_Tokens = new LinkedList<Token>();
  }

  protected void parseNextToken() {
    Token big_token;
    while(true){
      big_token = m_LowerLevel.next();
      if(big_token.isComment() && m_LowerLevel.hasNext()){
        continue;
      } else {
        break;
      }
    } 
    String next = big_token.getString();
    if(big_token.isImmutable() || big_token.isNewLine()){
      m_Tokens.add(big_token);
    } else {
      String[] tokens = next.split("\\s+");
      for(String token : tokens){
        m_Tokens.add(new Token(token));
      }
    }
  }
}
