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
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class MMult implements TestSerialization {

  @Override
  public List<Kernel> create() {
    int size = 4096;
    int[] a = new int[size*size];
    int[] b = new int[size*size];
    int[] c = new int[size*size];
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < size; ++i){
      MMultRunOnGpu curr = new MMultRunOnGpu(a, b, c, i, size);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    MMultRunOnGpu blhs = (MMultRunOnGpu) lhs;
    MMultRunOnGpu brhs = (MMultRunOnGpu) rhs;
    return blhs.compare(brhs);
  }
}
