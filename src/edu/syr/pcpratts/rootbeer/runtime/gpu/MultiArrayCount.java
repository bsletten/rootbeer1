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

package edu.syr.pcpratts.rootbeer.runtime.gpu;

public class MultiArrayCount {

  private int m_AllocCount;
  private long m_AllocSize;
  
  public MultiArrayCount(long total_size, long max_alloc_size){
    int alignment = 8;
    long total_blocks = align(total_size, alignment);
    m_AllocSize = 256*1024*1024L;
    m_AllocCount = ((int) (total_blocks / m_AllocSize)) - 2;
  }
  
  private long align(long size, long alignment){
    long mod = size % alignment;
    return size - mod;
  }
  
  public int getAllocCount(){
    return m_AllocCount;
  }
  
  public long getAllocSize(){
    return m_AllocSize;
  }
}