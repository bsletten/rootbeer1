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

public class FloatArraysRunOnGpu implements Kernel {

  private float element;
  private float[] element1;
  private float[][] element2;
  private float[][][] element3;

  public FloatArraysRunOnGpu(){
    element = 10;
    element1 = new float[10];
    element2 = new float[10][10];
    element3 = new float[10][10][10];

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          element3[m][n][p] = (float) p;
          element2[n][p] = (float) p;
          element1[p] = (float) p;
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

  boolean compare(FloatArraysRunOnGpu brhs) {

    if(element != brhs.element){
      return false;
    }

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          if(element1[p] != brhs.element1[p])
            return false;
          if(element2[n][p] != brhs.element2[n][p])
            return false;
          if(element3[m][n][p] != brhs.element3[m][n][p])
            return false;
        }
      }
    }

    return true;
  }
}
