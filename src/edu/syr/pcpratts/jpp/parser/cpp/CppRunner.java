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

package edu.syr.pcpratts.jpp.parser.cpp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CppRunner {
  
  public CppRunner(){
  }
  
  public Reader run(String input) throws Exception {
    List<String> lines = getLines(input);
    StringBuilder builder = new StringBuilder();
    for(String str : lines){
      builder.append(str);
      builder.append("\n");
    }
    return new StringReader(builder.toString());
  }

  private List<String> getLines(String input) throws Exception {
    List<String> ret = new ArrayList<String>();
    Process p = Runtime.getRuntime().exec("cpp -P "+input);
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    while(true){
      String line = reader.readLine();
      if(line == null)
        break;
      ret.add(line);
    }
    reader.close();
    return ret;
  }
    
  public static void main(String[] args){  
    try {
      CppRunner runner = new CppRunner();
      String file = "/home/pcpratts/code/jpp/gcc-testsuite/c-c++-common/torture/complex-sign-mul-minus-one.c";
      Reader reader1 = runner.run(file);
      BufferedReader reader = new BufferedReader(reader1);
      while(true){
        String line = reader.readLine();
        if(line == null)
          break;
        System.out.println(line);
      }
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
