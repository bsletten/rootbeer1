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

public class DeleteFolder {

  public void delete(String folder){
    File f = new File(folder);
    if(f.exists() == false)
      return;
    File[] files = f.listFiles();
    for(File file : files){
      if(file.isDirectory()){
        delete(file.getAbsolutePath());
        file.delete();
      } else if(file.isFile()){
        file.delete();
      }
    }
    f.delete();
  }
}
