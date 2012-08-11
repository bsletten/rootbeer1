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
import java.util.concurrent.atomic.AtomicLong;

public class WhileTrueTest implements TestSerialization{

  public List<Kernel> create() {
    List<Kernel> ret = new ArrayList<Kernel>();
    AtomicLong along = new AtomicLong();
    for(int i = 0; i < 5; ++i){
      Kernel kernel = new WhileTrueRunOnGpu(along, i);
      ret.add(kernel);
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_gpu) {
    WhileTrueRunOnGpu lhs = (WhileTrueRunOnGpu) original;
    WhileTrueRunOnGpu rhs = (WhileTrueRunOnGpu) from_gpu;
    if(lhs.get().get() != rhs.get().get()){
      System.out.println("lhs: "+lhs.get().get());
      System.out.println("rhs: "+rhs.get().get());
      return false;
    }
    return true;
  }
  
}
