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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.baseconversion;

public class GpuList {
  
  private Object[] m_Items;
  private int m_Count;
    
  public GpuList(){
    m_Items = create(8);
    m_Count = 0;
  }
  
  public void add(Object item){
    if(m_Count == m_Items.length){
      realloc();
    }
    int count = m_Count;
    m_Items[count] = item;
    m_Count = (count + 1);
  }
  
  private void realloc() {
    Object[] new_items = create(m_Items.length * 2);
    for(int i = 0; i < m_Items.length; ++i){
      new_items[i] = m_Items[i];
    }
    m_Items = new_items;
  }
  
  public void remove(int index){
    for(int i = index; i < m_Count - 2; ++i){
      m_Items[i] = m_Items[i + 1];
    }
    m_Count--;
  }
  
  public Object get(int index){
    return m_Items[index];
  }

  public int size() {
    return m_Count;
  }

  private Object[] create(int size) {
    return new Object[size];
  }

  void set(int ret_index, Object value) {
    m_Items[ret_index] = value;
  }
}
