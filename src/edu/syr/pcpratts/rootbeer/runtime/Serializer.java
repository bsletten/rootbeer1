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

package edu.syr.pcpratts.rootbeer.runtime;

import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.collections.map.IdentityMap;

public abstract class Serializer {

  public Memory mMem;
  public Memory mTextureMem;

  private static final Map<Object, Long> mWriteToGpuCache;
  private static final Map<Long, Object> mReadFromGpuCache;
  
  private ReadOnlyAnalyzer m_Analyzer;
  
  static {
    mWriteToGpuCache = new IdentityMap();
    mReadFromGpuCache = new HashMap<Long, Object>();
  }
  
  public Serializer(Memory mem, Memory texture_mem){
    mMem = mem;
    mTextureMem = texture_mem;
    mReadFromGpuCache.clear();
    mWriteToGpuCache.clear();
  }
  
  public void setAnalyzer(ReadOnlyAnalyzer analyzer){
    m_Analyzer = analyzer;
  }

  public void writeStaticsToHeap(){
    doWriteStaticsToHeap();
  }

  public long writeToHeap(Object o){
    return writeToHeap(o, true);
  }
  
  private class WriteCacheResult {
    public long m_Ref;
    public boolean m_NeedToWrite;
    public WriteCacheResult(long ref, boolean need_to_write){
      m_Ref = ref;
      m_NeedToWrite = need_to_write;
    }
  }
  
  private WriteCacheResult checkWriteCache(Object o, int size, boolean read_only){
    synchronized(mWriteToGpuCache){
      if(mWriteToGpuCache.containsKey(o)){
        long ref = mWriteToGpuCache.get(o);
        return new WriteCacheResult(ref, false);
      }
      Memory mem;
      if(read_only){
        mem = mTextureMem;
      } else {
        mem = mMem;
      }
      long ref = mem.mallocWithSize(size);
      mWriteToGpuCache.put(o, ref);
      return new WriteCacheResult(ref, true);
    }
  }
  
  public long writeToHeap(Object o, boolean write_data){
    if(o == null)
      return -1;    
    int size = doGetSize(o);
    boolean read_only = false;
    WriteCacheResult result;
    result = checkWriteCache(o, size, read_only);
    if(result.m_NeedToWrite == false)
      return result.m_Ref;
    doWriteToHeap(o, write_data, result.m_Ref, read_only);
    return result.m_Ref;
  }
  
  protected Object checkCache(long address, Object item){
    synchronized(mReadFromGpuCache){
      if(mReadFromGpuCache.containsKey(address)){
        return mReadFromGpuCache.get(address);
      } else {
        mReadFromGpuCache.put(address, item);
        return item;
      }
    }
  }

  public Object readFromHeap(Object o, boolean read_data, long address){
    synchronized(mReadFromGpuCache){
      if(mReadFromGpuCache.containsKey(address)){
        Object ret = mReadFromGpuCache.get(address);  
        return ret;
      }
    }
    long null_ptr_check = address >> 4;
    if(null_ptr_check == -1){
      return null;
    }
    Object ret = doReadFromHeap(o, read_data, address);
    return checkCache(address, ret);
  }

  public void readStaticsFromHeap(){
    doReadStaticsFromHeap();
  }
 
  public Object readField(Object base, String name){
    Class cls = base.getClass();
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(base);      
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }

  public Object readStaticField(Class cls, String name){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        Object ret = f.get(null);     
        return ret;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }

  public void writeField(Object base, String name, Object value){
    Class cls = base.getClass();
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.set(base, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public void writeStaticField(Class cls, String name, Object value){
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        f.set(null, value);  
        return;
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    }
  }
  
  public abstract void doWriteToHeap(Object o, boolean write_data, long ref, boolean read_only);
  public abstract void doWriteStaticsToHeap();
  public abstract Object doReadFromHeap(Object o, boolean read_data, long ref);
  public abstract void doReadStaticsFromHeap();
  public abstract int doGetSize(Object o);
}
