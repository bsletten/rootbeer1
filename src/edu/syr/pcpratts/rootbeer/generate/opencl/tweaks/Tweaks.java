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

package edu.syr.pcpratts.rootbeer.generate.opencl.tweaks;

import java.util.List;

public abstract class Tweaks {

  private static Tweaks m_Instance;
  
  public static Tweaks v(){
    return m_Instance;
  }
  
  public static void setInstance(Tweaks instance){
    m_Instance = instance;
  }

  public abstract String getHeaderPath();
  public abstract String getGlobalAddressSpaceQualifier();
  public abstract String getGarbageCollectorPath();
  public abstract String getKernelPath();
  public abstract String getDeviceFunctionQualifier();
}
