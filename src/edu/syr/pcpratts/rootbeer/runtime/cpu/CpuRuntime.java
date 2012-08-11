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

import edu.syr.pcpratts.rootbeer.runtime.ParallelRuntime;
import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CpuRuntime implements ParallelRuntime {

  private static CpuRuntime mInstance = null;
  private List<CpuCore> m_Cores;

  public static CpuRuntime v(){
    if(mInstance == null)
      mInstance = new CpuRuntime();
    return mInstance;
  }

  private CpuRuntime(){
    m_Cores = new ArrayList<CpuCore>();
    int num_cores = Runtime.getRuntime().availableProcessors();
    for(int i = 0; i < num_cores; ++i){
      m_Cores.add(new CpuCore());
    }
  }

  public PartiallyCompletedParallelJob run(Iterator<Kernel> jobs) throws Exception {
    PartiallyCompletedParallelJob ret = new PartiallyCompletedParallelJob(jobs);
    int enqueued = 0;
    for(int i = 0; i < m_Cores.size(); ++i){
      if(jobs.hasNext()){
        Kernel job = jobs.next();
        m_Cores.get(i).enqueue(job);
        enqueued++;
      }
    }

    for(int i = 0; i < enqueued; ++i){
      Kernel curr = m_Cores.get(i).getResult();
      ret.enqueueJob(curr);
    }
    return ret;
  }

  public boolean isGpuPresent() {
    return true;
  }


}
