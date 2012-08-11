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

import edu.syr.pcpratts.rootbeer.util.ResourceReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import soot.SootClass;

public class ClassRemapping {

  private Map<String, String> m_Map;
  private Map<String, String> m_ClonedMap;
  private List<String> m_RuntimeClasses;
  private List<String> m_RuntimeClassesJar;
  
  public ClassRemapping(){
    m_Map = new HashMap<String, String>();
    m_ClonedMap = new HashMap<String, String>();
    m_RuntimeClasses = new ArrayList<String>();
    m_RuntimeClassesJar = new ArrayList<String>();
    put("java.util.concurrent.atomic.AtomicLong", "edu.syr.pcpratts.rootbeer.runtime.remap.GpuAtomicLong");
    put("java.util.Random", "edu.syr.pcpratts.rootbeer.runtime.remap.Random");
  }

  private void put(String key, String value) {
    m_Map.put(key, value);
    m_RuntimeClasses.add(value);
    value = "/" + value.replace(".", "/") + ".class";
    m_RuntimeClassesJar.add(value);
  }
  
  public void cloneClass(String class_name){
    if(m_Map.containsKey(class_name)){
      return;
    }
    String new_name = "edu.syr.pcpratts.rootbeer.runtime.remap."+class_name;
    put(class_name, new_name);
    m_ClonedMap.put(class_name, new_name);
    
    SootClass soot_class = RootbeerScene.v().getClass(class_name);
    CloneClass cloner = new CloneClass();
    SootClass new_class = cloner.execute(soot_class, new_name);
    new_class.setApplicationClass();
    RootbeerScene.v().addClass(new_class);
  }
  
  
  public List<String> getRuntimeClasses(){
    return m_RuntimeClasses;
  }
  
  public List<String> getRuntimeClassesJar(){
    return m_RuntimeClassesJar;
  }

  public boolean containsKey(String cls_name) {
    return m_Map.containsKey(cls_name);
  }

  public String get(String cls_name) {
    return m_Map.get(cls_name);
  }

  public boolean cloned(String cls) {
    return m_ClonedMap.containsKey(cls);
  }

  public List<String> getCloned() {
    List<String> ret = new ArrayList<String>();
    ret.addAll(m_ClonedMap.values());
    return ret;
  }

  private String mapFilename(){
    return "/edu/syr/pcpratts/rootbeer/runtime/remap/remap.txt";
  }
  
  public void loadMap() {
    String contents = "";
    String filename = mapFilename();
    try {
      contents = ResourceReader.getResource(filename);
    } catch(Exception ex){
      contents = readFile("src"+filename);
    }
    doLoadMap(contents);
  }
  
  public void saveMap(){    
    String filename = "src"+mapFilename();
    try {
      PrintWriter writer = new PrintWriter(filename);
      Iterator<String> iter = m_ClonedMap.keySet().iterator();
      while(iter.hasNext()){
        String key = iter.next();
        String value = m_ClonedMap.get(key);
        writer.println(key+" => "+value);
      }
      writer.flush();
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
    
  }

  private String readFile(String filename) {
    String ret = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      while(true){
        String line = reader.readLine();
        if(line == null){
          break;
        }
        ret += line+"\n";
      }
      return ret;
    } catch(Exception ex){
      ex.printStackTrace();
      return ret;
    }    
  }

  private void doLoadMap(String contents) {
    try {
      BufferedReader reader = new BufferedReader(new StringReader(contents));
      while(true){
        String line = reader.readLine();
        if(line == null){
          break;
        }
        String[] tokens = line.split("=>");
        m_ClonedMap.put(tokens[0].trim(), tokens[1].trim()); 
        put(tokens[0].trim(), tokens[1].trim()); 
      }      
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public List<String> getUsed() {
    loadMap();
    List<String> ret = new ArrayList<String>();
    ret.addAll(m_RuntimeClasses);
    ret.addAll(m_ClonedMap.values());
    return ret;
  }
}
