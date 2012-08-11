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

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.GenerateRuntimeBasicBlock;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import soot.SootClass;
import soot.SootMethod;

public class Transform2 {
  
  private int m_Uuid;
  
  public Transform2(){
    m_Uuid = 1;
  }

  public void run(String cls){     
    OpenCLScene.v();
    
    System.out.println("Running Transform2 on: "+cls);
    
    SootClass soot_class1 = RootbeerScene.v().getClass(cls);
    SootMethod method = soot_class1.getMethod("void gpuMethod()");
    
    //generate RuntimeBasicBlock and GcObjectVisitor
    String uuid = getUuid();
    GenerateRuntimeBasicBlock generate = new GenerateRuntimeBasicBlock(method, uuid);
    try {
      generate.makeClass();
    } catch(Exception ex){
      ex.printStackTrace();
    }

    //add an interface to the class
    SootClass soot_class = method.getDeclaringClass();
    SootClass iface_class = RootbeerScene.v().getClass("edu.syr.pcpratts.rootbeer.runtime.CompiledKernel");
    soot_class.addInterface(iface_class);
    
    OpenCLScene.releaseV();
  }
  
  private String getUuid(){
    int uuid = m_Uuid;
    m_Uuid++;
    return Integer.toString(uuid);
  }
}
