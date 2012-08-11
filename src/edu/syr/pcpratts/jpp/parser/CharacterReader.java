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
import java.util.Iterator;

/**
 * Read a single character at a time from a BufferedReader.  If the character
 * is an escape character, read two.
 * @author pcpratts
 */
public class CharacterReader implements Iterator<Token> {

  private BufferedReader m_Reader;
  private String m_NextOutput;
  private boolean m_NeedToProcessHasNext;
  private boolean m_HasNext;

  public CharacterReader(BufferedReader reader){
    m_Reader = reader;
    m_NeedToProcessHasNext = true;
    m_HasNext = false;
  }

  public boolean hasNext(){
    if(m_NeedToProcessHasNext == false)
      return m_HasNext;
    processHasNext();
    m_NeedToProcessHasNext = false;
    return m_HasNext;
  }

  private void processHasNext(){
    try {
      int c = m_Reader.read();
      if(c == -1){
        m_HasNext = false;
        return;
      }
      //page break
      if(c == 0x0c){
        c = '\n';
      }
      if(c == '\\'){
        c = m_Reader.read();
        if(c == 0x0c){
          c = '\n';
        }
        if(c == -1){
          m_HasNext = false;
          m_NextOutput = "\\";
        } else {
          m_NextOutput = "\\" + (char) c;
          m_HasNext = true; 
        }
      } else {
        m_NextOutput = "" + (char) c;
        m_HasNext = true;
      }
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
    m_NeedToProcessHasNext = false;
  }

  public Token next() {
    if(m_HasNext == false)
      throw new RuntimeException("end of stream");
    if(m_NeedToProcessHasNext)
      throw new RuntimeException("need to process hasNext");
    m_NeedToProcessHasNext = true;
    return new Token(m_NextOutput);
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
