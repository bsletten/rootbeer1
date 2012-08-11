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
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.OuterClassRunOnGpu.OuterClassRunOnGpu2;

public class OuterClassTest2 implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    for(int i = 0; i < 500; ++i){
      OuterClassRunOnGpu curr_outer = new OuterClassRunOnGpu();
      OuterClassRunOnGpu.OuterClassRunOnGpu2 curr = curr_outer.new OuterClassRunOnGpu2();
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    OuterClassRunOnGpu.OuterClassRunOnGpu2 blhs = (OuterClassRunOnGpu.OuterClassRunOnGpu2) lhs;
    OuterClassRunOnGpu.OuterClassRunOnGpu2 brhs = (OuterClassRunOnGpu.OuterClassRunOnGpu2) rhs;
    return blhs.compare(brhs);
  }
}