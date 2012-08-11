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

import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class MultiArrayRunOnGpu implements Kernel {

  private int[][] m_Ret;
  private int[][][] m_Ret2;
    
  public MultiArrayRunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    int size = 30;
    m_Ret = new int[size][size];
    m_Ret2 = new int[size][size][size];
    for(int i = 0; i < size; ++i){
      for(int j = 0; j < size; ++j){ 
        m_Ret[i][j] = i+j;
        for(int k = 0; k < size; ++k){
          m_Ret2[i][j][k] = i+j+k;
        }
      }
    }
  }
  
  boolean compare(MultiArrayRunOnGpu brhs) {
    if(m_Ret.length != brhs.m_Ret.length){
      System.out.println("outer length failed");
      return false;
    }
    for(int i = 0; i < m_Ret.length; ++i){
      if(m_Ret[i].length != brhs.m_Ret[i].length){
        System.out.println("inner length failed");
        return false;
      }
      for(int j = 0; j < m_Ret[i].length; ++j){
        int lhs = m_Ret[i][j];
        int rhs = brhs.m_Ret[i][j];
        if(lhs != rhs){
          System.out.println("value failed");
          return false;
        }
      }
    }
    if(m_Ret2.length != brhs.m_Ret2.length){
      System.out.println("outer length failed ret2");
      return false;
    }
    for(int i = 0; i < m_Ret2.length; ++i){
      if(m_Ret2[i].length != brhs.m_Ret2[i].length){        
        System.out.println("inner1 length failed ret2");
        return false;
      }
      for(int j = 0; j < m_Ret2[i].length; ++j){
        if(m_Ret2[i][j].length != brhs.m_Ret2[i][j].length){
          System.out.println("inner2 length failed ret2");
          return false;
        }
        for(int k = 0; k < m_Ret2[i][j].length; ++k){        
          int lhs = m_Ret2[i][j][k];
          int rhs = brhs.m_Ret2[i][j][k];
          if(lhs != rhs){
            System.out.println("value failed");
            return false;
          }
        }
      }
    }
    return true;
  }

}
