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

import edu.syr.pcpratts.rootbeer.Configuration;
import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.runtime.Serializer;
import edu.syr.pcpratts.rootbeer.runtime.ParallelRuntime;
import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.ReadOnlyAnalyzer;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.CompiledKernel;
import edu.syr.pcpratts.rootbeer.runtime.memory.BufferPrinter;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import edu.syr.pcpratts.rootbeer.util.ResourceReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CudaRuntime2 implements ParallelRuntime {

  private static CudaRuntime2 m_Instance;
  
  public static CudaRuntime2 v(){
    if(m_Instance == null){
      m_Instance = new CudaRuntime2();
    }
    return m_Instance;
  }
  
  private int m_NumBlocks;
  
  private List<Memory> m_ToSpace;
  private List<Memory> m_Texture;
  private Handles m_Handles;
  private Handles m_ExceptionHandles;
  private long m_ToSpaceAddr;
  private long m_GpuToSpaceAddr;
  private long m_TextureAddr;
  private long m_GpuTextureAddr;
  private long m_HandlesAddr;
  private long m_GpuHandlesAddr;
  private long m_ExceptionsHandlesAddr;
  private long m_GpuExceptionsHandlesAddr;
  private long m_ToSpaceSize;
  private int m_NumCores;
  private int m_NumBlocksRun;
  private int m_BlockShape;
  private int m_GridShape;
  private long m_MaxGridDim;
  private long m_NumMultiProcessors;
  
  private List<Kernel> m_JobsToWrite;
  private List<Kernel> m_JobsWritten;
  private List<Kernel> m_NotWritten;
  private List<Long> m_HandlesCache;
  private CompiledKernel m_FirstJob;
  private PartiallyCompletedParallelJob m_Partial;
  
  private List<ToSpaceReader> m_Readers;
  private List<ToSpaceWriter> m_Writers;
  
  private CpuRunner m_CpuRunner;
  private BlockShaper m_BlockShaper;
  
  private CudaRuntime2(){
    Stopwatch watch = new Stopwatch();
    watch.start();    
    CudaLoader loader = new CudaLoader();
    loader.load();
    m_BlockShaper = new BlockShaper();
    setup(m_BlockShaper.getMaxBlocksPerProc(), m_BlockShaper.getMaxThreadsPerBlock());
    m_JobsToWrite = new ArrayList<Kernel>();
    m_JobsWritten = new ArrayList<Kernel>();  
    m_NotWritten = new ArrayList<Kernel>();
    m_HandlesCache = new ArrayList<Long>();
    m_ToSpace = new ArrayList<Memory>();
    m_Texture = new ArrayList<Memory>();
    m_Readers = new ArrayList<ToSpaceReader>();
    m_Writers = new ArrayList<ToSpaceWriter>();    
    m_NumCores = Runtime.getRuntime().availableProcessors();
    AtomicLong to_space_inst_ptr = new AtomicLong(0);
    AtomicLong to_space_static_ptr = new AtomicLong(0);
    AtomicLong texture_inst_ptr = new AtomicLong(0);
    AtomicLong texture_static_ptr = new AtomicLong(0);
    for(int i = 0; i < m_NumCores; ++i){
      m_ToSpace.add(new FastMemory(m_ToSpaceAddr, to_space_inst_ptr, to_space_static_ptr, m_ToSpaceSize));
      m_Texture.add(new FastMemory(m_TextureAddr, texture_inst_ptr, texture_static_ptr, m_ToSpaceSize));
      m_Readers.add(new ToSpaceReader());
      m_Writers.add(new ToSpaceWriter());
    }
    m_Handles = new Handles(m_HandlesAddr, m_GpuHandlesAddr);
    m_ExceptionHandles = new Handles(m_ExceptionsHandlesAddr, m_GpuExceptionsHandlesAddr);
    m_CpuRunner = new CpuRunner();
    watch.stopAndPrint("CudaRuntime2 ctor: ");
  }
  
  public void memoryTest(){
    MemoryTest test = new MemoryTest();
    test.run(m_ToSpace.get(0));
  }
  
  private native void setup(int max_blocks_per_proc, int max_threads_per_block);
  
  public PartiallyCompletedParallelJob run(Iterator<Kernel> jobs){
    Stopwatch watch2 = new Stopwatch();
    watch2.start();
    RootbeerGpu.setIsOnGpu(true);
    m_Partial = new PartiallyCompletedParallelJob(jobs);
    
    boolean any_jobs = writeBlocks(jobs);
    if(any_jobs == false){
      return m_Partial;
    }
    calculateShape();
    compileCode();
    
    Stopwatch watch = new Stopwatch();
    watch.start();
    runOnGpu();
    watch.stopAndPrint("GPU time: ");
        
    readBlocks();
    unload();
        
    runExtraBlocks();
    
    RootbeerGpu.setIsOnGpu(false);
    watch2.stopAndPrint("CudaRuntime2.run: ");
    return m_Partial;
  }

  public boolean writeBlocks(Iterator<Kernel> iter) {
        
    Stopwatch watch = new Stopwatch();
    watch.start();
    for(Memory mem : m_ToSpace){
      mem.setAddress(0);
    }
    m_Handles.activate();
    m_Handles.resetPointer();
    m_JobsToWrite.clear();
    m_JobsWritten.clear();
    m_HandlesCache.clear();
    m_NotWritten.clear();
    
    ReadOnlyAnalyzer analyzer = null;
    
    boolean first_block = true;    
    int count = 0;
    while(iter.hasNext()){
      Kernel job = iter.next();      
      if(first_block){
        m_FirstJob = (CompiledKernel) job;
        first_block = false;    
      }  
      
      m_JobsToWrite.add(job);
      if(count + 1 == m_BlockShaper.getMaxThreads(m_NumMultiProcessors))
        break;
      count++;
    }
    if(count == 0){
      return false;
    }
    
    List<Serializer> visitors = new ArrayList<Serializer>();
    for(int i = 0; i < m_NumCores; ++i){
      Memory mem = m_ToSpace.get(i);
      Memory texture_mem = m_Texture.get(i);
      mem.clearHeapEndPtr();
      texture_mem.clearHeapEndPtr();
      Serializer visitor = m_FirstJob.getSerializer(mem, texture_mem);
      visitor.setAnalyzer(analyzer);
      visitors.add(visitor);
    }
    
    //write the statics to the heap
    visitors.get(0).writeStaticsToHeap();
    
    int items_per = m_JobsToWrite.size() / m_NumCores;
    for(int i = 0; i < m_NumCores; ++i){
      Serializer visitor = visitors.get(i);
      int end_index;
      if(i == m_NumCores - 1){
        end_index = m_JobsToWrite.size();
      } else {
        end_index = (i+1)*items_per;
      }
      List<Kernel> items = m_JobsToWrite.subList(i*items_per, end_index);
      m_Writers.get(i).write(items, visitor);
    }
    
    for(int i = 0; i < m_NumCores; ++i){
      ToSpaceWriterResult result = m_Writers.get(i).join(); 
      List<Long> handles = result.getHandles();
      List<Kernel> items = result.getItems();      
      m_JobsWritten.addAll(items);
      m_Partial.enqueueJobs(items);
      m_HandlesCache.addAll(handles);
      m_NotWritten.addAll(result.getNotWrittenItems());
      for(Long handle : handles){
        m_Handles.writeLong(handle);
      }
    }
    
    m_Partial.addNotWritten(m_NotWritten);
    
    watch.stopAndPrint("write time: ");   
    
    if(Configuration.getRunAllTests() == false){
      BufferPrinter printer = new BufferPrinter();
      printer.print(m_ToSpace.get(0), 0, 720);
    }

    return true;
  }

  private void compileCode() {
    Stopwatch watch = new Stopwatch();
    watch.start();
    String filename = m_FirstJob.getCubin();
    List<byte[]> cubin = readCubin(filename);
    int total_size = 0;
    for(byte[] buffer : cubin){
      total_size += buffer.length;
    }
    loadFunction(getHeapEndPtr(), cubin, cubin.size(), total_size, m_NumBlocksRun);
    watch.stopAndPrint("compile code: ");
  }
  
  private List<byte[]> readCubin(String filename) {
    try {
      return ResourceReader.getResourceArray(filename);
    } catch(Exception ex){
      ex.printStackTrace();
      System.exit(-1);
      return null;
    }
  }
  
  private native void loadFunction(long heap_end_ptr, List<byte[]> cubin, int size, 
    int total_size, int num_blocks);
  private native int runBlocks(int size, int block_shape, int grid_shape);
  private native void unload();

  private void runOnGpu(){
    System.out.println("Running "+m_NumBlocksRun+" blocks.");
    System.out.println("BlockShape: "+m_BlockShape+" GridShape: "+m_GridShape);    
    
    int status = runBlocks(m_NumBlocksRun, m_BlockShape, m_GridShape); 
    if(status != 0){
      System.out.println("Error running blocks: "+status);
      System.exit(-1);
    }
  }  
    
  private void calculateShape() {    
    m_BlockShaper.run(m_JobsWritten.size(), m_NumMultiProcessors);
    m_GridShape = m_BlockShaper.gridShape();
    m_BlockShape = m_BlockShaper.blockShape();
    m_NumBlocksRun = m_GridShape * m_BlockShape;    
    if(m_NumBlocksRun > m_JobsWritten.size()){
      m_NumBlocksRun = m_JobsWritten.size(); 
    }
  }
  
  public void readBlocks() {    
    Stopwatch watch = new Stopwatch();
    watch.start();
    for(int i = 0; i < m_NumCores; ++i)
      m_ToSpace.get(i).setAddress(0);    
    
    m_ExceptionHandles.activate();
    
    if(Configuration.getRunAllTests() == false){
      BufferPrinter printer = new BufferPrinter();
      printer.print(m_ToSpace.get(0), 0, 720);
    }

    for(int i = 0; i < m_NumBlocksRun; ++i){
      long ref = m_ExceptionHandles.readLong();
      if(ref != 0){
        long ref_num = ref >> 4;
        if(ref_num == Constants.NullPointerNumber){
          throw new NullPointerException(); 
        } else if(ref_num == Constants.OutOfMemoryNumber){
          throw new OutOfMemoryError();
        }
        Memory mem = m_ToSpace.get(0);
        Memory texture_mem = m_Texture.get(0);
        Serializer visitor = m_FirstJob.getSerializer(mem, texture_mem);
        mem.setAddress(ref);           
        Object except = visitor.readFromHeap(null, true, ref);
        if(except instanceof Error){
          Error except_th = (Error) except;
          throw except_th;
        } else {
          throw new RuntimeException((Throwable) except);
        }
      }
    }    
    
    List<Serializer> visitors = new ArrayList<Serializer>();
    for(int i = 0; i < m_NumCores; ++i){      
      Memory mem = m_ToSpace.get(i);
      Memory texture_mem = m_Texture.get(i);
      Serializer visitor = m_FirstJob.getSerializer(mem, texture_mem);
      visitors.add(visitor);
    }
    
    //read the statics from the heap
    visitors.get(0).readStaticsFromHeap();
    
    int items_per = m_NumBlocksRun / m_NumCores;
    for(int i = 0; i < m_NumCores; ++i){
      Serializer visitor = visitors.get(i);
      int end_index;
      if(i == m_NumCores - 1){
        end_index = m_NumBlocksRun;
      } else {
        end_index = (i+1)*items_per;
      }
      List<Long> handles = m_HandlesCache.subList(i*items_per, end_index);
      List<Kernel> jobs = m_JobsWritten.subList(i*items_per, end_index);
      m_Readers.get(i).read(jobs, handles, visitor);
    }
    
    for(int i = 0; i < m_NumCores; ++i){
      m_Readers.get(i).join();  
    }
  }
  
  private void runExtraBlocks(){
    if(m_NumBlocksRun == m_JobsWritten.size())
      return;
    
    List<Kernel> cpu_jobs = m_JobsWritten.subList(m_NumBlocksRun, m_JobsWritten.size());
    m_CpuRunner.run(cpu_jobs);   
    m_CpuRunner.join();
    
    for(Kernel job : cpu_jobs){
      m_Partial.enqueueJob(job);
    }      
  }
  
  private long getHeapEndPtr() {
    long max = Long.MIN_VALUE;
    for(Memory mem : m_ToSpace){
      if(mem.getHeapEndPtr() > max)
        max = mem.getHeapEndPtr();
    }
    return max;
  }

  public boolean isGpuPresent() {
    return true;
  }
}
