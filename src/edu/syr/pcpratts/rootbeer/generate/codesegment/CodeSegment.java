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

package edu.syr.pcpratts.rootbeer.generate.codesegment;

import edu.syr.pcpratts.rootbeer.generate.bytecode.FieldReadWriteInspector;
import java.util.List;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;

public interface CodeSegment {
  public List<Local> getInputArguments();
  public List<Local> getOutputArguments();
  public FieldReadWriteInspector getReadWriteFieldInspector();
  public void findAllUsedMethodsAndFields();
  public void findAllUsedArrayTypes();
  public SootClass getSootClass();
  public List<Value> getInputValues();
  public List<Type> getParameterTypes();
  public void makeCpuBodyForRuntimeBasicBlock(SootClass mClass);
  public SootMethod getRootMethod();
  public SootClass getRootSootClass();
}
