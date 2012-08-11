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

import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import edu.syr.pcpratts.rootbeer.util.IntStack;
import java.util.List;

public abstract class BasicMemory extends Memory {

  protected ExtendedByteArray mData;
  protected IntStack mAddressStack;
  protected boolean mDebug;
  protected final long mSize;
  
  protected int loffset0;
  protected int loffset1;
  protected int loffset2;
  protected int loffset3;
  protected int loffset4;
  protected int loffset5;
  protected int loffset6;
  protected int loffset7;
  
  protected int ioffset0;
  protected int ioffset1;
  protected int ioffset2;
  protected int ioffset3;
  
  protected int soffset0;
  protected int soffset1;

  public BasicMemory(long size){
    mData = new ExtendedByteArray(size);
    mSize = size;
    mAddressStack = new IntStack();
    mDebug = false;
  }

  public long size(){
    return mSize;
  }
  
  public List<byte[]> getBuffer(){
    return mData.buffer();
  }
  
  public byte readByte(){
    byte ret = mData.get(getPointer());
    incPointer(1);
    return ret;
  }

  public boolean readBoolean(){
    byte b = readByte();
    if(b != 0)
      return true;
    return false;
  }
  
  public int readInt(){
    long ptr = getPointer();
    int value = (int) (((int) mData.get(ptr+ioffset3) << 24) & 0xff000000);
    value |= (int) (((int) mData.get(ptr+ioffset2) << 16) & 0x00ff0000);
    value |= (int) (((int) mData.get(ptr+ioffset1) << 8) & 0x0000ff00);
    value |= (int) ((int) mData.get(ptr+ioffset0) & 0x000000ff);

    incPointer(4);
    return value;
  }

  @Override
  public long readRef(){
    long ret = readInt();
    ret <<= 4;
    return ret;
  }
  
  @Override
  public void writeRef(long value){
    value = value >> 4;
    writeInt((int) value);
  }
  
  public short readShort(){
    long ptr = getPointer();
    short value = (short) (((short) mData.get(ptr+soffset1) << 8) & 0x0000ff00);
    value |=  (short) (((short) mData.get(ptr+soffset0)) & 0x000000ff);

    incPointer(2);
    return value;
  }

  public float readFloat(){
    int intValue = readInt();
    return Float.intBitsToFloat(intValue);
  }

  public double readDouble(){
    long longValue = readLong();
    return Double.longBitsToDouble(longValue);
  }

  public long readLong(){
    long ptr = getPointer();
    long value = (((long) mData.get(ptr+loffset7) << 56) & 0xff00000000000000L);
    value |= (((long) mData.get(ptr+loffset6) << 48)     & 0x00ff000000000000L);
    value |= (((long) mData.get(ptr+loffset5) << 40)     & 0x0000ff0000000000L);
    value |= (((long) mData.get(ptr+loffset4) << 32)     & 0x000000ff00000000L);
    value |= (((long) mData.get(ptr+loffset3) << 24)     & 0x00000000ff000000L);
    value |= (((long) mData.get(ptr+loffset2) << 16)     & 0x0000000000ff0000L);
    value |= (((long) mData.get(ptr+loffset1) << 8)      & 0x000000000000ff00L);
    value |= ((long) mData.get(ptr+loffset0)               & 0x00000000000000ffL);

    incPointer(8);
    return value;
  }

  public void writeByte(byte value){
    long ptr = getPointer();
    mData.set(ptr, value);
    incPointer(1);
  }
  
  public void writeBoolean(boolean value){
    if(value)
      writeByte((byte) 1);
    else
      writeByte((byte) 0);
  }

  public void writeInt(int value){
    long ptr = getPointer();
    mData.set(ptr+ioffset0, (byte) ((int) (value) & 0xff));
    mData.set(ptr+ioffset1, (byte) ((int) (value >> 8) & 0xff));
    mData.set(ptr+ioffset2, (byte) ((int) (value >> 16) & 0xff));
    mData.set(ptr+ioffset3, (byte) ((int) (value >> 24) & 0xff));
    incPointer(4);
  }

  public void writeShort(short value){
    long ptr = getPointer();
    mData.set(ptr+soffset0, (byte) ((int) (value) & 0xff));
    mData.set(ptr+soffset1, (byte) ((int) (value >> 8) & 0xff));
    incPointer(2);
  }

  public void writeFloat(float value){
    int intValue = Float.floatToIntBits(value);
    writeInt(intValue);
  }

  public void writeDouble(double value){
    long longValue = Double.doubleToLongBits(value);
    writeLong(longValue);
  }

  public void writeLong(long value){
    long ptr = getPointer();
    mData.set(ptr+loffset0, (byte) ((int) (value) & 0xff));
    mData.set(ptr+loffset1, (byte) ((int) (value >> 8) & 0xff));
    mData.set(ptr+loffset2, (byte) ((int) (value >> 16) & 0xff));
    mData.set(ptr+loffset3, (byte) ((int) (value >> 24) & 0xff));
    mData.set(ptr+loffset4, (byte) ((int) (value >> 32) & 0xff));
    mData.set(ptr+loffset5, (byte) ((int) (value >> 40) & 0xff));
    mData.set(ptr+loffset6, (byte) ((int) (value >> 48) & 0xff));
    mData.set(ptr+loffset7, (byte) ((int) (value >> 56) & 0xff));
    incPointer(8);
  }

  public void setAddress(long address){
    setPointer(address);
  }

  public void incrementAddress(int offset){
    incPointer(offset);
  }

  public void pushAddress() {
    mAddressStack.push(getPointer());
  }

  public void popAddress() {
    setPointer(mAddressStack.pop());
  }

  public void finishCopy(long size) {
  }


  public void finishRead() {
    //nothing to do
  }

  public void readIntArray(int[] array, int size){
    for(int i = 0; i < size; ++i){
      array[i] = readInt();
    }
  }
}
