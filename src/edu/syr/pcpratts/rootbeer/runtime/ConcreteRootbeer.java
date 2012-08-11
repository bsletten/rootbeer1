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

import edu.syr.pcpratts.rootbeer.Configuration;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.CudaTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.NativeCpuTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import edu.syr.pcpratts.rootbeer.runtime.cpu.CpuRuntime;
import edu.syr.pcpratts.rootbeer.runtime.nativecpu.NativeCpuRuntime;
import edu.syr.pcpratts.rootbeer.runtime2.cuda.CudaRuntime2;
import java.util.Iterator;
import java.util.List;

public class ConcreteRootbeer implements IRootbeer {

  boolean m_GpuWorking;

  public ConcreteRootbeer(){
    m_GpuWorking = true;
    if(Configuration.runtimeInstance().getMode() == Configuration.MODE_GPU) {
      CudaRuntime2.v();
    }
  }

  public void runAll(List<Kernel> list){
    Iterator<Kernel> iter = run(list.iterator());
    while(iter.hasNext()){
      iter.next();
    }
  }
  
  public Iterator<Kernel> run(Iterator<Kernel> iter) {
    if(Configuration.runtimeInstance().getMode() == Configuration.MODE_NEMU){
      return runOnNativeCpu(iter);
    } else if(Configuration.runtimeInstance().getMode() == Configuration.MODE_JEMU){
      return runOnCpu(iter);
    } else {
      return runOnCudaGpu(iter);
    }
  }

  private Iterator<Kernel> runOnCpu(Iterator<Kernel> jobs) {
    try {
      PartiallyCompletedParallelJob partial = CpuRuntime.v().run(jobs);
      return new ResultIterator(partial, CpuRuntime.v());
    } catch (Exception ex){
      ex.printStackTrace();
      System.exit(-1);
      return null;
    }
  }
  
  private Iterator<Kernel> runOnCudaGpu(Iterator<Kernel> jobs) {    
    Tweaks.setInstance(new CudaTweaks());
    PartiallyCompletedParallelJob partial = CudaRuntime2.v().run(jobs);
    return new ResultIterator(partial, CudaRuntime2.v());
  }
  
  private Iterator<Kernel> runOnNativeCpu(Iterator<Kernel> jobs) {
    Tweaks.setInstance(new NativeCpuTweaks());
    PartiallyCompletedParallelJob partial = NativeCpuRuntime.v().run(jobs);
    return new ResultIterator(partial, NativeCpuRuntime.v());
  }
}
