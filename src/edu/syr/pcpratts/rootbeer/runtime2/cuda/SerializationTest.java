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

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.JarClassLoader;
import edu.syr.pcpratts.rootbeer.RootbeerCompiler;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.LoadTestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.List;

public class SerializationTest {

  
  public static void main(String[] args){
     SerializationTest test = new SerializationTest();
     try {
       test.execute("../Rootbeer-Test4/dist/Rootbeer-Test4.jar");
     } catch(Exception ex){
       ex.printStackTrace();
     }
  }
  
  private void execute(String jar) throws Exception {
    RootbeerCompiler compiler = new RootbeerCompiler();
    String dest_jar = "output.jar";
    compiler.compile(jar, dest_jar);   
    
    JarClassLoader loader_factory = new JarClassLoader(dest_jar);
    ClassLoader cls_loader = loader_factory.getLoader();
    Thread.currentThread().setContextClassLoader(cls_loader);
        
    CudaRuntime2 runtime = CudaRuntime2.v();
    
    LoadTestSerialization loader = new LoadTestSerialization();
    List<TestSerialization> creators = loader.load(cls_loader, "rootbeertest.serialization.Main");
    for(TestSerialization creator : creators){
      List<Kernel> items = creator.create();
      runtime.writeBlocks(items.iterator());
      runtime.readBlocks();
    }
  }
}
