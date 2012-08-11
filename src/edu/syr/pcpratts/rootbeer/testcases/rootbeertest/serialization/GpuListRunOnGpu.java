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

public class GpuListRunOnGpu implements Kernel {

  private GpuList<Item> m_List;
  
  public GpuListRunOnGpu(){
    m_List = new GpuList<Item>();
  }
  
  @Override
  public void gpuMethod() {
    m_List = new GpuList<Item>();
    for(int i = 0; i < 10; ++i){
      m_List.add(new Item());
    }
  }
  
  public GpuList<Item> getList(){
    return m_List;
  }
}
