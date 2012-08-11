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

public class Handles {
  
  private long m_CpuAddr;
  private long m_GpuAddr;
  
  public Handles(long cpu_base_address, long gpu_base_address){
    m_CpuAddr = cpu_base_address;
    m_GpuAddr = gpu_base_address;
    setup(cpu_base_address, gpu_base_address);
  }

  public void activate(){
    setup(m_CpuAddr, m_GpuAddr);
  }
  
  private native void setup(long cpu_base_address, long gpu_base_address);
  
  public native void resetPointer();
  public native void writeLong(long value);
  public native long readLong();
}
