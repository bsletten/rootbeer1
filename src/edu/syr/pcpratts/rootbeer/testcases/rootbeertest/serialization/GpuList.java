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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

public class GpuList<T> {
  
  private T[] m_Items;
  private int m_Count;
    
  public GpuList(){
    m_Items = create(8);
    m_Count = 0;
  }
  
  public void add(T item){
    if(m_Count == m_Items.length){
      realloc();
    }
    m_Items[m_Count] = item;
    m_Count++;
  }

  private void realloc() {
    T[] new_items = create(m_Items.length * 2);
    for(int i = 0; i < m_Items.length; ++i){
      new_items[i] = m_Items[i];
    }
    m_Items = new_items;
  }
  
  public void remove(int index){
    for(int i = index; i < m_Count - 1; ++i){
      m_Items[i] = m_Items[i + 1];
    }
    m_Count--;
  }
  
  public T get(int index){
    Object ret = m_Items[index];
    return (T) ret;
  }

  public int size() {
    return m_Count;
  }

  private T[] create(int size) {
    return (T[]) new Object[size];
  }
  
  public static void main(String[] args){
    GpuList<Integer> int_list = new GpuList<Integer>();
    for(int i = 0; i < 40; ++i)
      int_list.add(i);
    int_list.remove(20);
    for(int i = 0; i < 39; ++i){
      System.out.println(int_list.get(i));
    }
  }
}
