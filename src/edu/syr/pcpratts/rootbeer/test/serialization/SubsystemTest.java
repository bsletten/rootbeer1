package edu.syr.pcpratts.rootbeer.test.serialization;

import java.io.File;

public class SubsystemTest {

  public static void main(String[] args){
    SubsystemTest test = new SubsystemTest();
    test.testJar("../Rootbeer-Test1/dist/Rootbeer-Test1.jar", "test.jar");
    //test.testJar("../Rootbeer-Test2/dist/Rootbeer-Test2.jar", "test.jar");
    //test.testJar("../Rootbeer-Test3/dist/Rootbeer-Test3.jar", "test.jar");    
    //test.testJar("../Rootbeer-Test5/dist/Rootbeer-Test5.jar", "test.jar");
  }

  private void testJar(String jar_file, String out_jar){
    GenerateSerializersAndTest tester = new GenerateSerializersAndTest();
    String sep = File.separator;
    String outfolder = "test-output"+sep;
    File f = new File(outfolder);
    f.mkdir();
    boolean ret = tester.run(jar_file, outfolder+out_jar);

    String passed_string = "PASSED";
    if(!ret)
      passed_string = "FAILED";
    System.out.println(jar_file+" test "+passed_string);

    if(!ret)
      System.exit(-1);
  }
}
