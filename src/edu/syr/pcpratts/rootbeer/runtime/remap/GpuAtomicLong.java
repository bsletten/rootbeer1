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

package edu.syr.pcpratts.rootbeer.runtime.remap;

import java.io.Serializable;

public class GpuAtomicLong extends Number implements Serializable {

  private volatile long m_Value;
  
  public GpuAtomicLong(long value){
    m_Value = value;
  }
  
  public GpuAtomicLong(){
    m_Value = 0;
  }
  
  public synchronized long get(){
    return m_Value;
  }
  
  public synchronized void set(long value){
    m_Value = value;
  }
  
  public synchronized void lazySet(long value){
    m_Value = value;
  }
  
  public synchronized long getAndSet(long value){
    long ret = m_Value;
    m_Value = value;
    return ret;
  }
  
  public synchronized boolean compareAndSet(long expect, long update){
    if(m_Value == expect){
      m_Value = update;
      return true;
    } else {
      return false;
    }
  }
  
  public synchronized boolean weakCompareAndSet(long expect, long update){
    if(m_Value == expect){
      m_Value = update;
      return true;
    } else {
      return false;
    }
  }
  
  public synchronized long getAndIncrement(){
    long ret = m_Value;
    m_Value++;
    return ret;
  }
  
  public synchronized long getAndDecrement(){
    long ret = m_Value;
    m_Value--;
    return ret;
  }
  
  public synchronized long getAndAdd(long value){
    long ret = m_Value;
    m_Value += value;
    return ret;
  }  
  
  public synchronized long incrementAndGet(){
    m_Value++;
    return m_Value;
  }
  
  public synchronized long decrementAndGet(){
    m_Value--;
    return m_Value;
  }
    
  public synchronized long addAndGet(long value){
    m_Value += value;
    return m_Value;
  }  
  
  @Override
  public String toString(){
    long value = get();
    return ""+value;
  }
  
  @Override
  public int intValue() {
    synchronized(this){
      return (int) m_Value;
    }
  }

  @Override
  public long longValue() {
    synchronized(this){
      return m_Value;
    }
  }

  @Override
  public float floatValue() {
    synchronized(this){
      return (float) m_Value;
    }
  }

  @Override
  public double doubleValue() {    
    synchronized(this){
      return (double) m_Value;
    }
  }  
}
