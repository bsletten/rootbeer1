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

package edu.syr.pcpratts.rootbeer.runtime;

import java.util.Map;
import org.apache.commons.collections.map.IdentityMap;

public abstract class ReadOnlyAnalyzer {

  protected Map<Object, Boolean> m_Map;
  
  public ReadOnlyAnalyzer(){
    m_Map = new IdentityMap();
  }
  
  public abstract void analyze(Kernel root);
  
  public boolean isReadOnly(Object o){
    return m_Map.get(o);
  }
}
