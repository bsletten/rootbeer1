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

public class SynchronizedMethod2RunOnGpu implements Kernel {

  private SynchronizedMethod2Shared m_Shared;
  private int value1;
  
  public SynchronizedMethod2RunOnGpu(SynchronizedMethod2Shared obj){
    m_Shared = obj;
  }

  @Override
  public void gpuMethod() {
    try {
      increment();
    } catch(Throwable ex){
      value1++;
    }    
  }
  
  private void increment(){
    m_Shared.increment();
    throw new RuntimeException();
  }
  
  boolean compare(SynchronizedMethod2RunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(m_Shared.m_Value != grhs.m_Shared.m_Value){
      System.out.println("m_Value");
      System.out.println("lhs: "+m_Shared.m_Value);
      System.out.println("rhs: "+grhs.m_Shared.m_Value);
      return false;
    }
    if(value1 != grhs.value1){
      System.out.println("value1");
      return false; 
    }
    return true;
  }
}