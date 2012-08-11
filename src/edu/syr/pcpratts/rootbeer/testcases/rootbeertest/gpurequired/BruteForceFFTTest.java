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

public class BruteForceFFTTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    int size = 114688;
    float[] a = new float[size];
    float[] b = new float[size];
    for(int i = 0; i < size; ++i){
      a[i] = (short) i;
    }
    List<Kernel> jobs = new ArrayList<Kernel>();
    int num = 256;
    for(int i = 0; i < size; i += num){
      int count = num;
      if(i + num > a.length){
        count = i + num - a.length;
        count--;
      }
      BruteForceFFTRunOnGpu curr = new BruteForceFFTRunOnGpu(a, b, i, count);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    BruteForceFFTRunOnGpu blhs = (BruteForceFFTRunOnGpu) lhs;
    BruteForceFFTRunOnGpu brhs = (BruteForceFFTRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
