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

public class SimpleSynchronizedTest implements TestSerialization {

  public List<Kernel> create() {
    SimpleSynchronizedObject sync_obj = new SimpleSynchronizedObject();
    List<Kernel> ret = new ArrayList<Kernel>();
    for(int i = 0; i < 20; ++i){
      Kernel kernel = new SimpleSynchronizedRunOnGpu(sync_obj);
      ret.add(kernel);
    }
    return ret;
  }

  public boolean compare(Kernel original, Kernel from_gpu) {
    SimpleSynchronizedRunOnGpu lhs = (SimpleSynchronizedRunOnGpu) original;
    SimpleSynchronizedRunOnGpu rhs = (SimpleSynchronizedRunOnGpu) from_gpu;
    
    if(lhs.getSyncObj().get() != rhs.getSyncObj().get()){
      System.out.println("lhs: "+lhs.getSyncObj().get());
      System.out.println("rhs: "+rhs.getSyncObj().get());
      return false;
    }
    
    return true;
  }
  
}
