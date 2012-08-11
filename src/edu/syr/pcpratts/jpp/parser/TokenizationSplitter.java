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
import java.util.List;

public class TokenizationSplitter {

  public List<Token> split(Token tok, String splitter){
    List<Token> ret = new ArrayList<Token>();
    String curr = "";
    String tok_str = tok.getString();
    for(int i = 0; i < tok_str.length(); ){
      String remainder = tok_str.substring(i);
      if(remainder.startsWith(splitter)){
        if(curr.equals("") == false){
          ret.add(new Token(curr));
          curr = "";
        }
        Token new_token = new Token(splitter);
        new_token.setImmutable();
        ret.add(new_token);
        i += splitter.length();
      } else {
        curr += remainder.charAt(0);
        i += 1;
      }
    }
    if(curr.equals("") == false){
      Token new_token = new Token(curr);
      if(curr.equals(splitter))
        new_token.setImmutable();
      ret.add(new_token);
    }    
    return ret;
  }
  
  public static void main(String[] args){
    Token tok = new Token("if(( var = new Hello()) == 0){");
    TokenizationSplitter split_engine = new TokenizationSplitter();
    List<Token> tokens = split_engine.split(tok, "(");
    for(Token token : tokens){
      System.out.println(token.getString());
    }
  }
}
