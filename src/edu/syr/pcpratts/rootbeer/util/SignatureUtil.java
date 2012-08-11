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

package edu.syr.pcpratts.rootbeer.util;

public class SignatureUtil {

  public String classFromMethodSig(String signature){
    String[] tokens = signature.split(":");
    String first = tokens[0].trim();
    return first.substring(1);
  }
  
  public String methodSubSigFromMethodSig(String signature){
    String[] tokens = signature.split(":");
    String second = tokens[1].trim();
    String[] tokens2 = second.split("\\)");
    return tokens2[0].trim() + ")";
  }
  
  public static void main(String[] args){
    String sig = "<rootbeertest.GpuWorkItem: void <init>()>";
    SignatureUtil util = new SignatureUtil();
    System.out.println(util.classFromMethodSig(sig));
    System.out.println(util.methodSubSigFromMethodSig(sig));
  }
}
