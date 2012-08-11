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

public class TrigraphReader implements Iterator<Token> {

  private Iterator<Token> m_LowerLevel;
  private Token m_Curr;
  private Token m_Next;
  private Token m_NextNext;

  private String m_ThreeCharacters;
  private String m_TriGraph;

  private int m_CharsLeft;

  public TrigraphReader(Iterator<Token> iter) {
    m_LowerLevel = iter;
    if(m_LowerLevel.hasNext())
      m_Curr = m_LowerLevel.next();
    if(m_LowerLevel.hasNext())
      m_Next = m_LowerLevel.next();
    if(m_LowerLevel.hasNext())
      m_NextNext = m_LowerLevel.next();
    m_CharsLeft = -1;
  }

  public boolean hasNext() {
    if(m_LowerLevel.hasNext() == false){
      if(m_CharsLeft == -1){
        m_CharsLeft = 4;
      }
      m_CharsLeft--;
      if(m_CharsLeft > 0)
        return true;
      return false;
    } else {
      return true;
    }
  }

  public Token next() {
    Token ret;
    if(isTrigraph()){
      ret = new Token(m_TriGraph);
      read();
      if(m_LowerLevel.hasNext())
        read();
      if(m_LowerLevel.hasNext())
        read();
    } else {
      ret = m_Curr;
      read();
    }
    return ret;
  }

  private void read(){
    m_Curr = m_Next;
    m_Next = m_NextNext;

    if(m_CharsLeft != -1)
      m_NextNext = new Token("");
    else
      m_NextNext = m_LowerLevel.next();
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private boolean isTrigraph() {
    m_ThreeCharacters = m_Curr.getString()+m_Next.getString()+m_NextNext.getString();
    if(m_ThreeCharacters.equals("??=")){
      m_TriGraph = "#";
      return true;
    }
    if(m_ThreeCharacters.equals("??/")){
      m_TriGraph = "\\";
      return true;
    }
    if(m_ThreeCharacters.equals("??'")){
      m_TriGraph = "^";
      return true;
    }
    if(m_ThreeCharacters.equals("??(")){
      m_TriGraph = "[";
      return true;
    }
    if(m_ThreeCharacters.equals("??)")){
      m_TriGraph = "]";
      return true;
    }
    if(m_ThreeCharacters.equals("??!")){
      m_TriGraph = "|";
      return true;
    }
    if(m_ThreeCharacters.equals("??-")){
      m_TriGraph = "~";
      return true;
    }
    if(m_ThreeCharacters.equals("??>")){
      m_TriGraph = "}";
      return true;
    }
    if(m_ThreeCharacters.equals("??!")){
      m_TriGraph = "|";
      return true;
    }
    return false;
  }
}
