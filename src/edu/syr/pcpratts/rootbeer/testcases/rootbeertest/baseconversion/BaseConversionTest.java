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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.baseconversion;

import edu.syr.pcpratts.rootbeer.Aug4th2011PerformanceStudy;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.ArrayList;
import java.util.List;

public class BaseConversionTest implements TestSerialization {

  @Override
  public List<Kernel> create() {
    List<Kernel> jobs = new ArrayList<Kernel>();
    int job_size = 5000000;
    int count = 1221;
    for(int i = 0; i < job_size; i += count){
      BaseConversionRunOnGpu curr = new BaseConversionRunOnGpu(i, count);
      jobs.add(curr);
    }
    return jobs;
  }

  @Override
  public boolean compare(Kernel lhs, Kernel rhs) {
    BaseConversionRunOnGpu blhs = (BaseConversionRunOnGpu) lhs;
    BaseConversionRunOnGpu brhs = (BaseConversionRunOnGpu) rhs;
    if(blhs.getRet() == null){
      System.out.println("blhs.getRet() == nill");
      return false;
    }
    if(brhs.getRet() == null){
      System.out.println("brhs.getRet() == nill");
      return false;
    }
    for(int j = 0; j < blhs.getRet().size(); ++j){
      IntList lhs_list = (IntList) blhs.getRet().get(j);
      IntList rhs_list = (IntList) brhs.getRet().get(j);
      for(int i = 0; i < lhs_list.size(); ++i){
        int lhs_value = lhs_list.get(i);
        int rhs_value = rhs_list.get(i);
        if(lhs_value != rhs_value){
          System.out.println("i: "+i+" lhs: "+lhs_value+" rhs: "+rhs_value);
          return false;
        }
      }
    }
    return true;
  }
}
