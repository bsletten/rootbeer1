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

package edu.syr.pcpratts.rootbeer.runtime.nativecpu;

import edu.syr.pcpratts.rootbeer.runtime.ParallelRuntime;
import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.Iterator;

public class NativeCpuRuntime implements ParallelRuntime {

  private static NativeCpuRuntime m_Instance = null;
  
  public static NativeCpuRuntime v(){
    if(m_Instance == null)
      m_Instance = new NativeCpuRuntime();
    return m_Instance;
  }
  
  NativeCpuDevice m_Device;
  
  private NativeCpuRuntime(){
    m_Device = new NativeCpuDevice();
  }
  
  public PartiallyCompletedParallelJob run(Iterator<Kernel> blocks) {
    return m_Device.run(blocks);
  }

  public boolean isGpuPresent() {
    return true;
  }
  
}
