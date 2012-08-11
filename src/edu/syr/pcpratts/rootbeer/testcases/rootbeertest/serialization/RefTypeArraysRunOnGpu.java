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

public class RefTypeArraysRunOnGpu implements Kernel {

  private RefType1 element;
  private RefType1[] element1;
  private RefType1[][] element2;
  private RefType1[][][] element3;

  public RefTypeArraysRunOnGpu(){
    element = new RefType1((byte) 10);
    element1 = new RefType1[10];
    element2 = new RefType1[10][10];
    element3 = new RefType1[10][10][10];

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(byte p = 0; p < 10; ++p){
          element3[m][n][p] = new RefType1(p);
          element2[n][p] = new RefType1(p);
          element1[p] = new RefType1(p);
        }
      }
    }
  }

  @Override
  public void gpuMethod() {
    RefType1 el = element1[0];
    el.modify();
    element1[0] = el;
    
    element3[0][0][0].modify();
    element2[0][0].modify();
    element.modify();
  }

  boolean compare(RefTypeArraysRunOnGpu brhs) {

    if(element.equals(brhs.element) == false){
      System.out.println("1");
      return false;
    }

    for(int m = 0; m < 10; m++){
      for(int n = 0; n < 10; n++){
        for(int p = 0; p < 10; ++p){
          if(element1[p].equals(brhs.element1[p]) == false){
            System.out.println("2: m: "+m+" n: "+n+" p: "+p);
            return false;
          }
          if(element2[n][p].equals(brhs.element2[n][p]) == false){
            return false;
          }
          if(element3[m][n][p].equals(brhs.element3[m][n][p]) == false){
            return false;
          }
        }
      }
    }
    return true;
  }
}
