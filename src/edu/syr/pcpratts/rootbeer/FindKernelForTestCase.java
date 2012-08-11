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

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.util.List;
import soot.*;
import soot.jimple.InvokeExpr;

public class FindKernelForTestCase {

  private List<String> m_kernels;
  
  public String get(String test_case, List<String> kernels){
    m_kernels = kernels;
    SootClass test_class = RootbeerScene.v().getClass(test_case);
    List<SootMethod> methods = test_class.getMethods();
    for(SootMethod method : methods){
      String kernel = searchMethod(method);
      if(kernel != null){
        return kernel;
      }
    }
    throw new RuntimeException("cannot find kernel for test case: "+test_case);
  }

  private String searchMethod(SootMethod method) {
    Body body = RootbeerScene.v().getBody(method);
    List<ValueBox> boxes = body.getUseAndDefBoxes();
    for(ValueBox box : boxes){
      Value value = box.getValue();
      if(value instanceof InvokeExpr){
        InvokeExpr expr = (InvokeExpr) value;
        SootClass to_call = expr.getMethodRef().declaringClass();
        if(m_kernels.contains(to_call.getName())){
          return to_call.getName();
        }
      }
    }
    return null;
  }
}
