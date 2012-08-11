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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import soot.*;
import soot.util.Chain;

public class FindGpuMethodTransform extends BodyTransformer {

  private List<String> m_classes;
  
  public FindGpuMethodTransform(){
    m_classes = new ArrayList<String>();
  }
  
  @Override
  protected void internalTransform(Body body, String string, Map map) {
    SootMethod method = body.getMethod();
    SootClass soot_class = method.getDeclaringClass();
    SootClass kernel_class = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.Kernel");
    Chain<SootClass> ifaces = soot_class.getInterfaces();
    if(ifaces.contains(kernel_class)){
      m_classes.add(soot_class.getName());
    }
  }
  
  public List<String> getKernelClasses(){
    return m_classes;
  }
}
