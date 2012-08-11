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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class LargeMemTestRunOnGpu implements Kernel {

  private int[] m_Mem;
  private long m_Sum;

  public LargeMemTestRunOnGpu(){
    m_Mem = new int[2*1024*1024];
  }

  @Override
  public void gpuMethod() {
    m_Sum = 0;
    for(int i = 0; i < m_Mem.length; ++i){
      m_Sum += m_Mem[i];
    }
  }

  boolean compare(LargeMemTestRunOnGpu brhs) {
    if(m_Sum != brhs.m_Sum){
      System.out.println("m_Sum");
      System.out.println("lhs: "+m_Sum+" rhs: "+brhs.m_Sum);
      return false;
    }
    return true;
  }
}
