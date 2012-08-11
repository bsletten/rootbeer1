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

package edu.syr.pcpratts.rootbeer.testcases.otherpackage2;

public class StaticsTest2PublicClass {
  
  private static int m_IntValue;
  
  static {
    m_IntValue = 20;
  }
  
  public int getNumber(){
    return m_IntValue + StaticsTest2PrivateClass.getPrivateNumber();
  }
  
  public void setNumber2(int value){
    StaticsTest2PrivateClass.setPrivateNumber2(value);
  }
  
  public int getNumber2(){
    return StaticsTest2PrivateClass.getPrivateNumber2();
  }
}
