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

public class StringRunOnGpu implements Kernel {
  private int index;
  private String str;
  
  public StringRunOnGpu(int index){
    this.index = index;
    str = "hello";
  }

  @Override
  public void gpuMethod() {
    str += " world";
    //for(int i = 0; i < 5; ++i){
    //  str += "!";
    //}
  }

  boolean compare(StringRunOnGpu brhs) {
    if(str.equals(brhs.str) == false){
      System.out.println(str);
      System.out.println(brhs.str);
      return false;
    }
    return true;
  }
}
