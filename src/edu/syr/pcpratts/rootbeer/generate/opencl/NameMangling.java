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

package edu.syr.pcpratts.rootbeer.generate.opencl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import soot.SootMethod;
import soot.Type;

public class NameMangling {

  private static NameMangling mInstance = null;
  private Map<String, Integer> mangleMap;
  private int lastInt;
  
  public static NameMangling v(){
    if(mInstance == null)
      mInstance = new NameMangling();
    return mInstance;
  }
  
  private NameMangling(){
    mangleMap = new HashMap<String, Integer>();
    lastInt = 0;
  }

  public String mangleArgs(SootMethod method){
    String ret = "";

    Type return_type = method.getReturnType();
    ret += mangle(return_type);
    
    List parameter_types = method.getParameterTypes();
    for(int i = 0; i < parameter_types.size(); ++i){
      Type type = (Type) parameter_types.get(i);
      ret += mangle(type);
    }
    return ret;
  }

  public String mangle(Type type){
    String name_without_arrays = type.toString();
    name_without_arrays = name_without_arrays.replace("\\[", "a");

    int number;
    if(mangleMap.containsKey(name_without_arrays)){
      number = mangleMap.get(name_without_arrays);
    } else {
      number = lastInt;
      lastInt++;
      mangleMap.put(name_without_arrays, number);
    }

    int dims = arrayDimensions(type);
    String ret = "";
    for(int i = 0; i < dims; ++i)
      ret += "a";
    ret += Integer.toString(number);
    return ret+"_";
  }

  private int arrayDimensions(Type type){
    int ret = 0;
    String str = type.toString();
    for(int i = 0; i < str.length(); ++i){
      char c = str.charAt(i);
      if(c == '[')
        ret++;
    }
    return ret;
  }
}
