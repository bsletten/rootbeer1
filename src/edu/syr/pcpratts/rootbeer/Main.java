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

import java.util.ArrayList;
import java.util.List;

public class Main {
  
  public int m_mode;
  
  private boolean m_runTests;
  private String m_testCase;
  private boolean m_simpleCompile;
  
  private String m_mainJar;
  private List<String> m_libJars;
  private List<String> m_directories;
  private String m_destJar;
  
  public Main(){
    m_libJars = new ArrayList<String>();
    m_directories = new ArrayList<String>();
    m_mode = Configuration.MODE_GPU;
    m_simpleCompile = false;
  }
  
  private void parseArgs(String[] args) {
    for(int i = 0; i < args.length; ++i){
      String arg = args[i];
      if(arg.equals("-nemu")){
        m_mode = Configuration.MODE_NEMU;
      } else if(arg.equals("-jemu")){
        m_mode = Configuration.MODE_JEMU;
      } else if(arg.equals("-mainjar")){
        m_mainJar = safeGet(args, i+1, "-mainjar");
        ++i;
      } else if(arg.equals("-libjar")){
        String lib = safeGet(args, i+1, "-libjar");
        m_libJars.add(lib);
        ++i;
      } else if(arg.equals("-directory")){
        String dir = safeGet(args, i+1, "-directory");
        m_directories.add(dir);
        ++i;
      } else if(arg.equals("-destjar")){
        m_destJar = safeGet(args, i+1, "-destjar");
        ++i;
      } else if(arg.equals("-runtests")){
        m_runTests = true;
        m_testCase = null;
      } else if(arg.equals("-runtest")){
        m_runTests = true;
        m_testCase = safeGet(args, i+1, "-runtest");
        ++i;
      } else {      
        m_mainJar = arg;
        m_destJar = safeGet(args, i+1, arg);
        m_simpleCompile = true;
        break;
      }
    }
    Configuration.compilerInstance().setMode(m_mode);
  }
  
  private String safeGet(String[] args, int index, String argname) {
    if(index >= args.length){
      System.out.println(argname+" needs another argument after it.");
      System.exit(-1);
    }
    return args[index];
  }

  private void run() {
    if(m_runTests){
      RootbeerTest test = new RootbeerTest();
      test.runTests(m_testCase);
      return;
    } 
    
    if(m_simpleCompile){
      RootbeerCompiler compiler = new RootbeerCompiler();
      try {
        compiler.compile(m_mainJar, m_destJar);
      } catch(Exception ex){
        ex.printStackTrace();
      }
    } else {
      RootbeerCompiler compiler = new RootbeerCompiler();
      try {
        compiler.compile(m_mainJar, m_libJars, m_directories, m_destJar);
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }
  
  public static void main(String[] args){
    Main main = new Main();
    main.parseArgs(args);
    main.run();
  }
}
