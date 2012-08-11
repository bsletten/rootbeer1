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

import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.CompositeClass4;

class CompositeClass5 extends CompositeClass4 {
  
  private long m_ThisWillBreakStuff;
  private int m_Modified;
  
  public CompositeClass5(){
    super(5000);        
  }
  
  @Override
  public int go(){
    m_Modified = 40;
    m_ThisWillBreakStuff = 20;
    return super.go() + (int) m_ThisWillBreakStuff;
  }
  
  public int getModified(){
    return m_Modified;
  }
}
