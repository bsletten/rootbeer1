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

import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FastMemory extends Memory {

  private long m_CpuBase;
  private long m_SpaceSize;
  private long m_Reserve;
  private MemPointer m_StaticMemPointer;
  private MemPointer m_InstanceMemPointer;
  private MemPointer m_CurrMemPointer;
    
  public FastMemory(long cpu_base_address, AtomicLong instance_pointer, 
    AtomicLong static_pointer, long space_size){
    
    m_CpuBase = cpu_base_address;    
    m_SpaceSize = space_size;
    m_InstanceMemPointer = new MemPointer(instance_pointer);
    m_StaticMemPointer = new MemPointer(static_pointer);
    m_CurrMemPointer = m_InstanceMemPointer;
    m_Reserve = 1024;
  }    
  
  private long currPointer(){
    return m_CurrMemPointer.m_Pointer;
  }  
  
  @Override
  public byte readByte() {
    byte ret = doReadByte(currPointer(), m_CpuBase);
    incrementAddress(1);
    return ret;
  }

  @Override
  public boolean readBoolean() {
    boolean ret = doReadBoolean(currPointer(), m_CpuBase);
    incrementAddress(1);
    return ret;
  }

  @Override
  public short readShort() {
    short ret = doReadShort(currPointer(), m_CpuBase);
    incrementAddress(2);
    return ret;
  }

  @Override
  public int readInt() {
    int ret = doReadInt(currPointer(), m_CpuBase);
    incrementAddress(4);
    return ret;
  }

  @Override
  public float readFloat() {
    float ret = doReadFloat(currPointer(), m_CpuBase);
    incrementAddress(4);
    return ret;
  }

  @Override
  public double readDouble() {
    double ret = doReadDouble(currPointer(), m_CpuBase);
    incrementAddress(8);
    return ret;
  }

  @Override
  public long readLong() {
    long ret = doReadLong(currPointer(), m_CpuBase);
    incrementAddress(8);
    return ret;
  }
  
  @Override
  public long readRef() {
    long ret = readInt();
    ret = ret << 4;
    return ret;
  }


  @Override
  public void writeByte(byte value) {
    doWriteByte(currPointer(), value, m_CpuBase);
    incrementAddress(1);
  }

  @Override
  public void writeBoolean(boolean value) {
    doWriteBoolean(currPointer(), value, m_CpuBase);
    incrementAddress(1);
  }

  @Override
  public void writeShort(short value) {
    doWriteShort(currPointer(), value, m_CpuBase);
    incrementAddress(2);
  }

  @Override
  public void writeInt(int value) {
    doWriteInt(currPointer(), value, m_CpuBase);
    incrementAddress(4);
  }

  @Override
  public void writeRef(long value) {
    value = value >> 4;
    writeInt((int) value);
  }
  
  @Override
  public void writeFloat(float value) {
    doWriteFloat(currPointer(), value, m_CpuBase);
    incrementAddress(4);
  }

  @Override
  public void writeDouble(double value) {
    doWriteDouble(currPointer(), value, m_CpuBase);
    incrementAddress(8);
  }

  @Override
  public void writeLong(long value) {
    doWriteLong(currPointer(), value, m_CpuBase);
    incrementAddress(8);
  }
  
  public native byte doReadByte(long ptr, long cpu_base);
  public native boolean doReadBoolean(long ptr, long cpu_base);
  public native short doReadShort(long ptr, long cpu_base);
  public native int doReadInt(long ptr, long cpu_base);
  public native float doReadFloat(long ptr, long cpu_base);
  public native double doReadDouble(long ptr, long cpu_base);
  public native long doReadLong(long ptr, long cpu_base);
  public native void doWriteByte(long ptr, byte value, long cpu_base);
  public native void doWriteBoolean(long ptr, boolean value, long cpu_base);
  public native void doWriteShort(long ptr, short value, long cpu_base);
  public native void doWriteInt(long ptr, int value, long cpu_base);
  public native void doWriteFloat(long ptr, float value, long cpu_base);
  public native void doWriteDouble(long ptr, double value, long cpu_base);
  public native void doWriteLong(long ptr, long value, long cpu_base);

  @Override
  public void clearHeapEndPtr() {
    m_CurrMemPointer.clearHeapEndPtr();
  }

  @Override
  public long getHeapEndPtr() {
    return m_CurrMemPointer.m_HeapEnd;
  }

  @Override
  public long getPointer(){
    return m_CurrMemPointer.m_Pointer;
  }
  
  @Override
  public void setAddress(long address) {
    m_CurrMemPointer.setAddress(address);
  }

  @Override
  public void incrementAddress(int offset) {
    m_CurrMemPointer.incrementAddress(offset);
  }
    
  @Override
  public long mallocWithSize(int size){
    return m_CurrMemPointer.mallocWithSize(size);
  }
  
  @Override
  public void setPointer(long ptr){
    setAddress(ptr);
  }

  @Override
  public void incPointer(long value){
    incrementAddress((int) value);
  }

  @Override
  public void pushAddress() {
    m_CurrMemPointer.pushAddress();
  }

  @Override
  public void popAddress() {
    m_CurrMemPointer.popAddress();
  }

  @Override
  public List<byte[]> getBuffer() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void finishCopy(long size) {
    
  }

  @Override
  public void finishRead() {
    
  }

  @Override
  public void readIntArray(int[] array, int size) {
    for(int i = 0; i < size; ++i){
      array[i] = readInt();
    }
  }

  @Override
  public void useInstancePointer() {
    m_CurrMemPointer = m_InstanceMemPointer;
  }

  @Override
  public void useStaticPointer() {
    m_CurrMemPointer = m_StaticMemPointer;
  }
  
  @Override
  public void align(){
    m_CurrMemPointer.align();
  }
  
  private class MemPointer {
   
    private PointerStack m_Stack; 
    private AtomicLong m_EndPointer;
    private long m_Pointer;
    private long m_HeapEnd;
    
    public MemPointer(AtomicLong end_ptr){
      m_Stack = new PointerStack();
      m_EndPointer = end_ptr;
    }
    
    public void popAddress() {
      m_Pointer = m_Stack.pop();
    }
    
    public void pushAddress() {
      m_Stack.push(m_Pointer);
    }

    public long mallocWithSize(int size){
      malloc();
      int mod = size % 16;
      if(mod != 0)
        size += (16 - mod);

      long ret = m_EndPointer.getAndAdd(size);              
      if(ret + size + m_Reserve > m_SpaceSize){
        throw new OutOfMemoryError();
      }        
      m_Pointer = ret;
      if(ret > m_HeapEnd)
        m_HeapEnd = ret;
      
      return ret;
    }
  
    public long malloc(){
      //align all new items on 8 bytes
      long mod = m_HeapEnd % 8;
      if(mod != 0)
        m_HeapEnd += (8 - mod);
      m_Pointer = m_HeapEnd;
      return m_HeapEnd;
    }

    private void clearHeapEndPtr() {      
      m_HeapEnd = 0;
      m_Pointer = 0;
      m_EndPointer.set(0);
    }

    private void setAddress(long address) {
      m_Pointer = address;
      if(address > m_HeapEnd)
        m_HeapEnd = address;
    }

    private void incrementAddress(int offset) { 
      m_Pointer += offset;
      if(m_Pointer > m_HeapEnd){
        m_HeapEnd = m_Pointer;
      } 
    }

    private void align() {
      long mod = m_Pointer % 8;
      if(mod != 0){
        m_Pointer += (8 - mod);
      }
      if(m_Pointer > m_HeapEnd){
        m_HeapEnd = m_Pointer;
      }
      if(m_Pointer > m_EndPointer.get()){
        m_EndPointer.set(m_Pointer);
      }
    }
  }
}