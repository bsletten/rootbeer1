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

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class CpuRunnerCore implements Runnable {
  
  private BlockingQueue<List<Kernel>> m_InputQueue;
  private BlockingQueue<List<Kernel>> m_OutputQueue;
  private Thread m_Thread;
  
  public CpuRunnerCore(){
    m_InputQueue = new BlockingQueue<List<Kernel>>();
    m_OutputQueue = new BlockingQueue<List<Kernel>>();
    m_Thread = new Thread(this);
    m_Thread.setDaemon(true);
    m_Thread.start();
  }
  
  public void put(List<Kernel> jobs) {
    m_InputQueue.put(jobs);
  }

  public void take() {
    m_OutputQueue.take();
  }

  public void run() {
    while(true){
      List<Kernel> jobs = m_InputQueue.take();
      runJobs(jobs);
      m_OutputQueue.put(jobs);
    }
  }

  private void runJobs(List<Kernel> jobs) {
    for(Kernel job : jobs){
      job.gpuMethod();
    }
  }  
  
}
