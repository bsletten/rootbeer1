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
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongRunOnGpu implements Kernel {
  private AtomicLong m_ALong;
  private float m_Random;
  private double m_Random2;
  private Random random;
  private long m_Num1;
  private long m_Num2;
  
  public AtomicLongRunOnGpu(AtomicLong along, Random random){
    m_ALong = along;
    m_Random = -100;
    m_Random2 = -100;
    this.random = random;
  }

  @Override
  public void gpuMethod() {
    m_Num1 = m_ALong.addAndGet(10);
    m_Random = (float) random.nextDouble();
    m_Random2 = Math.random();
    AtomicLong newlong = new AtomicLong(30);
    m_Num1 = newlong.get();
    m_Num2 = newlong.get();
  }
  
  boolean compare(AtomicLongRunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(m_Num1 != grhs.m_Num1){
      System.out.println("num1");
      System.out.println("lhs: "+m_Num1);
      System.out.println("rhs: "+grhs.m_Num1);
      return false;
    } 
    if(m_ALong.get() != grhs.m_ALong.get()){
      System.out.println("value");
      System.out.println("lhs: "+m_ALong.get());
      System.out.println("rhs: "+grhs.m_ALong.get());
      return false;
    }
    if(m_Num2 != grhs.m_Num2){
      System.out.println("num2");
      System.out.println("lhs: "+m_Num2);
      System.out.println("rhs: "+grhs.m_Num2);
      return false;
    }
    if(grhs.m_Random == -100){
      System.out.println("random");
      return false;
    }
    if(grhs.m_Random2 == -100){
      System.out.println("random2");
      return false;
    }
    return true;
  }
}