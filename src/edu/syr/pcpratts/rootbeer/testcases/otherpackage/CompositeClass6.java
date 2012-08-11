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

package edu.syr.pcpratts.rootbeer.testcases.otherpackage;

public class CompositeClass6 extends CompositeClass5 {

  private long m_Shared;
  private int m_Modified;
  private String m_HelloStrings;
  
  public CompositeClass6(){
    m_Shared = 100;
    m_Modified = 50;
    m_HelloStrings = "hello world";
  }

  @Override
  public int go() {
    m_Shared = 50;
    m_Modified = 40;
    m_HelloStrings += "hello";
    return super.go() + m_HelloStrings.length();
  }
  
  @Override
  public int getModified(){
    return m_Modified + super.getModified();
  }
}
