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

public class Token {

  private String m_String;
  private boolean m_Immutable;
  private boolean m_Preprocessor;
  private boolean m_Comment;
  private boolean m_NewLine;

  public Token(String str){
    m_String = str;
    m_Immutable = false;
    if(stringIsComment())
      m_Comment = true;
    if(stringIsPreprocessor())
      m_Preprocessor = true;
  }

  public void setImmutable(){
    m_Immutable = true;
  }

  public boolean isComment(){
    return m_Comment;
  }

  public boolean isPreprocessor(){
    return m_Preprocessor;
  }

  public boolean isImmutable(){
    return m_Immutable;
  }

  public String getString(){
    return m_String;
  }

  @Override
  public String toString(){
    return m_String;
  }

  private boolean stringIsComment() {
    if(m_String.startsWith("//"))
      return true;
    if(m_String.startsWith("/*"))
      return true;
    return false;
  }

  private boolean stringIsPreprocessor() {
    if(m_String.startsWith("#"))
      return true;
    return false;
  }

  void setNewLine() {
    m_NewLine = true;
  }

  public boolean isNewLine(){
    if(m_String.equals("\n"))
      return true;
    if(m_String.equals("\r"))
      return true;
    return m_NewLine;
  }

  void setComment() {
    m_Comment = true;
  }
}
