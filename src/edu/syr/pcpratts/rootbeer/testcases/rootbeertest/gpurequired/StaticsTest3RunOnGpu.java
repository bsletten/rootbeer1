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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class StaticsTest3RunOnGpu extends StaticsTest3BaseClass implements Kernel {

  private static char m_Char;
  private static short m_Short;
  private static int m_Int;
  private static long m_Long;
  private static float m_Float;
  private static double m_Double;
  private static boolean m_Bool;
  
  private char m_Char2;
  private short m_Short2;
  private int m_Int2;
  private long m_Long2;
  private float m_Float2;
  private double m_Double2;
  private boolean m_Bool2;
  
  static {
    m_Bool = false; 
  }
  
  public StaticsTest3RunOnGpu(){
    m_Char = 5;
    m_Short = 10;
    m_Int = 15;
    m_Long = 20;
    m_Float = 25.0f;
    m_Double = 30.0;
    m_Bool2 = false;
  }
  
  @Override
  public void gpuMethod() {
    m_Char2 = (char) (m_Char + 1);
    m_Short2 = (short) (m_Short + 1);
    m_Int2 = m_Int + 1;
  }
  
  boolean compare(StaticsTest3RunOnGpu brhs) {
    if(m_Char2 != brhs.m_Char2){
      System.out.println("char");
      System.out.println("lhs: "+(int) m_Char2);
      System.out.println("rhs: "+(int) brhs.m_Char2);
      return false;
    }
    if(m_Short2 != brhs.m_Short2){
      System.out.println("short");
      System.out.println("lhs: "+(int) m_Short2);
      System.out.println("rhs: "+(int) brhs.m_Short2);
      return false;
    }
    if(m_Int2 != brhs.m_Int2){
      System.out.println("int");
      return false;
    }
    return true;
  }

}
