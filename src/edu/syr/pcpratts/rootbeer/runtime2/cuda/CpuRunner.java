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
import java.util.ArrayList;
import java.util.List;

public class CpuRunner {

  private int m_NumCores;
  private List<CpuRunnerCore> m_Cores;
  
  public CpuRunner(){   
    m_NumCores = Runtime.getRuntime().availableProcessors(); 
    m_Cores = new ArrayList<CpuRunnerCore>();
    for(int i = 0; i < m_NumCores; ++i){
      m_Cores.add(new CpuRunnerCore());
    }
  }

  public void run(List<Kernel> cpu_jobs) {
    int items_per = cpu_jobs.size() / m_NumCores;
    for(int i = 0; i < m_NumCores; ++i){
      int end_index;
      if(i == m_NumCores - 1){
        end_index = cpu_jobs.size();
      } else {
        end_index = (i+1)*items_per;
      }
      List<Kernel> jobs = cpu_jobs.subList(i*items_per, end_index);
      m_Cores.get(i).put(jobs);
    }
  }

  public void join() {
    for(int i = 0; i < m_NumCores; ++i){
      m_Cores.get(i).take();
    }
  }
}
