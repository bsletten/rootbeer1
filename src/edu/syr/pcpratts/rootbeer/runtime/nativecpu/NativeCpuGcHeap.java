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

package edu.syr.pcpratts.rootbeer.runtime.nativecpu;

import edu.syr.pcpratts.rootbeer.runtime.gpu.GcHeap;
import edu.syr.pcpratts.rootbeer.runtime.gpu.GpuDevice;
import edu.syr.pcpratts.rootbeer.runtime.memory.BasicMemory;
import edu.syr.pcpratts.rootbeer.runtime.memory.BasicSwappedMemory;
import edu.syr.pcpratts.rootbeer.runtime.memory.BasicUnswappedMemory;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import java.util.ArrayList;
import java.util.List;

public class NativeCpuGcHeap extends GcHeap {

  public NativeCpuGcHeap(GpuDevice device){
    super(device);  
    allocateMemory();
  }
  
  @Override
  protected void allocateMemory() {
    mBufferSize = 128*1024*1024L;
    mToSpaceMemory = new BasicSwappedMemory(mBufferSize);
    mTextureMemory = new BasicSwappedMemory(mBufferSize);
    mHandlesMemory = new BasicSwappedMemory(mBufferSize);
    mHeapEndPtrMemory = new BasicSwappedMemory(8);
    mGcInfoSpaceMemory = new BasicSwappedMemory(mGcInfoSpaceSize);
    mExceptionsMemory = new BasicSwappedMemory(mBufferSize);
  }

  @Override
  protected void makeSureReadyForUsingGarbageCollector() {
    
  }
 
  public List<Memory> getMemory(){
    List<Memory> ret = new ArrayList<Memory>();
    ret.add(mToSpaceMemory);
    ret.add(mHandlesMemory);
    ret.add(mHeapEndPtrMemory);
    ret.add(mGcInfoSpaceMemory);
    ret.add(mExceptionsMemory);
    return ret;
  }
}