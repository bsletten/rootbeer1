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

package edu.syr.pcpratts.jpp;

public class JppSettings {

  private static JppSettings m_Instance;

  public static JppSettings v(){
    if(m_Instance == null)
      m_Instance = new JppSettings();
    return m_Instance;
  }

  private boolean m_IsCpp;

  public boolean isCpp(){
    return m_IsCpp;
  }

  public void setCpp(boolean value){
    m_IsCpp = value;
  }
}
