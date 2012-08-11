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

public class OuterClass3RunOnGpu implements Kernel {

  private int m_Int;

  public OuterClass3RunOnGpu(){
    m_Int = 0;
  }

  @Override
  public void gpuMethod() {
    InnerClass inner = new InnerClass();
    m_Int += inner.getValue();
  }

  boolean compare(OuterClass3RunOnGpu brhs) {
    if(m_Int != brhs.m_Int){
      System.out.println("m_Int");
      System.out.println("lhs: "+m_Int+" rhs: "+brhs.m_Int);
      return false;
    }
    return true;
  }
  
  private class InnerClass {
    
    private int m_Int;
    
    public InnerClass(){
      m_Int = 5;
    }
    
    public int getValue(){
      return m_Int + 10;
    }
  }
}
