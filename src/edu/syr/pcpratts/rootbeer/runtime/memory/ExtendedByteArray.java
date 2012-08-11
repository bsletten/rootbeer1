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

package edu.syr.pcpratts.rootbeer.runtime.memory;

import java.util.ArrayList;
import java.util.List;

public class ExtendedByteArray {
  
  private long m_Size;
  private List<byte[]> m_Data;
  private int m_MaxSize;
    
  public ExtendedByteArray(long size){
    m_Size = size;
    m_MaxSize = 1024*1024*1024;
    m_Data = new ArrayList<byte[]>();
    int num_arrays = (int) (m_Size / m_MaxSize);
    for(int i = 0; i < num_arrays; ++i){
      m_Data.add(new byte[m_MaxSize]);
    }
    int left_over = (int) (m_Size % m_MaxSize);
    if(left_over != 0)
      m_Data.add(new byte[left_over]);
  }
  
  public List<byte[]> buffer(){
    return m_Data; 
  }
  
  public byte get(long index){
    try {
      int array = (int) (index / m_MaxSize);
      int offset = (int) (index % m_MaxSize);

      byte[] data = m_Data.get(array);
      return data[offset];
    } catch(Throwable ex){
      System.out.println("Error in get: "+index);
      int array = (int) (index / m_MaxSize);
      int offset = (int) (index % m_MaxSize);
      System.out.println("array: "+array);
      System.out.println("offset: "+offset);
      System.out.println("max size: "+m_MaxSize);    
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }
  
  public void set(long index, byte value){    
    int array = (int) (index / m_MaxSize);
    int offset = (int) (index % m_MaxSize);
    
    byte[] data = m_Data.get(array);
    data[offset] = value;
  }
  
  public static void main(String[] args){
    long size = 2*1024*1024*1024L;
    ExtendedByteArray array = new ExtendedByteArray(size);
    for(long i = 0; i < size; ++i){
      array.set(i, (byte) i);        
    }
    for(long i = 0; i < size; ++i){
      System.out.println(array.get(i));
    }
  }
}
