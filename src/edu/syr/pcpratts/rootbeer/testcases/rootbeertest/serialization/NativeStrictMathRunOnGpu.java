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

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class NativeStrictMathRunOnGpu implements Kernel {

  private int i;
  private double exp;
  private double log;
  private double log10;
  private double sqrt;
  private double cbrt;
  private double IEEEremainder;
  private double ceil;
  private double floor;
  private double sin;
  private double cos;
  private double tan;
  private double asin;
  private double acos;
  private double atan;
  private double atan2;
  private double pow;
  private double sinh;
  private double cosh;
  private double tanh;
  
  public NativeStrictMathRunOnGpu(int i){
    this.i = i;
  }

  @Override
  public void gpuMethod() {
    exp = java.lang.StrictMath.exp(i);
    log = java.lang.StrictMath.log(i);
    log10 = java.lang.StrictMath.log10(i);
    sqrt = java.lang.StrictMath.sqrt(i);
    cbrt = java.lang.StrictMath.cbrt(i);
    IEEEremainder = java.lang.StrictMath.IEEEremainder(i, i+1);
    ceil = java.lang.StrictMath.ceil(i);
    floor = java.lang.StrictMath.floor(i);
    sin = java.lang.StrictMath.sin(i);
    tan = java.lang.StrictMath.tan(i);
    asin = java.lang.StrictMath.asin(i);
    acos = java.lang.StrictMath.acos(i);
    atan = java.lang.StrictMath.atan(i);
    atan2 = java.lang.StrictMath.atan2(i, i+1);
    pow = java.lang.StrictMath.pow(i, i+1);
    sinh = java.lang.StrictMath.sinh(i);
    cosh = java.lang.StrictMath.cosh(i);
    tanh = java.lang.StrictMath.tanh(i);
  }
  
  private boolean NaN(double value){
    if(value < 0)
      return false;
    if(value > 0)
      return false;
    if(value == 0)
      return false;
    return true;
  }
  
  private boolean eq(double lhs, double rhs){
    if(NaN(lhs) && NaN(rhs))
      return true;
    if(lhs == Double.NEGATIVE_INFINITY && rhs == Double.NEGATIVE_INFINITY)
      return true;
    if(lhs == Double.POSITIVE_INFINITY && rhs == Double.POSITIVE_INFINITY)
      return true;
    double diff = Math.abs(lhs - rhs);
    if(diff < 0.0000000000001)   
      return true;
    return false;
  }
  
  boolean compare(NativeStrictMathRunOnGpu grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(!eq(exp, grhs.exp)){
      System.out.println("exp");
      return false;
    }
    if(!eq(log, grhs.log)){
      System.out.println("log");
      System.out.println("lhs: "+log);
      System.out.println("rhs: "+grhs.log);
      return false;
    }
    if(!eq(log10, grhs.log10)){
      System.out.println("log10");
      return false;
    }
    if(!eq(sqrt, grhs.sqrt)){
      System.out.println("sqrt");
      return false;
    }
    if(!eq(cbrt, grhs.cbrt)){
      System.out.println("cbrt");
      System.out.println("lhs: "+cbrt);
      System.out.println("rhs: "+grhs.cbrt);
      return false;
    }
    if(!eq(IEEEremainder, grhs.IEEEremainder)){
      System.out.println("IEEEremainder");
      return false;
    }
    if(!eq(ceil, grhs.ceil)){
      System.out.println("ceil");
      return false;
    }
    if(!eq(floor, grhs.floor)){
      System.out.println("floor");
      return false;
    }
    if(!eq(sin, grhs.sin)){
      System.out.println("sin");
      return false;
    }
    if(!eq(cos, grhs.cos)){
      System.out.println("cos");
      return false;
    }
    if(!eq(tan, grhs.tan)){
      System.out.println("tan");
      return false;
    }
    if(!eq(asin, grhs.asin)){
      System.out.println("asin");
      System.out.println("lhs: "+asin);
      System.out.println("rhs: "+grhs.asin);
      return false;
    }
    if(!eq(atan2, grhs.atan2)){
      System.out.println("atan2");
      return false;
    }
    if(!eq(pow, grhs.pow)){
      System.out.println("pow");
      System.out.println("lhs: "+pow);
      System.out.println("rhs: "+grhs.pow);
      return false;
    }
    if(!eq(sinh, grhs.sinh)){
      System.out.println("sinh");
      return false;
    }
    if(!eq(cosh, grhs.cosh)){
      System.out.println("cosh");
      return false;
    }
    if(!eq(tanh, grhs.tanh)){
      System.out.println("tanh");
      return false;
    }
    if(!eq(acos, grhs.acos)){
      System.out.println("acos");
      System.out.println("lhs: "+acos);
      System.out.println("rhs: "+grhs.acos);
      return false;
    }
    if(!eq(atan, grhs.atan)){
      System.out.println("atan");
      return false;
    }
    return true;
  }
}