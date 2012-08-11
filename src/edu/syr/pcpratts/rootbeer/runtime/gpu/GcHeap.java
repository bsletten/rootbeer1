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

import edu.syr.pcpratts.rootbeer.Aug4th2011PerformanceStudy;
import edu.syr.pcpratts.rootbeer.Configuration;
import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.runtime.Serializer;
import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.CompiledKernel;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import edu.syr.pcpratts.rootbeer.runtime.memory.BufferPrinter;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class GcHeap {
  private List<CompiledKernel> mBlocks;

  protected final int mGcInfoSpaceSize = 64;
  private GpuDevice mDevice;

  private long m_PreviousRef;
  private long m_PreviousSize;

  protected long mBufferSize;
  protected Memory mToSpaceMemory;
  protected Memory mTextureMemory;
  protected Memory mHandlesMemory;
  protected Memory mHeapEndPtrMemory;
  protected Memory mToHandleMapMemory;
  protected Memory mGcInfoSpaceMemory;
  protected Memory mExceptionsMemory;

  private Serializer mGcObjectVisitor;
  private boolean mUsingGarbageCollector;
  private int m_CountWritten;
  private List<Long> m_HandlesList;

  private long mMaxToHandleMapAddress;

  private PartiallyCompletedParallelJob mWriteRet;

  private static Map<GpuDevice, GcHeap> mInstances = new HashMap<GpuDevice, GcHeap>();

  public static GcHeap v(GpuDevice device){
    if(mInstances.containsKey(device)){
      GcHeap ret = mInstances.get(device);
      ret.reset();
      return ret;
    }
    GcHeap ret = device.CreateHeap();
    //mInstances.put(device, ret);
    return ret;
  }
  
  private void reset(){
    mBlocks.clear();
    m_PreviousRef = 0;
    m_PreviousSize = 0;

    mToSpaceMemory.setAddress(0);
    mToSpaceMemory.clearHeapEndPtr();
    
    mHandlesMemory.setAddress(0);
    mHandlesMemory.clearHeapEndPtr();
    mHeapEndPtrMemory.setAddress(0);
    mHeapEndPtrMemory.clearHeapEndPtr();

    mGcObjectVisitor = null;
    mUsingGarbageCollector = false;
    m_CountWritten = 0;

    mMaxToHandleMapAddress = 0;

    mWriteRet = null;
  }
  
  protected GcHeap(GpuDevice device){
    mDevice = device;
    m_HandlesList = new ArrayList<Long>();
  }
  
  private void writeOneRuntimeBasicBlock(CompiledKernel block){
    mBlocks.add(block);
    long ref = mGcObjectVisitor.writeToHeap(block, true);
    mHandlesMemory.writeLong(ref);
    m_HandlesList.add(ref);
    if(mUsingGarbageCollector){
      long to_handle_map_memory_address = ref*4;
      mToHandleMapMemory.setAddress(to_handle_map_memory_address);
      if(to_handle_map_memory_address > mMaxToHandleMapAddress)
        mMaxToHandleMapAddress = to_handle_map_memory_address;
      mToHandleMapMemory.writeLong(ref);
    }
    long prev_size = ref - m_PreviousRef;
    if(prev_size > m_PreviousSize){
      m_PreviousSize = prev_size;
    }
    m_PreviousRef = ref; 
  }
  
  private CompiledKernel getBlock(Iterator<Kernel> jobs){
    Kernel job = jobs.next();
    mWriteRet.enqueueJob(job);
    return (CompiledKernel) job;
  }

  public int writeRuntimeBasicBlocks(Iterator<Kernel> jobs){
    Stopwatch watch = new Stopwatch();
    watch.start();
    
    mBlocks = new ArrayList<CompiledKernel>();
    m_HandlesList.clear();
    
    mWriteRet = new PartiallyCompletedParallelJob(jobs);

    CompiledKernel first_block = getBlock(jobs);

    //mUsingGarbageCollector = first_block.isUsingGarbageCollector();
    mUsingGarbageCollector = false;
    mGcObjectVisitor = first_block.getSerializer(mToSpaceMemory, mTextureMemory);

    mHeapEndPtrMemory.setAddress(0);
    mHandlesMemory.setAddress(0);
    mToSpaceMemory.setAddress(0);
    mToSpaceMemory.clearHeapEndPtr();
    
    if(mUsingGarbageCollector){
      makeSureReadyForUsingGarbageCollector();
    }

    //write statics
    mGcObjectVisitor.writeStaticsToHeap();
    
    m_PreviousRef = 0;
    m_PreviousSize = 0;
    m_CountWritten = 1;
    mMaxToHandleMapAddress = -1;

    writeOneRuntimeBasicBlock(first_block);
    while(jobs.hasNext()){

      if(roomForMore(m_PreviousSize, m_PreviousRef) == false){
        break;
      }
      m_CountWritten++;

      CompiledKernel block = getBlock(jobs);
      writeOneRuntimeBasicBlock(block);
      
    }
    long heap_end_ptr = mToSpaceMemory.getHeapEndPtr();
    mHeapEndPtrMemory.writeLong(heap_end_ptr);
    
    mToSpaceMemory.finishCopy(mToSpaceMemory.getHeapEndPtr());    
    
    mHandlesMemory.finishCopy(m_CountWritten*8); //8 is sizeof long
    if(mUsingGarbageCollector){
      mToHandleMapMemory.finishCopy(mMaxToHandleMapAddress);
    }
    mHeapEndPtrMemory.finishCopy(8);    
        
    if(Configuration.getRunAllTests() == false){
      BufferPrinter printer = new BufferPrinter();
      printer.print(mToSpaceMemory, 0, 512);
    }
    return m_CountWritten;
  }

  protected abstract void allocateMemory();

  public PartiallyCompletedParallelJob readRuntimeBasicBlocks(){    
    if(Configuration.getRunAllTests() == false){
      BufferPrinter printer1 = new BufferPrinter();
      printer1.print(mToSpaceMemory, 0, 512);
    }
    
    Stopwatch watch = new Stopwatch();
    watch.start();
    
    mHandlesMemory.setAddress(0);

    //read statics
    mToSpaceMemory.setAddress(0);    
        
    CompiledKernel first_block = mBlocks.get(0);
    mGcObjectVisitor.readStaticsFromHeap();
    
    mExceptionsMemory.setAddress(0);
    for(int i = 0; i < m_CountWritten; ++i){
      int reference = mExceptionsMemory.readInt();
      if(reference == Constants.NullPointerNumber){
        throw new NullPointerException();
      }
      if(reference != 0){
        mToSpaceMemory.setAddress(reference);
        Object o = mGcObjectVisitor.readFromHeap(null, true, reference);
        throw new RuntimeException((Throwable) o);
      }
    }
    
    //read instances
    for(int i = 0; i < m_CountWritten; ++i){
      CompiledKernel block = mBlocks.get(i);
      long reference = m_HandlesList.get(i);
      mToSpaceMemory.setAddress(reference);
      mGcObjectVisitor.readFromHeap(block, true, reference);
    }
        
    mHandlesMemory.finishRead();
    mToSpaceMemory.finishRead();
    if(mUsingGarbageCollector){
      mToHandleMapMemory.finishRead();
    }
        
    return mWriteRet;
  }

  protected abstract void makeSureReadyForUsingGarbageCollector();

  private boolean roomForMore(long size, long ref) {
    long next_ref = ref + size + size;
    //System.out.printf("Next_ref: "+next_ref);
    if(next_ref >= mBufferSize)
      return false;
    if((m_CountWritten * 4) + 4 >= mBufferSize)
      return false;
    if(m_CountWritten + 1 > mDevice.getNumBlocks())
      return false;
    if(mUsingGarbageCollector){
      if(mMaxToHandleMapAddress + 4 >= mBufferSize)
        return false;
    }
    return true;
  }

  int getCountWritten() {
    return m_CountWritten;
  }

  public List<CompiledKernel> getBlocks() {
    return mBlocks;
  }
}