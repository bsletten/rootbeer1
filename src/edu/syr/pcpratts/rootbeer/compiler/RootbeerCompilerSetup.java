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

import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.compiler.ClassRemapping;
import edu.syr.pcpratts.rootbeer.util.DeleteFolder;
import edu.syr.pcpratts.rootbeer.util.JarToFolder;
import edu.syr.pcpratts.rootbeer.util.ReadJar;
import edu.syr.pcpratts.rootbeer.util.StringDelegate;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RootbeerCompilerSetup implements StringDelegate {
  private List<String> m_AllClasses;
  
  public List<String> setup(String single_jar_file){
    List<String> jar_files = new ArrayList<String>();
    jar_files.add(single_jar_file);
    return setup(jar_files);
  }
  
  public List<String> setup(List<String> jar_files) {
    m_AllClasses = new ArrayList<String>();
    clearOutputFolders();
    writeJars(jar_files);
    writeRuntimeClasses();
    return m_AllClasses;
  }  
  
  private void writeJars(List<String> jar_files) {
    String folder = Constants.JAR_CONTENTS_FOLDER;
    File folder_file = new File(folder);
    folder_file.mkdirs();

    try {
      JarToFolder jtf = new JarToFolder(this);
      for(String jar : jar_files){
        jtf.writeJar(jar, folder);
      } 
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }

  private void writeRuntimeClasses() {
    List<String> classes = getRuntimeClasses();
    ClassRemapping remapping = new ClassRemapping();
    classes.addAll(remapping.getRuntimeClassesJar());
    for(String cls : classes){
      writeRuntimeClass(cls);
    }
  }
  
  private List<String> getRuntimeClasses(){
    List<String> ret = new ArrayList<String>();
    ret.add("/edu/syr/pcpratts/rootbeer/generate/bytecode/Constants.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/RootbeerFactory.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/Rootbeer.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/RootbeerGpu.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/Kernel.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/CompiledKernel.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/Serializer.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/memory/Memory.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/Sentinal.class");
    ret.add("/edu/syr/pcpratts/rootbeer/test/TestSerialization.class");
    ret.add("/edu/syr/pcpratts/rootbeer/test/TestSerializationFactory.class");
    ret.add("/edu/syr/pcpratts/rootbeer/test/TestException.class");
    ret.add("/edu/syr/pcpratts/rootbeer/test/TestExceptionFactory.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/util/Stopwatch.class");
    ret.add("/edu/syr/pcpratts/rootbeer/runtime/PrivateFields.class");
    m_AllClasses.addAll(ret);
    return ret;
  }

  private void writeRuntimeClass(String cls) {
    ReadJar reader = new ReadJar();
    reader.writeRuntimeClass(cls);
  }
  
  private void clearOutputFolders() {
    DeleteFolder deleter = new DeleteFolder();
    deleter.delete(Constants.OUTPUT_JAR_FOLDER);
    deleter.delete(Constants.OUTPUT_CLASS_FOLDER);
    deleter.delete(Constants.OUTPUT_SHIMPLE_FOLDER);
  }

  public void call(String value) {
    m_AllClasses.add(value);
  }
}
