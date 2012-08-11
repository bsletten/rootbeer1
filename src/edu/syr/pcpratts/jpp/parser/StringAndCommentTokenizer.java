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
import java.util.List;

/**
 * Tokenize a raw stream into Strings, Comments and Everything else
 * @author pcpratts
 */
public class StringAndCommentTokenizer implements Iterator<Token> {

  private Iterator<Token> m_Reader;
  private Token m_CurrChar;
  private Token m_NextChar;
  private String m_SpecialEndToken;
  private StringBuilder m_Ret;
  private boolean m_InComment;
  private boolean m_EndOfStream;

  private List<Token> m_NextNext;

  public StringAndCommentTokenizer(Iterator<Token> iter){
    m_Reader = iter;
    m_NextNext = new ArrayList<Token>();
    
    if(m_Reader.hasNext())
      m_CurrChar = m_Reader.next();
    else
      m_CurrChar = new Token("");
    
    if(m_Reader.hasNext())
      m_NextChar = m_Reader.next();
    else
      m_NextChar = new Token("");
    
    m_Ret = new StringBuilder();
  }

  public boolean hasNext(){
    if(m_NextNext.isEmpty() == false)
      return true;
    if(m_EndOfStream)
      return false;
    return m_Reader.hasNext();
  }

  public Token next(){
    try {
      if(m_NextNext.isEmpty() == false){
        Token next = m_NextNext.get(0);
        m_NextNext.remove(0);
        m_Ret = new StringBuilder();
        return next;
      }
      if(m_CurrChar.isImmutable()){
        Token ret1 = m_CurrChar;
        read();
        m_Ret = new StringBuilder();
        return ret1;
      }
      while(shouldTrim(m_CurrChar.getString()))
        read();
      if(m_CurrChar.isImmutable()){
        Token ret1 = m_CurrChar;
        read();
        m_Ret = new StringBuilder();
        return ret1;
      }
      Token ret = doNext();
      while(shouldTrim(m_CurrChar.getString()))
        read();
      m_Ret = new StringBuilder();
      return ret;
    } catch(Exception ex) {
      m_EndOfStream = true;
      if(m_Ret == null)
        m_Ret = new StringBuilder();
      String ret_string = m_Ret.toString();
      Token ret = new Token(ret_string.trim());
      return ret;
    }
  }

  private Token doNext() throws Exception {
    if(isSingleLineToken()){
      Token token = eatLine();
      m_InComment = false;
      token.setImmutable();
      return token;
    }
    if(isSpecialStartToken()){
      return readSpecial();
    } else {
      m_Ret = new StringBuilder();
      while(isSpecialStartToken() == false && isSingleLineToken() == false){
        m_Ret.append(m_CurrChar.getString());
        read();
      }
      Token ret = new Token(m_Ret.toString());
      return ret;
    }
  }

  private void read() throws Exception {
    m_CurrChar = m_NextChar;
    if(m_Reader.hasNext())
      m_NextChar = m_Reader.next();
    else
      throw new Exception("end of stream");
  }

  private boolean isSingleLineToken() {
    if(m_CurrChar.getString().equals("#"))
      return true;
    if(isSingeLineComment())
      return true;
    return false;
  }

  private boolean isSingeLineComment(){
    if(m_CurrChar.getString().equals("/") && m_NextChar.getString().equals("/")){
      m_InComment = true;
      return true;
    }
    return false;
  }

  private Token eatLine() throws Exception {
    m_Ret = new StringBuilder();
    m_Ret.append(m_CurrChar.getString());
    List<Token> all_ret = new ArrayList<Token>();
    boolean is_immutable = false;
    if(isSingeLineComment())
      is_immutable = true;
    while(true){
      read();
      if(isSpecialStartToken()){
        Token curr = new Token(m_Ret.toString());
        if(is_immutable)
          curr.setImmutable();
        all_ret.add(curr);
        all_ret.add(readSpecial());
        is_immutable = false;
        m_Ret = new StringBuilder();
      } else if(m_InComment == false && isSingeLineComment()){
        Token curr = new Token(m_Ret.toString());
        is_immutable = true;
        all_ret.add(curr);
        m_Ret = new StringBuilder();
      } else if(isNewLine(m_CurrChar.getString())){
        Token curr = new Token(m_Ret.toString());
        if(is_immutable)
          curr.setImmutable();
        all_ret.add(curr);
        Token newline = new Token("\n");
        newline.setNewLine();
        all_ret.add(newline);
        Token ret = all_ret.get(0);
        all_ret.remove(0);
        m_NextNext.addAll(all_ret);
        is_immutable = false;
        //ignore # lines like they are comments
        //ret.setComment();
        return ret;
      }
      m_Ret.append(m_CurrChar.getString());
    }
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private boolean isSpecialStartToken() {
    String two = m_CurrChar.getString() + m_NextChar.getString();
    if(two.equals("/*")){
      m_InComment = true;
      m_SpecialEndToken = "*/";
      return true;
    } else if(m_CurrChar.getString().equals("\"") && m_InComment == false){
      m_SpecialEndToken = "\"";
      return true;
    } else if(m_CurrChar.getString().equals("'") && m_InComment == false){
      m_SpecialEndToken = "\'";
      return true;
    }
    return false;
  }

  private boolean isSpecialEndToken() {
    String curr;
    if(m_SpecialEndToken.length() == 2){
      curr = m_CurrChar.getString() + m_NextChar.getString();
    } else {
      curr = m_CurrChar.getString();
    }
    if(curr.equals(m_SpecialEndToken)){
      if(m_SpecialEndToken.equals("*/")){
        m_InComment = false;
      }
      return true;
    }
    return false;
  }

  private boolean shouldTrim(String str) {
    if(isNewLine(str))
      return true;
    if(str.equals(" "))
      return true;
    return false;
  }

  private boolean isNewLine(String str){
    if(str.equals("\n"))
      return true;
    if(str.equals("\r"))
      return true;
    if(str.length() == 0)
      return false;
    if(str.charAt(0) == 0x0c){
      return true;
    }
    return false;
  }

  private Token readSpecial() throws Exception {
    String ret = null;
    try {
      ret = m_CurrChar.getString();
      read();
      while(isSpecialEndToken() == false){
        ret += m_CurrChar.getString();
        read();
      }
      read();
      if(m_SpecialEndToken.length() == 2)
        read();
      ret += m_SpecialEndToken;
      Token token = new Token(ret);
      token.setImmutable();
      return token;
    } catch(Exception ex){
      Token token = new Token(ret);
      token.setImmutable();
      return token;
    }

  }
}
