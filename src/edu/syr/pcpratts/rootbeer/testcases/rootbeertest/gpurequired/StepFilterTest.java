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
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class StepFilterTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    int size = 1600*1200*100/4;
    short[] a0 = new short[size];
    short[] b0 = new short[size];
    short[] a1 = new short[size];
    short[] b1 = new short[size];
    short[] a2 = new short[size];
    short[] b2 = new short[size];
    short[] a3 = new short[size];
    short[] b3 = new short[size];
    short[][] kx = {{-1,-2,-1},{0,0,0},{1,2,1}};
    short[][] ky = {{-1,0,1}, {-2,0,2},{-1,0,1}};
    for(int i = 0; i < size; ++i){
      a0[i] = (short) i;
      a1[i] = (short) i;
      a2[i] = (short) i;
      a3[i] = (short) i;
    }
    List<Kernel> jobs = new ArrayList<Kernel>();
    int num = 1675;
    for(int i = 0; i < size * 4; i += num){
      short[] a;
      short[] b;
      if(i < size){
        a = a0;
        b = b0;
      } else if (i < size * 2){
        a = a1;
        b = b1;
      } else if(i < size * 3){
        a = a2;
        b = b2;
      } else {
        a = a3;
        b = b3;
      }
      StepFilterRunOnGpu curr = new StepFilterRunOnGpu(a, b, kx, ky, i, num);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    StepFilterRunOnGpu blhs = (StepFilterRunOnGpu) lhs;
    StepFilterRunOnGpu brhs = (StepFilterRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
