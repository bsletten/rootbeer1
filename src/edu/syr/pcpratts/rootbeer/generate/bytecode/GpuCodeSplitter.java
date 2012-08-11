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

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.List;

public class GpuCodeSplitter {

  public List<String> split(String code){
    List<String> ret = new ArrayList<String>();
    String[] lines = code.split("\n");
    
    StringBuilder curr_block = new StringBuilder();
    for(int i = 0; i < lines.length; ++i){
      String line = lines[i];
      if(curr_block.length() + line.length() > 60000){
        ret.add(curr_block.toString());
        curr_block = new StringBuilder();
        curr_block.append(line);
        curr_block.append("\n");
      } else {        
        curr_block.append(line);
        curr_block.append("\n");
      }
    }
    String last_line = curr_block.toString();
    if(last_line.length() > 0){
      ret.add(last_line);
    }
    return ret;
  }
}
