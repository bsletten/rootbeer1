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

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.List;

public class AcceptableGpuTypes {
  
  private List<String> m_ExcludedPackages;
  
  public AcceptableGpuTypes(){    
    m_ExcludedPackages = new ArrayList<String>();
    m_ExcludedPackages.add("java.");
    m_ExcludedPackages.add("sun.");
    m_ExcludedPackages.add("javax.");
    m_ExcludedPackages.add("com.sun.");
    m_ExcludedPackages.add("com.ibm.");
    m_ExcludedPackages.add("org.xml.");
    m_ExcludedPackages.add("org.w3c.");
    m_ExcludedPackages.add("apple.awt.");
    m_ExcludedPackages.add("com.apple.");
  }
  
  boolean shouldGenerateCtor(String type) {
    if(type.equals("java.lang.StringBuilder"))
      return false;
    if(type.equals("java.lang.AbstractStringBuilder"))
      return false;
    return true;
  }
  
}
