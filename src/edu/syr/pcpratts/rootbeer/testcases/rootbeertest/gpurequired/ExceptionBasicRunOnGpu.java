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

public class ExceptionBasicRunOnGpu implements Kernel {
  private int index;
  
  public ExceptionBasicRunOnGpu(int index){
    this.index = index;
  }

  @Override
  public void gpuMethod() {
    method2();
  }
  
  private void method2(){
    int[] arr = new int[] {10, 20, 30};
    try {
      try {
        method3();
      } catch(RuntimeException ex){
        index++;
        method3();
      }
    } catch(Throwable ex){
      throw new RuntimeException(ex);
    } 
  }

  private void method3(){
    throw new RuntimeException();
  }
}
