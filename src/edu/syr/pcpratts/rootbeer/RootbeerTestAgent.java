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
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RootbeerTestAgent {

  private long m_cpuTime;
  private long m_gpuTime;
  private boolean m_passed;
  private String m_message;
  private List<String> m_failedTests;
  
  public RootbeerTestAgent(){
    m_failedTests = new ArrayList<String>();
  }
  
  public void testOne(ClassLoader cls_loader, String test_case) throws Exception {
    Configuration.setRunAllTests(false);
    Class test_case_cls = cls_loader.loadClass(test_case);
    Object test_case_obj = test_case_cls.newInstance();
    if(test_case_obj instanceof TestSerialization){
      TestSerialization test_ser = (TestSerialization) test_case_obj;
      System.out.println("[TEST 1/1] "+test_ser.toString());
      test(test_ser);      
      if(m_passed){
        System.out.println("  PASSED");
        System.out.println("  Cpu time: "+m_cpuTime+" ms");
        System.out.println("  Gpu time: "+m_gpuTime+" ms");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
      }   
    } else if(test_case_obj instanceof TestException){
      TestException test_ex = (TestException) test_case_obj;
      System.out.println("[TEST 1/1] "+test_ex.toString());
      ex_test(test_ex);
      if(m_passed){
        System.out.println("  PASSED");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
      }        
    } else {
      throw new RuntimeException("unknown test case type");
    }
  }
  
  public void test(ClassLoader cls_loader) throws Exception {
    Configuration.setRunAllTests(true);
    LoadTestSerialization loader = new LoadTestSerialization();
    List<TestSerialization> creators = loader.load(cls_loader, "edu.syr.pcpratts.rootbeer.testcases.rootbeertest.Main");
    List<TestException> ex_creators = loader.loadException(cls_loader, "edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ExMain");
    int num_tests = creators.size() + ex_creators.size();
    int test_num = 1;

    for(TestSerialization creator : creators){
      System.out.println("[TEST "+test_num+"/"+num_tests+"] "+creator.toString());
      test(creator);
      if(m_passed){
        System.out.println("  PASSED");
        System.out.println("  Cpu time: "+m_cpuTime+" ms");
        System.out.println("  Gpu time: "+m_gpuTime+" ms");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
        m_failedTests.add(creator.toString());
      }        
      ++test_num;
    }

    for(TestException ex_creator : ex_creators){
      System.out.println("[TEST "+test_num+"/"+num_tests+"] "+ex_creator.toString());
      ex_test(ex_creator);
      if(m_passed){
        System.out.println("  PASSED");
      } else {
        System.out.println("  FAILED");
        System.out.println("  "+m_message);
        m_failedTests.add(ex_creator.toString());
      }        
      ++test_num;
    }

    int test_passed = num_tests - m_failedTests.size();
    System.out.println(test_passed+"/"+num_tests+" tests PASS");
    if(test_passed == num_tests){
      System.out.println("ALL TESTS PASS!");
    } else {
      System.out.println("Failing tests:");
      for(String failure : m_failedTests){
        System.out.println("  "+failure);
      }
    } 
  }
  
  
  private void test(TestSerialization creator) {
    int i = 0;
    try {      
      Rootbeer rootbeer = new Rootbeer();
      List<Kernel> known_good_items = creator.create();
      List<Kernel> testing_items = creator.create();
      Stopwatch watch = new Stopwatch();
      watch.start();
      rootbeer.runAll(testing_items);
      m_passed = true;
      watch.stop();
      m_gpuTime = watch.elapsedTimeMillis();
      watch.start();
      for(i = 0; i < known_good_items.size(); ++i){       
        Kernel known_good_item = known_good_items.get(i);
        known_good_item.gpuMethod();
      }
      watch.stop();
      m_cpuTime = watch.elapsedTimeMillis();
      for(i = 0; i < known_good_items.size(); ++i){
        Kernel known_good_item = known_good_items.get(i);
        Kernel testing_item = testing_items.get(i);
        if(!creator.compare(known_good_item, testing_item)){
          m_message = "Compare failed at: "+i;
          m_passed = false;
          return;
        }
      }
    } catch(Throwable ex){
      m_message = "Exception thrown at index: "+i+"\n";
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(os);
      ex.printStackTrace(writer);
      writer.flush();
      writer.close();
      m_message += os.toString();
      m_passed = false;
    }
  }

  private void ex_test(TestException creator) {
    Rootbeer rootbeer = new Rootbeer();
    List<Kernel> testing_items = creator.create();
    try {
      rootbeer.runAll(testing_items);
      m_passed = false;
      m_message = "No exception thrown when expecting one.";
    } catch(Throwable ex){
      m_passed = creator.catchException(ex);
      if(m_passed == false){
        m_message = "Exception is: "+ex.toString(); 
      }
    }
  }
}
