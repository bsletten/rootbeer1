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

import java.io.File;

public class SubsystemTest {

  public static void main(String[] args){
    SubsystemTest test = new SubsystemTest();
    //test.testJar("../../FruitingCompute/ofcoarse/ofcoarse-1.0.jar");
    test.testJar("Rootbeer-Test1");
    //test.testJar("Rootbeer-Test2");
    //test.testJar("Rootbeer-Test3");
    //test.testJar("Rootbeer-Test4");
    //test.testJar("Rootbeer-Test5");
    //test.testJar("SciMark");
  }

  private void testJar(String test_name) {
    String sep = File.separator;
    String jar_file = ".." + sep + test_name + sep + "dist" + sep + test_name + ".jar";
    ExecutionTest tester = new ExecutionTest();
    boolean ret = tester.run(jar_file);
    if(ret){
      System.out.println("All tests pass");
    } else {
      System.out.println("Tests FAIL");
    }
  }
}