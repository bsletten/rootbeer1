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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Further tokenizes tokens that have special C++ strings in them
 * @author pcpratts
 */
public class PreSemiExpression extends AbstractTokenizer {

  private List<String> m_SplitStrings;
  private List<Token> m_TempTokens;

  public PreSemiExpression(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
    m_SplitStrings = new ArrayList<String>();

    m_SplitStrings.add("...");
    m_SplitStrings.add("<<=");
    m_SplitStrings.add(">>=");
    
    m_SplitStrings.add("<:");
    m_SplitStrings.add(":>");
    m_SplitStrings.add("<%");
    m_SplitStrings.add("%>");
    m_SplitStrings.add("%:");
    
    m_SplitStrings.add("::");
    m_SplitStrings.add("->");
    m_SplitStrings.add("++");    
    m_SplitStrings.add("--");

    m_SplitStrings.add("<<");
    m_SplitStrings.add(">>");
    m_SplitStrings.add("==");
    m_SplitStrings.add("!=");
    
    m_SplitStrings.add("&&");
    m_SplitStrings.add("||");
    
    m_SplitStrings.add("+=");
    m_SplitStrings.add("-=");
    m_SplitStrings.add("*=");
    m_SplitStrings.add("/=");
    m_SplitStrings.add("%=");
    m_SplitStrings.add("&=");
    m_SplitStrings.add("^=");
    m_SplitStrings.add("|=");
    
    m_SplitStrings.add("<=");
    m_SplitStrings.add(">=");
    
    m_SplitStrings.add("<");
    m_SplitStrings.add(">");
    m_SplitStrings.add("-");
    m_SplitStrings.add("+");
    m_SplitStrings.add("=");
    m_SplitStrings.add("%");
    m_SplitStrings.add("|");
    m_SplitStrings.add("*");
    m_SplitStrings.add("&");
    m_SplitStrings.add("/");
    m_SplitStrings.add("(");
    m_SplitStrings.add(")");
    m_SplitStrings.add("?");
    m_SplitStrings.add("~");
    m_SplitStrings.add("[");
    m_SplitStrings.add("]");
    m_SplitStrings.add("{");
    m_SplitStrings.add("}");
    m_SplitStrings.add(":");
    m_SplitStrings.add(";");
    m_SplitStrings.add("!");
    m_SplitStrings.add(",");
    m_SplitStrings.add(".");
  }

  @Override
  protected void parseNextToken() {
    Token big_token = m_LowerLevel.next();
    if(big_token.isImmutable() || big_token.isNewLine()){
      m_Tokens.add(big_token);
    } else {
      String next = big_token.getString();
      m_TempTokens = new ArrayList<Token>();
      m_TempTokens.add(new Token(next));

      for(String splitter : m_SplitStrings){
        split(splitter);
      }
      m_Tokens = m_TempTokens;
    }
  }

  private void split(String splitter) {
    List<Token> new_tokens = new ArrayList<Token>();
    for(Token curr : m_TempTokens){
      if(curr.isImmutable()){
        new_tokens.add(curr);
        continue;
      }
      TokenizationSplitter splitter_engine = new TokenizationSplitter();
      List<Token> tokens = splitter_engine.split(curr, splitter);
      new_tokens.addAll(tokens);
    }
    m_TempTokens = new_tokens;
  }

  private List<String> sizeTokenize(int size, String str){
    List<String> ret = new ArrayList<String>();
    String curr = "";
    for(int i = 0; i < str.length(); i += size){
      String sub = str.substring(i);
      for(int j = 0; j < size && j < sub.length(); ++j){
        curr += sub.charAt(j);
      }
      ret.add(curr);
      curr = "";
    }
    return ret;
  }

  private void addEnd(List<Token> new_tokens, List<String> number_tokens, String splitter) {
    for(String curr : number_tokens){
      if(curr.equals(splitter) == false)
        return;
      Token new_token = new Token(splitter);
      new_token.setImmutable();
      new_tokens.add(new_token);
    }
  }

  private List<String> reverse(List<String> number_tokens) {
    Collections.reverse(number_tokens);
    return number_tokens;
  }

  private List<String> trimTokens(String[] tokens) {
    List<String> ret = new ArrayList<String>();
    for(int i = 0; i < tokens.length; ++i){
      String token = tokens[i];
      token = token.trim();
      if(token.equals(""))
        continue;
      ret.add(token);
    }
    return ret;
  }
}
