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

public class NewOnGpuRunOnGpu implements Kernel {

  private long[] element1;
  private NewOnGpuRefObject[] test;
  //private long[][] element2;
  private NewOnGpuRefObject obj;
  
  public NewOnGpuRunOnGpu(){
    element1 = new long[10];
    test = new NewOnGpuRefObject[20];
    //element2 = new long[10][10];
    obj = null;
  }

  @Override
  public void gpuMethod() {
    element1 = new long[11];
    test = new NewOnGpuRefObject[44];
    for(int i = 0; i < 44; ++i){
      test[i] = new NewOnGpuRefObject();
    }
    //element2 = new long[50][50];
    obj = new NewOnGpuRefObject();
    
    int x = 20;
    switch(x){
      case 10:
        x = 40;
        break;
      case 20:
        x = 30;
        break;
    }
  }

  boolean compare(NewOnGpuRunOnGpu brhs) {
    if(obj.value != brhs.obj.value)
      return false;
    if(element1.length != brhs.element1.length)
      return false;
    if(test.length != test.length)
      return false;
    for(int i = 0; i < test.length; ++i){
      NewOnGpuRefObject left = test[i];
      NewOnGpuRefObject right = brhs.test[i];
      if(left.value != right.value)
        return false;
    }
    return true;
  }
}
