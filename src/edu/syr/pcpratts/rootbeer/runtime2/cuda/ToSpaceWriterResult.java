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

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class ToSpaceWriterResult {
  
  private List<Long> m_Handles;
  private List<Kernel> m_Items;
  private List<Kernel> m_NotWrittenItems;
  
  public ToSpaceWriterResult(List<Long> handles, List<Kernel> items,
    List<Kernel> not_written){
    
    m_Handles = handles;
    m_Items = items;
    m_NotWrittenItems = not_written;
  }
  
  public List<Long> getHandles(){
    return m_Handles;
  }
  
  public List<Kernel> getItems(){
    return m_Items;
  }
  
  public List<Kernel> getNotWrittenItems(){
    return m_NotWrittenItems; 
  }
}
