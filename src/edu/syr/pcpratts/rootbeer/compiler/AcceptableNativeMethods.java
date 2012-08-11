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

package edu.syr.pcpratts.rootbeer.compiler;

import java.util.HashSet;
import java.util.Set;

public class AcceptableNativeMethods {

  private Set<String> m_OkMethods;
  
  public AcceptableNativeMethods(){
    m_OkMethods = new HashSet<String>();
    m_OkMethods.add("<java.lang.Throwable: java.lang.Throwable fillInStackTrace()>");
    m_OkMethods.add("<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>");
    m_OkMethods.add("<java.lang.Double: long doubleToRawLongBits(double)>");
    m_OkMethods.add("<java.lang.Double: double longBitsToDouble(long)>");
    m_OkMethods.add("<java.lang.Float: int floatToRawIntBits(float)>");
    m_OkMethods.add("<java.lang.Float: float intBitsToFloat(int)>");
  }
  
  public boolean isOk(String method_signature){
    return m_OkMethods.contains(method_signature);
  }
}
