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

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class BaseConversionRunOnGpu implements Kernel {

  private int m_Index;
  private GpuList m_Ret;
  private int m_Count;
  
  public BaseConversionRunOnGpu(int index, int count) {
    m_Index = index;
    m_Count = count;
    m_Ret = new GpuList();
  }
  
  @Override
  public void gpuMethod() {  
    int count = m_Count;
    int start_index = m_Index;
    int n = 9600;
    int len = 3;
    IntList ret_list = new IntList();
    for(int i = 0; i < count; ++i){
      int index = start_index + i;
      while(index > 0){
        int mod = index % n;      
        ret_list.add(mod);
        index /= n;
      }
      if(index != 0){    
        ret_list.add(index);
      }
      while(ret_list.size() < len){   
        ret_list.add(0);
      }
    }
    m_Ret.add(ret_list);      
  }

  public GpuList getRet(){
    return m_Ret;
  }
}
