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

public class SynchronizedMethodRunOnGpu implements Kernel {

  private SynchronizedMethodObject m_Object;
  
  public SynchronizedMethodRunOnGpu(SynchronizedMethodObject obj){
    m_Object = obj;
  }

  @Override
  public void gpuMethod() {
    m_Object.increment(true);
  }
  
  boolean compare(SynchronizedMethodRunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(m_Object.value != grhs.m_Object.value){
      System.out.println("value");
      System.out.println("lhs: "+m_Object.value);
      System.out.println("rhs: "+grhs.m_Object.value);
      return false;
    }
    return true;
  }
}