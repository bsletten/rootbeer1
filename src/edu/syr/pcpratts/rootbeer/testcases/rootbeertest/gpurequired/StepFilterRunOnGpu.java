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

public class StepFilterRunOnGpu implements Kernel {

  public short[] a;
  public short[] b;
  private int index;
  private int num;
    
  private short[][] kx;
  private short[][] ky;
  
  public StepFilterRunOnGpu(short[] a, short[] b, short[][] kx, short[][] ky, 
    int index, int num){
    
    this.a = a;
    this.b = b;
    this.index = index;
    this.kx = kx;
    this.ky = ky;
    this.num = num;
  }
  
  @Override
  public void gpuMethod() {
    index /= 4;
    int end = index + num;
    if(end > a.length)
      end = a.length;
    
    for(int i = index; i < end; ++i){
      int sx = convolve(i, kx);
      int sy = convolve(i, ky);

      if(sx*sx+sy*sy > 100*100){
        b[i] = 0; 
      } else {
        b[i] = 255;
      }
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
  
  boolean compare(StepFilterRunOnGpu brhs) {
    if(a.length != brhs.a.length){
      System.out.println("len failed");
      System.out.println("c.length: "+a.length);
      System.out.println("brhs.c.length: "+brhs.a.length);
      return false;
    }
    if(a[index] != brhs.a[index]){
      System.out.println("a failed");
      System.out.println("lhs: "+a[index]);
      System.out.println("rhs: "+brhs.a[index]);
      return false; 
    }
    if(b[index] != brhs.b[index]){
      System.out.println("b failed");
      System.out.println("lhs: "+b[index]);
      System.out.println("rhs: "+brhs.b[index]);
      return false;
    }
    return true;
  }
}
