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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class NullPointer1RunOnGpu implements Kernel {

  private int index;
  private int[] a;
  private int[] b;
  private short[][] kx = {{-1,-2,-1},{0,0,0},{1,2,1}};
  private short[][] ky = {{-1,0,1}, {-2,0,2},{-1,0,1}};
  
  public NullPointer1RunOnGpu(int[] a, int index){
    this.a = a; 
    this.index = index;
  }
  
  @Override
  public void gpuMethod() {    
    int sx = convolve(index, kx);
    int sy = convolve(index, ky);

    if(sx*sx+sy*sy > 100*100){
      b[index] = 0; 
    } else {
      b[index] = 255;
    }
  }
  
  private int convolve(int index, short[][] kern) {
    int xx;
    int yy;
    int ret = 0;
    for(xx = -1; xx <= 1; xx++){
      for(yy = -1; yy <=1; yy++){
        int i = index+xx+yy;
        if(i >= a.length){
          return 0;
        }
        if(i < 0){
          return 0;
        } 
        int value = a[i];
        ret += value * kern[xx+1][yy+1];
      }
    }
    return ret;
  }
}
