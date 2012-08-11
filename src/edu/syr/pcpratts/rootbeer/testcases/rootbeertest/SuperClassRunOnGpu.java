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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

import edu.syr.pcpratts.rootbeer.testcases.otherpackage.CompositeClass6;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class SuperClassRunOnGpu implements Kernel {

  private CompositeClass6 m_Class;
  private int ret;
  private String str;
  
  public SuperClassRunOnGpu(){
    m_Class = new CompositeClass6(); 
    str = "hello";
  }
  
  @Override
  public void gpuMethod() {
    m_Class = new CompositeClass6();
    ret = m_Class.go();
    //str += " world";
    //for(int i = 0; i < 5; ++i){
    //  str += "!";
    //}
  }
  
  public int getRet(){
    return ret;
  }

  boolean compare(SuperClassRunOnGpu brhs) {
    if(str.equals(brhs.str) == false){
      System.out.println("Failed at str");
      System.out.println("lhs: "+str);
      System.out.println("rhs: "+brhs.str);
      return false;
    }
    if(ret != brhs.ret){
      System.out.println("Failed at ret");
      System.out.println("lhs: "+ret);
      System.out.println("rhs: "+brhs.ret);
      return false;
    }
    /*
    if(m_Class.getModified() != brhs.m_Class.getModified()){
      System.out.println("Failed at m_Class");
      System.out.println("lhs: "+m_Class.getModified());
      System.out.println("rhs: "+brhs.m_Class.getModified());
      return false;
    }
    */
    return true;
  }
}
