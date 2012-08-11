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

package edu.syr.pcpratts.rootbeer.test;

import edu.syr.pcpratts.rootbeer.runtime.memory.BasicSwappedMemory;

public class AlignmentCheckedMemory extends BasicSwappedMemory {

  public AlignmentCheckedMemory(int size){
    super(size);
  }

  @Override
  public void checkAlignment(int alignment){
    long ptr = getPointer();
    if(ptr % alignment != 0){
      throw new RuntimeException("alignment of: "+alignment+" not working.  ptr: "+ptr);
    }
  }

  @Override
  public int readInt(){
    checkAlignment(4);
    return super.readInt();
  }

  @Override
  public short readShort(){
    checkAlignment(2);
    return super.readShort();
  }

  @Override
  public float readFloat(){
    checkAlignment(4);
    return super.readFloat();
  }

  @Override
  public double readDouble(){
    checkAlignment(8);
    return super.readDouble();
  }

  @Override
  public long readLong(){
    checkAlignment(8);
    return super.readLong();
  }

  @Override
  public void writeInt(int value){
    checkAlignment(4);
    super.writeInt(value);
  }

  @Override
  public void writeShort(short value){
    checkAlignment(2);
    super.writeShort(value);
  }

  @Override
  public void writeFloat(float value){
    checkAlignment(4);
    super.writeFloat(value);
  }

  @Override
  public void writeDouble(double value){
    checkAlignment(8);
    super.writeDouble(value);
  }

  @Override
  public void writeLong(long value){
    checkAlignment(8);
    super.writeLong(value);
  }

}
