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

package edu.syr.pcpratts.rootbeer.test.serialization;

import edu.syr.pcpratts.rootbeer.JarClassLoader;
import edu.syr.pcpratts.rootbeer.RootbeerCompiler;
import edu.syr.pcpratts.rootbeer.test.LoadTestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.List;

public class GenerateSerializersAndTest {

  public boolean run(String jar_file, String out_jar_file){
    try {
      RootbeerCompiler compiler = new RootbeerCompiler();
      compiler.compile(jar_file, out_jar_file);
      return test(out_jar_file);
    } catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
  }

  private boolean test(String dest_jar) throws Exception {
    JarClassLoader loader_factory = new JarClassLoader(dest_jar);
    ClassLoader cls_loader = loader_factory.getLoader();
    Thread.currentThread().setContextClassLoader(cls_loader);
    
    LoadTestSerialization loader = new LoadTestSerialization();
    List<TestSerialization> creators = loader.load(cls_loader, "rootbeertest.serialization.Main");
    for(TestSerialization creator : creators){
      System.out.println("Testing: "+creator.toString()+"...");
      SerializationTester tester = new SerializationTester(creator);
      boolean ret = tester.test();
      if(ret){
        System.out.println(" PASSED");
      } else {
        System.out.println();
        return false;
      }
    }
    return true;
  }

}
