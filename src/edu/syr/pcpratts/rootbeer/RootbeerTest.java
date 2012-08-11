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

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import edu.syr.pcpratts.rootbeer.test.LoadTestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestException;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RootbeerTest {
  
  public RootbeerTest(){
  }
  
  public void runTests(String test_case) {
    RootbeerCompiler compiler = new RootbeerCompiler();
    String dest_jar = "output.jar";   
    try {
      if(test_case == null){
        compiler.compile("Rootbeer.jar", dest_jar);
      } else {
        compiler.compile("Rootbeer.jar", dest_jar, test_case);
      }
      
      JarClassLoader loader_factory = new JarClassLoader(dest_jar);
      ClassLoader cls_loader = loader_factory.getLoader();
      Thread.currentThread().setContextClassLoader(cls_loader);
      
      Class agent_class = cls_loader.loadClass("edu.syr.pcpratts.rootbeer.RootbeerTestAgent");
      Object agent_obj = agent_class.newInstance();
      Method[] methods = agent_class.getMethods();
      if(test_case == null){
        Method test_method = findMethodByName("test", methods);
        test_method.invoke(agent_obj, cls_loader);
      } else {
        Method test_method = findMethodByName("testOne", methods);
        test_method.invoke(agent_obj, cls_loader, test_case);
      }
      
    } catch(Exception ex){
      ex.printStackTrace();
      System.exit(-1);
    } 
  }
  
  private Method findMethodByName(String name, Method[] methods){
    for(Method method : methods){
      if(method.getName().equals(name)){
        return method;
      }
    }
    return null;
  }
}
