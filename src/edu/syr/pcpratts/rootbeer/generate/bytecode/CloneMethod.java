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

import java.util.Iterator;
import soot.Local;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.util.Chain;

public class CloneMethod {

  public SootMethod clone(SootMethod method, String new_name){
    SootMethod ret = new SootMethod(new_name, method.getParameterTypes(), method.getReturnType());

    //clone body
    JimpleBody body = Jimple.v().newBody(ret);
    UnitAssembler assembler = new UnitAssembler();
    PatchingChain<Unit> unit_chain = method.getActiveBody().getUnits();
    Iterator<Unit> iter = unit_chain.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      assembler.add(next);
    }
    assembler.assemble(body);
    ret.setActiveBody(body);
    return ret;
  }
}
