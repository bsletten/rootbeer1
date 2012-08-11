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

package edu.syr.pcpratts.rootbeer.generate.opencl;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.StaticInvokeExpr;

public abstract class UnitsSuitableForGpu {
  public abstract boolean isSuitable(Unit u);

  public boolean isStaticMathMethod(StaticInvokeExpr sinvoke_expr){
    SootMethod soot_method = sinvoke_expr.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    if(soot_class.getName().equals("java.lang.Math") == false)
      return false;
    return true;
  }
}
