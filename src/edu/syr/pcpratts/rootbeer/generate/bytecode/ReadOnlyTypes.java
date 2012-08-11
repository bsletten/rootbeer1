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

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.compiler.FindMethodCalls;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import soot.Body;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;

public class ReadOnlyTypes {

  private SootClass m_RootClass;
  private Set<String> m_WrittenClasses;
  private Set<String> m_Inspected;
  
  public ReadOnlyTypes(SootMethod gpuMethod) {
    m_RootClass = gpuMethod.getDeclaringClass();
    m_WrittenClasses = new HashSet<String>();
    m_Inspected = new HashSet<String>();
    inspectMethod(gpuMethod);
  }

  public boolean isRootReadOnly(){
    return isReadOnly(m_RootClass);
  }
  
  public boolean isReadOnly(SootClass soot_class){
    String name = soot_class.getName();
    if(m_WrittenClasses.contains(name))
      return false;
    return true;
  }

  private void inspectMethod(SootMethod method) {
    String sig = method.getSignature();
    if(m_Inspected.contains(sig))
      return;
    m_Inspected.add(sig);
    
    Body body = RootbeerScene.v().getBody(method);
    if(body == null)
      return;
    inspectBody(body);
    
    FindMethodCalls finder = new FindMethodCalls();
    Set<SootMethod> calls = finder.findForMethod(method);
    Iterator<SootMethod> iter = calls.iterator();
    while(iter.hasNext()){
      SootMethod curr = iter.next();
      inspectMethod(curr);
    }
  }

  private void inspectBody(Body body) {
    Iterator<Unit> iter = body.getUnits().iterator();
    while(iter.hasNext()){
      Unit curr = iter.next();
      if(curr instanceof AssignStmt == false)
        continue;
      
      AssignStmt assign = (AssignStmt) curr;
      Value lhs = assign.getLeftOp();
      
      if(lhs instanceof FieldRef == false)
        continue;
        
      FieldRef ref = (FieldRef) lhs;
      SootField field = ref.getField();
      String name = field.getDeclaringClass().getName();
      if(m_WrittenClasses.contains(name) == false)
        m_WrittenClasses.add(name);
    }
  }
}
