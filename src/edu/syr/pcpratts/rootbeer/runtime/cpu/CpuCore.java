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

package edu.syr.pcpratts.rootbeer.runtime.cpu;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

class CpuCore implements Runnable{

  private LinkedBlockingQueue<Kernel> m_InQueue;
  private LinkedBlockingQueue<Kernel> m_OutQueue;
  public CpuCore(){
    m_InQueue = new LinkedBlockingQueue<Kernel>();
    m_OutQueue = new LinkedBlockingQueue<Kernel>();
    Thread t = new Thread(this);
    t.setDaemon(true);
    t.start();
  }

  public void run() {
    while(true){
      try {
        Kernel job = m_InQueue.take();
        job.gpuMethod();
        m_OutQueue.put(job);
      } catch(Exception ex){
        //ignore
      }
    }
  }

  void enqueue(Kernel job) {
    while(true){
      try {
        m_InQueue.put(job);
        return;
      } catch(Exception ex){
        //ignore
      }
    }
  }

  Kernel getResult() {
    while(true){
      try {
        return m_OutQueue.take();
      } catch(Exception ex){
        //ignore
      }
    }
  }
}
