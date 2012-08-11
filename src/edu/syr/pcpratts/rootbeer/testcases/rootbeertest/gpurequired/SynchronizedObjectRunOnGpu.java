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

public class SynchronizedObjectRunOnGpu implements Kernel {

  private final SynchronizedObjectShared m_Object;
  private final SynchronizedObjectShared m_Object2;
  private int int1;
  private int int2;
  private int int3;
  private int int4;
  private int int5;
  private int int6;
  private int int7;
  private int int8;
  private int int9;
  
  public SynchronizedObjectRunOnGpu(SynchronizedObjectShared obj){
    m_Object = obj;
    m_Object2 = new SynchronizedObjectShared();
  }

  @Override
  public void gpuMethod() {
    int1++;
    synchronized(m_Object){
      int2++;
      synchronized(m_Object2){
        int3++;
        m_Object.increment(true);
        int4++;
        synchronized(m_Object2){          
          int3++;
          m_Object.increment(true);
          int4++;
        }
        synchronized(m_Object2){          
          int3++;
          m_Object.increment(true);
          int4++;
        }
      }
      synchronized(m_Object2){
        int3++;
        m_Object.increment(true);
        int4++;
      }
      int5++;
      synchronized(m_Object2){
        int6++;
        m_Object.increment(true);
        int7++;
      }
      int8++;
    }
    int9++;
    synchronized(m_Object){
      int2++;
      synchronized(m_Object2){
        int3++;
        m_Object.increment(true);
        int4++;
      }
      int5++;
      synchronized(m_Object2){
        int6++;
        m_Object.increment(true);
        int7++;
      }
      int8++;
    }
  }
  
  boolean compare(SynchronizedObjectRunOnGpu grhs) {
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
    if(int1 != grhs.int1){
      System.out.println("int1");
      return false; 
    }
    if(int2 != grhs.int2){
      System.out.println("int2");
      return false; 
    }
    if(int3 != grhs.int3){
      System.out.println("int3");
      return false; 
    }
    if(int4 != grhs.int4){
      System.out.println("int4");
      return false; 
    }
    if(int5 != grhs.int5){
      System.out.println("int5");
      return false; 
    }
    if(int6 != grhs.int6){
      System.out.println("int6");
      return false; 
    }
    if(int7 != grhs.int7){
      System.out.println("int7");
      return false; 
    }
    if(int8 != grhs.int8){
      System.out.println("int8");
      return false; 
    }
    if(int9 != grhs.int9){
      System.out.println("int9");
      return false; 
    }
    return true;
  }
}