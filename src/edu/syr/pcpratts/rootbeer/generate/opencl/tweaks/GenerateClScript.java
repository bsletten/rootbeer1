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

package edu.syr.pcpratts.rootbeer.generate.opencl.tweaks;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerateClScript {

  private List<String> m_VisualStudioPaths;
  private List<String> m_NvidiaPaths;
  
  public GenerateClScript(){
    m_VisualStudioPaths = new ArrayList<String>();
    m_VisualStudioPaths.add("C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\VC\\vcvarsall.bat");
    m_VisualStudioPaths.add("C:\\Program Files\\Microsoft Visual Studio 10.0\\VC\\vcvarsall.bat");
    
    m_NvidiaPaths = new ArrayList<String>();
    m_NvidiaPaths.add("C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\v4.0\\bin\\nvcc.exe");
  }
  
  public File execute(File generated, File code_file) {
    String vs_path = findPath(m_VisualStudioPaths, "Visual Studio");
    String nvidia_path = findPath(m_NvidiaPaths, "nvcc.exe");
        
    String file_text = "";
    String endl = System.getProperty("line.separator"); 
    file_text += "@call \""+vs_path+"\" amd64"+endl;
    file_text += "\""+nvidia_path+"\" -arch sm_20 -cubin \""+generated.getAbsolutePath()+"\" -o \""+code_file.getAbsolutePath()+"\""+endl;
    File ret = new File("cl_script.bat");
    try {
      PrintWriter writer = new PrintWriter(ret);
      writer.println(file_text);
      writer.flush();
      writer.close();
      return ret;
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }

  private String findPath(List<String> paths, String desc) {
    for(String path : paths){
      File file = new File(path);
      if(file.exists())
        return path;
    }
    throw new RuntimeException("cannot find path for: "+desc);
  }
  
}
