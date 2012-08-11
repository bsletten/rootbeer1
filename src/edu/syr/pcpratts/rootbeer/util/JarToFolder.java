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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarToFolder {

  private StringDelegate m_StringDelegate;
  
  public JarToFolder(){
    m_StringDelegate = null;
  }
  
  public JarToFolder(StringDelegate del){
    m_StringDelegate = del;
  }
  
  public void writeJar(String jar, String folder) throws Exception {
    folder = normalizeFolder(folder);
    ZipInputStream zin = new ZipInputStream(new FileInputStream(jar));
    while(true){
      ZipEntry entry = zin.getNextEntry();
      if(entry == null)
        break;
      if(shouldWrite(entry) == false)
        continue;
      String name = entry.getName();
      if(entry.isDirectory() == false && m_StringDelegate != null && name.endsWith(".class")){
        m_StringDelegate.call(name);
      }
      String s = File.separator;
      if(s.equals("\\")){
        name = name.replace("/", "\\");
      }
      String outname = folder+name;
      
      JarEntryHelp.mkdir(outname);

      if(entry.isDirectory() == false){
        OutputStream fout = new FileOutputStream(outname);
        write(zin, fout);
        fout.flush();
        fout.close();
      }
    }
    zin.close();
  }

  protected boolean shouldWrite(ZipEntry entry){
    return true;
  }

  private String normalizeFolder(String folder){
    String sep = File.separator;
    if(folder.endsWith(sep) == false)
      folder += sep;
    return folder;
  }

  private void write(ZipInputStream zin, OutputStream fout) throws Exception {
    while(true){
      byte[] buffer = new byte[4096];
      int len = zin.read(buffer);
      if(len == -1)
        break;
      fout.write(buffer, 0, len);
    }
  }
}
