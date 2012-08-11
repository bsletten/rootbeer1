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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

public class FindMethodCalls {
    
  public FindMethodCalls(){ 
  }
  
  public Set<SootMethod> findForBody(Body body) {
    Set<SootMethod> methods = new LinkedHashSet<SootMethod>();    
    PatchingChain<Unit> chain = body.getUnits();
    Iterator<Unit> iter = chain.iterator();
    while(iter.hasNext()){
      Unit unit = iter.next();
      List<ValueBox> vboxes = unit.getUseAndDefBoxes();
      for(ValueBox vbox : vboxes){
        Value value = vbox.getValue();
        if(value instanceof InvokeExpr == false)
          continue;
        InvokeExpr expr = (InvokeExpr) value;
        SootMethod method = expr.getMethod();
        if(methods.contains(method) == false)
          methods.add(method);
      }
    }
    return methods;
  }
  
  public Set<SootMethod> findForMethod(SootMethod method){
    if(method.isConcrete() == false){
      return new HashSet<SootMethod>();
    }
    Body body = RootbeerScene.v().getBody(method);
    return findForBody(body);
  }
}
