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

public class MMultRunOnGpu implements Kernel {

  private int[] a;
  private int[] b;
  private int[] c;
  private int index;
  private int size;
  
  public MMultRunOnGpu(int[] a, int[] b, int[] c, int index, int size){
    this.a = a;
    this.b = b;
    this.c = c;
    this.index = index;
    this.size = size;
  }

  @Override
  public void gpuMethod() {
    int len = a.length;
    int lsize = size;
    int lindex = index;
    int[] la = a;
    int[] lb = b;
    int[] lc = c;
    for(int j = 0; j < lsize; ++j){
      int sum = 0;
      for(int k = 0; k < lsize; ++k){
        sum += (la[lindex*lsize+j]*lb[j*lsize+k]);
      }
      lc[lindex*lsize+j] = sum + len;
    }
  }

  boolean compare(MMultRunOnGpu brhs) {
    if(c.length != brhs.c.length){
      System.out.println("len failed");
      System.out.println("c.length: "+c.length);
      System.out.println("brhs.c.length: "+brhs.c.length);
      return false;
    }
    for(int i = index*size; i < (index+1)*size; ++i){
      int lhs = c[i];
      int rhs = brhs.c[i];
      if(lhs != rhs){
        System.out.println("c value failed");
        System.out.println("i: "+i);
        System.out.println("lhs: "+lhs+" rhs: "+rhs);
        return false;
      }
    }
    return true;
  }
}
