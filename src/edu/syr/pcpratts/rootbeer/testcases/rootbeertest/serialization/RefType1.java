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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

public class RefType1 {

  private char m_Char;
  private byte m_Byte;
  private short m_Short;
  private int m_Int;
  private long m_Long;
  private float m_Float;
  private double m_Double;

  public RefType1(byte value){
    m_Byte = value;
    m_Char = Character.MAX_VALUE;
    m_Short = Short.MAX_VALUE;
    m_Int = Integer.MAX_VALUE;
    m_Long = Integer.MAX_VALUE;
    m_Float = Float.MAX_VALUE;
    m_Double = Double.MAX_VALUE;
  }

  void modify() {
    m_Byte++;
    m_Char--;
    m_Short--;
    m_Int--;
    m_Long--;
    m_Float--;
    m_Double--;
  }

  @Override
  public boolean equals(Object o){

    if(o instanceof RefType1 == false){
      System.out.println("1");
      return false;
    }
    RefType1 other = (RefType1) o;
    if(m_Byte != other.m_Byte){
      System.out.println("2");
      System.out.println("lhs: "+m_Byte);
      System.out.println("rhs: "+other.m_Byte);
      return false;
    }
    if(m_Char != other.m_Char){
      System.out.println("3");
      return false;
    }
    if(m_Short != other.m_Short){
      System.out.println("4");
      return false;
    }
    if(m_Int != other.m_Int){
      System.out.println("5");
      return false;
    }
    if(m_Long != other.m_Long){
      System.out.println("6");
      return false;
    }
    if(m_Float != other.m_Float){
      System.out.println("7");
      return false;
    }
    if(m_Double != other.m_Double){
      System.out.println("8");
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + this.m_Char;
    hash = 89 * hash + this.m_Byte;
    hash = 89 * hash + this.m_Short;
    hash = 89 * hash + this.m_Int;
    hash = 89 * hash + (int) (this.m_Long ^ (this.m_Long >>> 32));
    hash = 89 * hash + Float.floatToIntBits(this.m_Float);
    hash = 89 * hash + (int) (Double.doubleToLongBits(this.m_Double) ^ (Double.doubleToLongBits(this.m_Double) >>> 32));
    return hash;
  }
}
