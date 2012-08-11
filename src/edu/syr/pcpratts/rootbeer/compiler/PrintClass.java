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

import edu.syr.pcpratts.rootbeer.compiler.AddBasicClass;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.util.JimpleWriter;
import java.util.List;
import soot.SootClass;
import soot.SootMethod;

public class PrintClass {

  public void print(String cls){
    while(true){
      try {
        loadClass(cls);
        break;
      } catch(RuntimeException ex){
        AddBasicClass adder = new AddBasicClass();
        adder.add(ex);
      }
    }
    try {
      SootClass soot_class = RootbeerScene.v().getClass(cls);
      JimpleWriter writer = new JimpleWriter();
      writer.write("copied", soot_class);
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }

  private void loadClass(String cls) {
    SootClass soot_class = RootbeerScene.v().getClass(cls);
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      if(method.isConcrete())
        method.getActiveBody();
    }
  }
  
  public static void main(String[] args){
    try {   
      String jar_filename = "../Rootbeer-Test3/dist/Rootbeer-Test3.jar";
      System.out.println("Initializing RootbeerScene");
      RootbeerScene.v().init(jar_filename);

      PrintClass printer = new PrintClass();
      printer.print("java.lang.Object");
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
