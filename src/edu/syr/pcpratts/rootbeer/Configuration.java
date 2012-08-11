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

package edu.syr.pcpratts.rootbeer;

import edu.syr.pcpratts.rootbeer.util.ResourceReader;
import java.lang.reflect.Method;
import java.util.List;

public class Configuration {

  public static final int MODE_GPU = 0;
  public static final int MODE_NEMU = 1;
  public static final int MODE_JEMU = 2;
  
  private static Configuration m_Instance;
  
  public static Configuration compilerInstance(){
    if(m_Instance == null){
      m_Instance = new Configuration();
    }
    return m_Instance;
  }
  
  public static Configuration runtimeInstance(){
    if(m_Instance == null){
      m_Instance = new Configuration(true);
    } else if(m_Instance.m_compilerInstance){
      m_Instance = new Configuration(true);
    }
    return m_Instance;
  }
  
  private int m_mode;
  private boolean m_compilerInstance;
  private static boolean m_runAll;
  
  private Configuration(){
    m_compilerInstance = true;
  }

  private Configuration(boolean load) {
    m_compilerInstance = false;
    try {
      List<byte[]> data = ResourceReader.getResourceArray("edu/syr/pcpratts/rootbeer/runtime/config.txt");
      int mode = data.get(0)[0];
      m_mode = mode;
    } catch(Exception ex){
      m_mode = MODE_GPU;
    }
  }
  
  public void setMode(int mode) {
    m_mode = mode;
  }
  
  public int getMode(){
    return m_mode;
  }
  
  public static void setRunAllTests(boolean run_all){
    m_runAll = run_all;
  }
  
  public static boolean getRunAllTests(){
    return m_runAll;
  }
}
