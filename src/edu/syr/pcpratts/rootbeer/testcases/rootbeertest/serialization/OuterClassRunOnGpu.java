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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class OuterClassRunOnGpu implements Kernel {

  private int m_Int;

  public OuterClassRunOnGpu(){
    m_Int = 0;
  }

  @Override
  public void gpuMethod() {
    m_Int++;
  }

  boolean compare(OuterClassRunOnGpu brhs) {
    if(m_Int != brhs.m_Int){
      System.out.println("m_Int");
      System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
      return false;
    }
    return true;
  }
  
  public class OuterClassRunOnGpu2 implements Kernel {
    private int m_Int;
    
    public OuterClassRunOnGpu2(){
      m_Int = 1;
    }
    
    @Override
    public void gpuMethod() {
      m_Int++;
    }
    
    public boolean compare(OuterClassRunOnGpu.OuterClassRunOnGpu2 brhs) {
      if(m_Int != brhs.m_Int){
        System.out.println("m_Int");
        System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
        return false;
      }
      return true;
    }
  }
}
