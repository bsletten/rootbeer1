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

public class ConcreteIteratorEx<T> implements Iterator<T> {

  private Iterator<T> m_Iterator;
  private List<T> m_TokenList;
  private boolean m_NeedToCallHasNext;

  public ConcreteIteratorEx(Iterator<T> iter){
    m_Iterator = iter;
    m_TokenList = new ArrayList<T>();
  }

  public boolean hasNext() {
    m_NeedToCallHasNext = false;
    if(m_TokenList.isEmpty() == false)
      return true;
    return m_Iterator.hasNext();
  }

  public T next() {
    m_NeedToCallHasNext = true;
    if(m_TokenList.isEmpty() == false){
      T ret = m_TokenList.get(0);
      m_TokenList.remove(0);
      return ret;
    } else {
      return m_Iterator.next();
    }
  }
  
  public T peek(int tokens){ 
    while(m_TokenList.size() <= tokens){
      if(m_NeedToCallHasNext){
        if(m_Iterator.hasNext() == false){
          return null;
        }
        m_TokenList.add(m_Iterator.next());
        m_NeedToCallHasNext = true;
      } else {
        m_TokenList.add(m_Iterator.next());
        m_NeedToCallHasNext = true;
      }
    }
    return m_TokenList.get(tokens);
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void putback(T item){
    List<T> new_tokens = new ArrayList<T>();
    new_tokens.add(item);
    for(T tok : m_TokenList)
      new_tokens.add(tok);
    m_TokenList = new_tokens;
  }
}
