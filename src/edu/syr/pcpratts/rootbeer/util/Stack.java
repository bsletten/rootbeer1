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

package edu.syr.pcpratts.rootbeer.util;

import java.util.ArrayList;
import java.util.List;

public class Stack<T> {
  
  private List<T> m_Data;
  
  public Stack(){
    m_Data = new ArrayList<T>();
  }
  
  public T top()          
  {
    return m_Data.get(m_Data.size()-1);
  }
  
  public void pop(){
    m_Data.remove(m_Data.size()-1);
  }
  
  public void push(T value){
    m_Data.add(value);
  }

  public int size() {
    return m_Data.size();
  }
}
