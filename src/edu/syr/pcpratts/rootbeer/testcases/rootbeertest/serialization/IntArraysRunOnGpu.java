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

public class IntArraysRunOnGpu implements Kernel {

  private int[][] array2;
  private int[][] array3;

  public IntArraysRunOnGpu(int value){
    array2 = new int[3][3];
  }

  public void initArray3(){
    array3 = new int[3][2];
    array3[0][0] = 0;
    array3[0][1] = 1;
    array3[1][0] = 2;
    array3[1][1] = 3;
    array3[2][0] = 4;
    array3[2][1] = 5;
  }

  public void gpuMethod() {

  }

  public boolean compare(IntArraysRunOnGpu rhs) {
    if(rhs.array3[0][0] != 0){
      System.out.println("0, 0 = "+rhs.array3[0][0]);
      return false;
    }
    if(rhs.array3[0][1] != 1){
      System.out.println("0, 1 = "+rhs.array3[0][1]);
      return false;
    }
    if(rhs.array3[1][0] != 2){
      System.out.println("1, 0 = "+rhs.array3[1][0]);
      return false;
    }
    if(rhs.array3[1][1] != 3){
      System.out.println("1, 1 = "+rhs.array3[1][1]);
      return false;
    }
    if(rhs.array3[2][0] != 4){
      System.out.println("2, 0 = "+rhs.array3[2][0]);
      return false;
    }
    if(rhs.array3[2][1] != 5){
      System.out.println("2, 1 = "+rhs.array3[2][1]);
      return false;
    }
    return true;
  }
}
