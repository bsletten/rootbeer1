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

public class BruteForceFFTRunOnGpu implements Kernel {

  public float[] a;
  public float[] b;
  private int index;
  private int count;
  
  public BruteForceFFTRunOnGpu(float[] a, float[] b, int index, int count){
    this.a = a;
    this.b = b;
    this.index = index;
    this.count = count;
  }
  
  @Override
  public void gpuMethod() {
    int N = a.length;
    for(int k = index; k < index + count; ++k){
      float sum = 0;
      for(int i = 0; i < N; ++i){
        float term = a[i] * (float) Math.pow(Math.E, (-2*Math.PI/N)*i*k);
        sum += term;
      }
      a[k] = sum;
    }
  }
  
  boolean compare(BruteForceFFTRunOnGpu brhs) {
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
