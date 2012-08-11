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

import edu.syr.pcpratts.rootbeer.runtime.Serializer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import java.util.List;

public class ToSpaceReader {

  private BlockingQueue<InputItem> m_InputQueue;
  private BlockingQueue<InputItem> m_OutputQueue;
  private Thread m_Thread;
  
  public ToSpaceReader(){
    m_InputQueue = new BlockingQueue<InputItem>();
    m_OutputQueue = new BlockingQueue<InputItem>();
    ReadThreadProc proc = new ReadThreadProc(m_InputQueue, m_OutputQueue);
    m_Thread = new Thread(proc);
    m_Thread.setDaemon(true);
    m_Thread.start();
  }
  
  public void read(List<Kernel> items, List<Long> handles, Serializer visitor){
    InputItem item = new InputItem();
    item.m_Items = items;
    item.m_HandlesCache = handles;
    item.m_Visitor = visitor;
    m_InputQueue.put(item);
  }
  
  public void join(){
    m_OutputQueue.take();
  }
  
  private class InputItem {
    public List<Kernel> m_Items;
    public List<Long> m_HandlesCache;
    public Serializer m_Visitor;
  }
    
  private class ReadThreadProc implements Runnable {
       
    private BlockingQueue<InputItem> m_InputQueue;
    private BlockingQueue<InputItem> m_OutputQueue;
  
    public ReadThreadProc(BlockingQueue<InputItem> input_queue,
      BlockingQueue<InputItem> output_queue){
      
      m_InputQueue = input_queue;
      m_OutputQueue = output_queue;
    }
        
    public void run(){
      while(true){
        InputItem input_item = m_InputQueue.take();
        for(int i = 0; i < input_item.m_Items.size(); ++i){
          Kernel item = input_item.m_Items.get(i);
          long handle = input_item.m_HandlesCache.get(i);
          input_item.m_Visitor.readFromHeap(item, true, handle);
        }
        m_OutputQueue.put(input_item);
      }
    }
  } 
}
