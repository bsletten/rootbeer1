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

import java.io.File;

public class JarEntryHelp {

  public static void mkdir(String name) {
    String dir_name = "";
    if(name.endsWith(File.separator)){
      dir_name = name;
    } else {
      String s = File.separator;
      if(s.equals("\\")){
        s = "\\\\";
        name = name.replace("/", "\\");
      }
      String[] tokens = name.split(s);
      if(name.startsWith(File.separator))
        dir_name += File.separator;
      for(int i = 0; i < tokens.length - 1; ++i){
        dir_name += tokens[i] + File.separator;
      }
    }
    File f = new File(dir_name);
    f.mkdirs();
  }
}
