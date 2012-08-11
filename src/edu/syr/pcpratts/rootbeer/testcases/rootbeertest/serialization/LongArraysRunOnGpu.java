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

public class LongArraysRunOnGpu implements Kernel {

  private long element;
  private long[] element1;
  private long[][] element2;
  private long[][][] element3;

  public LongArraysRunOnGpu(){
    int size = 11;
    element = 10;
    element1 = new long[size];
    element2 = new long[size][size];
    element3 = new long[size][size][size];

    for(int m = 0; m < size; m++){
      for(int n = 0; n < size; n++){
        for(int p = 0; p < size; ++p){
          element3[m][n][p] = (long) p;
          element2[n][p] = (long) p;
          element1[p] = (long) p;
        }
      }
    }
  }

  public void gpuMethod() {
    element++;
    element1[0]++;
    element2[0][0]++;
    element3[0][0][0]++;
  }

  boolean compare(LongArraysRunOnGpu brhs) {

    if(element != brhs.element){
      System.out.println("element: "+element);
      System.out.println("rhs.element: "+brhs.element);
      return false;
    }

    int size = 11;
    for(int m = 0; m < size; m++){
      for(int n = 0; n < size; n++){
        for(int p = 0; p < size; ++p){
          if(element1[p] != brhs.element1[p]){
            System.out.println("p: "+p);
            System.out.println("element: "+element1[p]);
            System.out.println("rhs.element: "+brhs.element1[p]);
            return false;
          }
          if(element2[n][p] != brhs.element2[n][p]){
            System.out.println("p: "+p);
            System.out.println("n: "+n);
            System.out.println("element: "+element2[n][p]);
            System.out.println("rhs.element: "+brhs.element2[n][p]);
            return false;
          }
          if(element3[m][n][p] != brhs.element3[m][n][p]){
            System.out.println("p: "+p);
            System.out.println("n: "+n);
            System.out.println("m: "+m);
            System.out.println("element: "+element3[m][n][p]);
            System.out.println("rhs.element: "+brhs.element3[m][n][p]);
            return false;
          }
        }
      }
    }

    return true;
  }
}
