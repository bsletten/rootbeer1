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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class NullPointer4RunOnGpu implements Kernel {

  private NullPointer4Object m_Obj;
  private int m_Result;
  private int m_Result2;
  
  public NullPointer4RunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    try {
      m_Result = m_Obj.m_Value;
    } catch(NullPointerException ex){
      m_Result = 10;
    }
    try {
      m_Obj.increment();
    } catch(NullPointerException ex){
      m_Result2 = 20;
    }
  }

  boolean compare(NullPointer4RunOnGpu rhs) {
    if(m_Result != rhs.m_Result){
      System.out.println("result");
      System.out.println("lhs: "+m_Result);
      System.out.println("rhs: "+rhs.m_Result);
      return false;
    }
    if(m_Result2 != rhs.m_Result2){
      System.out.println("result2");
      System.out.println("lhs: "+m_Result2);
      System.out.println("rhs: "+rhs.m_Result2);
      return false;
    }
    return true;
  }
}
