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

package edu.syr.pcpratts.rootbeer;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Aug4th2011PerformanceStudy {

  private static Aug4th2011PerformanceStudy m_Instance;
  public static Aug4th2011PerformanceStudy v(){
    if(m_Instance == null){
      m_Instance = new Aug4th2011PerformanceStudy();
    }
    return m_Instance;    
  }
  
  private int m_CurrentIndex;
  private List<Integer> m_BufferSizes;
  private List<Integer> m_Count;
  private List<Integer> m_RunOnGpu;
  
  private List<Integer> m_WriteTime;
  private List<Integer> m_ReadTime;
  private List<Integer> m_GpuRunTime;
  
  private List<Integer> m_BufferList;
  private List<Integer> m_NumBlocks;
  
  public Aug4th2011PerformanceStudy(){
    m_CurrentIndex = 0;
    m_BufferSizes = new ArrayList<Integer>();
    m_Count = new ArrayList<Integer>();
    m_RunOnGpu = new ArrayList<Integer>();
    m_ReadTime = new ArrayList<Integer>();
    m_WriteTime = new ArrayList<Integer>();
    m_GpuRunTime = new ArrayList<Integer>();
    m_NumBlocks = new ArrayList<Integer>();
    
    addBlockSize(2048);
    addBlockSize(2304);
    addBlockSize(2432);
    addBlockSize(2688);
    addBlockSize(2816);
    addBlockSize(3072);
    addBlockSize(3200+(128*3));
    addBlockSize(3200+(128*4));
    addBlockSize(3200+(128*5));
    addBlockSize(3200+(128*6));
    addBlockSize(3200+(128*7));
    addBlockSize(4096);
    addBlockSize(4224);
    addBlockSize(4480);
    addBlockSize(4928);
    addBlockSize(5376);
    addBlockSize(5824);
    addBlockSize(6272);
    addBlockSize(4224);
    addBlockSize(4608);
    addBlockSize(4736);
    addBlockSize(4992);
    addBlockSize(5120);
    addBlockSize(5248);
    addBlockSize(5376);
    addBlockSize(5504);
    addBlockSize(5632);
    addBlockSize(5760);
    addBlockSize(5888);
    addBlockSize(6016);
    addBlockSize(6144);
    addBlockSize(6272);
    addBlockSize(6400);
    addBlockSize(6528);
    addBlockSize(6656);
    
    List<Integer> buffer_list = new ArrayList<Integer>();
    buffer_list.add(512);
    buffer_list.add(1*1024);
    buffer_list.add(2*1024);
    buffer_list.add(4*1024);
    buffer_list.add(8*1024);
    buffer_list.add(16*1024);
    buffer_list.add(32*1024);
    buffer_list.add(64*1024);
    buffer_list.add(128*1024);
    buffer_list.add(256*1024);
    buffer_list.add(512*1024);
    buffer_list.add(1024*1024);
    buffer_list.add(2*1024*1024);
    buffer_list.add(4*1024*1024);
    buffer_list.add(8*1024*1024);
    buffer_list.add(16*1024*1024);
    buffer_list.add(32*1024*1024);
    buffer_list.add(64*1024*1024);
    buffer_list.add(128*1024*1024);
    buffer_list.add(256*1024*1024);
    buffer_list.add(512*1024*1024);
    buffer_list.add(1024*1024*1024);
    m_BufferList = buffer_list;
  }
  
  public void addWriteTime(int time){
    m_WriteTime.add(time);
  }
  
  public void addReadTime(int time){
    m_ReadTime.add(time);
  }
  
  public void addGpuRunTime(int time){
    m_GpuRunTime.add(time);
  }
  
  public int getBufferSize(){
    return m_BufferList.get(m_CurrentIndex);
  }
  
  public int getCount(){
    return m_Count.get(m_CurrentIndex);
  }
  
  public void next(){
    System.out.println("Just covered: "+m_NumBlocks.get(m_CurrentIndex));
    m_CurrentIndex++;
    //nextBufferSize();
    nextRunOnGpuSize();
  }
  
  public void nextBufferSize(){
    if(m_CurrentIndex == m_BufferList.size()){
      printResultsBufferSize();
      System.exit(0);
    } 
  }

  private void printResultsBufferSize() {
    System.out.println("BufferSize,ReadTime,WriteTime");
    for(int i = 0; i < m_BufferList.size(); ++i){
      System.out.println(m_BufferList.get(i)+","+m_ReadTime.get(i)+","+m_WriteTime.get(i));
    }
  }

  private void nextRunOnGpuSize() {
    if(m_CurrentIndex == m_NumBlocks.size()){
      printResultsNumBlocks();
      System.exit(0);
    }
  }
  
  private void printResultsNumBlocks(){
    System.out.println("NumBlocks,Count,GpuTime,WriteTime");
    for(int i = 0; i < m_NumBlocks.size(); ++i){
      System.out.println(m_NumBlocks.get(i)+","+m_Count.get(i)+","+m_GpuRunTime.get(i)+","+m_WriteTime.get(i));
    }
  }

  public long getNumBlocks() {
    //System.out.println("Blocks: "+m_NumBlocks.get(m_CurrentIndex)+" Count: "+m_Count.get(m_CurrentIndex));
    return m_NumBlocks.get(m_CurrentIndex);
  }

  private void addBlockSize(int size) {
    m_NumBlocks.add(size);
    int total = 1000000;
    int count = total / size;
    if(total % size != 0)
      count++;
    m_Count.add(count);
  }
}
