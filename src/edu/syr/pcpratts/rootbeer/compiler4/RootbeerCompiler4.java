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

package edu.syr.pcpratts.rootbeer.compiler4;

import java.util.List;
import soot.G;
import soot.PackManager;
import soot.Transform;

public class RootbeerCompiler4 {

  public void compile(String in_jar, String out_jar){
    
    FindGpuMethodTransform finder = new FindGpuMethodTransform();
    Transform transform1 = new Transform("jtp.FindGpuMethodTransform", finder);
    PackManager.v().getPack("jtp").add(transform1);
    
    int size = 4;
    String[] soot_args = new String[size];
    soot_args[0] = "-process-dir";
    soot_args[1] = in_jar;
    soot_args[2] = "-pp";
    soot_args[3] = "-allow-phantom-refs";
    soot.Main.main(soot_args);
    
    G.reset();
    
    List<String> kernel_classes = finder.getKernelClasses();
    for(String kernel : kernel_classes){
      System.out.println(kernel);
    }
    RootbeerTransform rootbeer_transform = new RootbeerTransform(kernel_classes);
    Transform transform2 = new Transform("wjtp.RootbeerTransform", rootbeer_transform);
    PackManager.v().getPack("wjtp").add(transform2);
    
    size = 6;
    soot_args = new String[size];
    soot_args[0] = "-process-dir";
    soot_args[1] = in_jar;
    soot_args[2] = "-pp";
    soot_args[3] = "-allow-phantom-refs";
    soot_args[4] = "-include-all";
    soot_args[5] = "-whole-program";
    soot.Main.main(soot_args);
  }      
}
