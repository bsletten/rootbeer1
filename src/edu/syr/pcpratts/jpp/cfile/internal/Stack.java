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

package edu.syr.pcpratts.jpp.cfile.internal;

import java.util.LinkedList;
import java.util.List;

public class Stack<T> {
  
  private List<T> m_Elements;
  
  public Stack(){
    m_Elements = new LinkedList<T>();
  }
  
  public T top(){
    return m_Elements.get(m_Elements.size()-1);
  }
  
  public T pop(){
    T ret = top();
    m_Elements.remove(m_Elements.size()-1);
    return ret;
  }
  
  public void push(T element){
    m_Elements.add(element);
  }
  
  public int size(){
    return m_Elements.size();
  }
  
  public boolean isEmpty(){
    return m_Elements.isEmpty();  
  }
  
  public List<T> getList(){
    return m_Elements;
  }
}
