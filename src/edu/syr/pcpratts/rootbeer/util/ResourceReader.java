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

import edu.syr.pcpratts.rootbeer.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourceReader {

  public static String getResource(String path) throws IOException {
    InputStream is = ResourceReader.class.getResourceAsStream(path);
    StringBuilder ret = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    while(true){
      String line = reader.readLine();
      if(line == null)
        break;
      ret.append(line + "\n");
    }
    is.close();
    return ret.toString();
  }
  
  public static List<byte[]> getResourceArray(String jar_path) throws IOException {
    InputStream is = ResourceReader.class.getResourceAsStream(jar_path);
    if(is == null){
      jar_path = Constants.OUTPUT_CLASS_FOLDER + File.separator + jar_path;
      is = new FileInputStream(jar_path);
    }
    List<byte[]> ret = new ArrayList<byte[]>();
    while(true){
      byte[] buffer = new byte[32*1024];
      int len = is.read(buffer);
      if(len == -1)
        break;
      byte[] small_buffer = new byte[len];
      for(int i = 0; i < len; ++i){
        small_buffer[i] = buffer[i];
      }
      ret.add(small_buffer);
    }
    is.close();
    return ret;
  }
}
