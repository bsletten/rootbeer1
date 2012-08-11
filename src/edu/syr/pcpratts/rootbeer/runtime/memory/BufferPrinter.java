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

package edu.syr.pcpratts.rootbeer.runtime.memory;

import java.util.ArrayList;
import java.util.List;

public class BufferPrinter {

  public void print(Memory mem, long start_ptr, int length){
    long previous = mem.getPointer();
    mem.setAddress(start_ptr);

    List<String> lines = new ArrayList<String>();
    String curr_line = "";
    int elements_per_line = 16;

    int item_count = 1;
    for(int i = 0; i < length; ++i){
      String curr = getString(mem.readByte());
      curr_line += curr+" ";
      if(item_count >= elements_per_line){
        lines.add(curr_line);
        curr_line = "";
        item_count = 1;
      } else {
        item_count++;
      }
    }
    if(curr_line.equals("") == false)
      lines.add(curr_line);

    List<String> line_numbers = createLineNumbers(lines.size(), elements_per_line, start_ptr);
    for(int i = 0; i < lines.size(); ++i){
      System.out.print(line_numbers.get(i));
      System.out.println(lines.get(i));
    }
    
    mem.setAddress(previous);
  }

  private String getString(byte data) {
    String ret = Integer.toHexString(data);
    while(ret.length() < 2){
      ret = "0"+ret;
    }
    return ret.substring(ret.length()-2);
  }

  private List<String> createLineNumbers(int num_lines, int elements_per_line,
    long start_ptr) {

    List<String> unpadded = new ArrayList<String>();
    for(int i = 0; i < num_lines; ++i){
      long line_num = start_ptr + (i * elements_per_line);
      unpadded.add(line_num+": ");
    }
    int max_len = unpadded.get(unpadded.size()-1).length();
    List<String> ret = new ArrayList<String>();
    for(int i = 0; i < unpadded.size(); ++i){
      String curr = unpadded.get(i);
      while(curr.length() < max_len)
        curr = " "+curr;
      ret.add(curr);
    }
    return ret;
  }
}

