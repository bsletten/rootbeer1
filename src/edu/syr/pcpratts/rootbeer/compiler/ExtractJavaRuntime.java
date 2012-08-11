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

package edu.syr.pcpratts.rootbeer.compiler;

import edu.syr.pcpratts.rootbeer.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ExtractJavaRuntime {
  
  private List<String> m_AllClasses;

  private List<String> getRuntimeJars(){
    List<String> ret = new ArrayList<String>();
    String s = File.separator;
    if(System.getProperty("os.name").equals("Mac OS X")) {
	    //in Mac OS X, rt.jar is split into classes.jar and ui.jar
      ret.add(System.getProperty("java.home")+s+".."+s+"Classes"+s+"classes.jar");
      ret.add(System.getProperty("java.home")+s+".."+s+"Classes"+s+"ui.jar");
	  } else {
      //if windows or linux
      ret.add(System.getProperty("java.home")+s+"lib"+s+"rt.jar");
      ret.add(System.getProperty("java.home")+s+"lib"+s+"jce.jar");
      ret.add(System.getProperty("java.home")+s+"lib"+s+"charsets.jar");
      ret.add(System.getProperty("java.home")+s+"lib"+s+"jsse.jar");
      //ret.add(System.getProperty("java.home")+s+".."+s+"lib"+s+"tools.jar");
      
      //if linux add rhino.jar
      if(s.equals("/")){
        ret.add(System.getProperty("java.home")+s+"lib"+s+"rhino.jar");
      }
    }
    return ret;
  }  
  
  public List<String> extract(String folder) throws Exception {
    m_AllClasses = new ArrayList<String>();
    mkdir(folder);
    List<String> rt_jars = getRuntimeJars();
    for(String rt_jar : rt_jars){
      extractJar(rt_jar);
    }
    return m_AllClasses;
  }
  
  private void mkdir(String folder) {
    File f = new File(folder);
    f.mkdirs();
  }
  
  private void extractJar(String rt_jar) throws Exception {
    FileInputStream fin = new FileInputStream(rt_jar);
    JarInputStream jin = new JarInputStream(fin);
    while(true){
      JarEntry entry = jin.getNextJarEntry();
      if(entry == null)
        break;
      if(entry.isDirectory()){
        mkdir(Constants.JAR_CONTENTS_FOLDER+File.separator+entry.getName());
        continue;
      }
      String name = entry.getName();
      if(name.endsWith(".class") == false)
        continue;
      
      m_AllClasses.add(name);
      
      String filename = Constants.JAR_CONTENTS_FOLDER+File.separator+name;
      File f = new File(filename);
      if(f.exists())
        continue;
      
      write(jin, filename);
    }
    jin.close();
  }
  
  private void write(JarInputStream jin, String name) throws Exception {
    mkdir(getDir(name));
    OutputStream fout = new FileOutputStream(name);
    while(true){
      byte[] buffer = new byte[4096];
      int len = jin.read(buffer);
      if(len == -1)
        break;  
      fout.write(buffer, 0, len);
    }
    fout.flush();
    fout.close();
  }
  
  private String getDir(String name) {
    String[] tokens = name.split("/");
    String ret = "";
    for(int i = 0; i < tokens.length - 1; ++i){
      ret += tokens[i] + File.separator;
    }
    return ret;
  }
  
  public static void main(String[] args){
    try {
      ExtractJavaRuntime extractor = new ExtractJavaRuntime();
      extractor.extract(Constants.JAR_CONTENTS_FOLDER);
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
