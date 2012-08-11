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
import java.util.List;

public class SynchronizedMethod3RunOnGpu implements Kernel {

  private SynchronizedMethod3Object m_Object;
  private int m_Result2;
  
  public SynchronizedMethod3RunOnGpu(SynchronizedMethod3Object obj){
    m_Object = obj;
  }
  
  @Override
  public void gpuMethod() {
    try {
      m_Object.increment();
    } catch(NullPointerException ex){
      m_Result2 = 10;
    }
    try {
      m_Object.increment2();
    } catch(NullPointerException ex){
      m_Result2 += 10;
    }
  }

  boolean compare(SynchronizedMethod3RunOnGpu rhs) {
    if(m_Object.value != rhs.m_Object.value){
      System.out.println("result");
      System.out.println("lhs: "+m_Object.value);
      System.out.println("rhs: "+rhs.m_Object.value);
      return false;
    }
    if(m_Result2 != rhs.m_Result2){
      System.out.println("result2");
      System.out.println("lhs: "+m_Result2);
      System.out.println("rhs: "+rhs.m_Result2);
      return false;
    }
    return true;
  }
}
