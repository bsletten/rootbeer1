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

package edu.syr.pcpratts.rootbeer.test.execution;

import edu.syr.pcpratts.rootbeer.JarClassLoader;
import edu.syr.pcpratts.rootbeer.RootbeerCompiler;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import edu.syr.pcpratts.rootbeer.test.LoadTestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestException;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.io.File;
import java.util.List;

class ExecutionTest {

  boolean run(String jar_file) {
    try {
      RootbeerCompiler compiler = new RootbeerCompiler();
      String dest_jar = "output.jar";
      compiler.compile(jar_file, dest_jar);
    
      JarClassLoader loader_factory = new JarClassLoader(dest_jar);
      ClassLoader cls_loader = loader_factory.getLoader();
      Thread.currentThread().setContextClassLoader(cls_loader);
      
      System.out.println("Running execution test");
      LoadTestSerialization loader = new LoadTestSerialization();
      List<TestSerialization> creators = loader.load(cls_loader, "rootbeertest.Main");
      for(TestSerialization creator : creators){
        System.out.println("Testing: "+creator.toString()+"...");
        boolean ret = test(creator);
        if(ret){
          System.out.println("PASSED");
        } else {
          System.out.println("FAILED");
          return false;
        }
      }
      List<TestException> ex_creators;
      try {
        ex_creators = loader.loadException(cls_loader, "rootbeertest.ExMain");
        for(TestException ex_creator : ex_creators){
          System.out.println("Testing: "+ex_creator.toString()+"...");
          boolean ret = test(ex_creator);
          if(ret){
            System.out.println("PASSED");
          } else {
            System.out.println("FAILED");
            return false;
          }
        }
      } catch(Exception ex){
        ex.printStackTrace();
      }
      
      List<TestSerialization> perf_creators;
      try {
        perf_creators = loader.load(cls_loader, "rootbeertest.PerformanceTests");      
        for(TestSerialization creator : perf_creators){
          System.out.println("Testing: "+creator.toString()+"...");
          boolean ret = test(creator);
          if(ret){
            System.out.println("PASSED");
          } else {
            System.out.println("FAILED");
            return false;
          }
        }
      } catch(Exception ex){
        ex.printStackTrace();
      }      
      return true;
    } catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
  }

  private boolean test(TestException creator) {
    Rootbeer rootbeer = new Rootbeer();
    List<Kernel> testing_items = creator.create();
    try {
      rootbeer.runAll(testing_items);
      System.out.println("No exception thrown.");
      return false;
    } catch(Throwable ex){
      return creator.catchException(ex);
    }
  }
  
  private boolean test(TestSerialization creator) {
    int i = 0;
    try {      
      Rootbeer rootbeer = new Rootbeer();
      List<Kernel> known_good_items = creator.create();
      List<Kernel> testing_items = creator.create();
      Stopwatch watch = new Stopwatch();
      watch.start();
      rootbeer.runAll(testing_items);
      boolean passed = true;
      watch.stopAndPrint("GPU time: ");
      watch.start();
      for(i = 0; i < known_good_items.size(); ++i){       
        Kernel known_good_item = known_good_items.get(i);
        known_good_item.gpuMethod();
      }
      watch.stopAndPrint("CPU time: ");
      for(i = 0; i < known_good_items.size(); ++i){
        Kernel known_good_item = known_good_items.get(i);
        Kernel testing_item = testing_items.get(i);
        if(!creator.compare(known_good_item, testing_item)){
          System.out.println("Results failed at: "+i);
          passed = false;
          return false;
        }
      }
      
      return passed;
    } catch(Throwable ex){
      System.out.println("Results failed at: "+i);
      ex.printStackTrace();
      return false;
    }
  }

  private void createFolder(String folder) {
    File f = new File(folder);
    if(f.exists())
      return;
    f.mkdirs();
  }

}
