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

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import java.util.ArrayList;
import java.util.List;

public class PointerStack {

  private List<Long> m_Stack;
  private int m_Index;
  private final int m_DefaultDepth;
  
  public PointerStack(){
    m_DefaultDepth = 16;
    m_Stack = new ArrayList<Long>(m_DefaultDepth);
    m_Index = 0;
    for(int i = 0; i < m_DefaultDepth; ++i)
      m_Stack.add(0L);
  }
  
  public void push(long value){
    m_Index++;
    if(m_Index < m_DefaultDepth){
      m_Stack.set(m_Index, value);
      return;
    } else {   
      while(m_Stack.size() <= m_Index){
        m_Stack.add(0L);
      }
      m_Stack.set(m_Index, value);
    }
  }
  
  public long pop(){
    long ret = m_Stack.get(m_Index);
    m_Index--;
    return ret;
  }
}
