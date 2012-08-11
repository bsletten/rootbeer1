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

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLMethod;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.FieldReadWriteInspector;
import java.util.ArrayList;
import java.util.List;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.internal.JimpleLocal;

public class MethodCodeSegment implements CodeSegment {
  private SootMethod existingMethod;
  private FieldReadWriteInspector fieldReadWriteInspector;
  private SootMethod clonedMethod;
  private SootClass mClonedClass;

  public MethodCodeSegment(SootMethod method){
    this.existingMethod = method;
    fieldReadWriteInspector = new FieldReadWriteInspector(method);
  }

  public List<Local> getInputArguments() {
    List<Local> ret = new ArrayList<Local>();
    Type t = existingMethod.getDeclaringClass().getType();
    Local l = new JimpleLocal("r0", t);
    ret.add(l);
    return ret;
  }

  public List<Local> getOutputArguments() {
    return new ArrayList<Local>();
  }

  public FieldReadWriteInspector getReadWriteFieldInspector() {
    return fieldReadWriteInspector;
  }

  public void findAllUsedMethodsAndFields() {
    OpenCLScene.v().addMethod(existingMethod);
    
    OpenCLMethod ocl_method = new OpenCLMethod(existingMethod, existingMethod.getDeclaringClass());
    ocl_method.findAllUsedMethodsAndFields();
  }

  public void findAllUsedArrayTypes() {
    OpenCLMethod ocl_method = new OpenCLMethod(existingMethod, existingMethod.getDeclaringClass());
    ocl_method.findAllUsedArrayTypes();
  }

  public SootClass getSootClass() {
    return existingMethod.getDeclaringClass();
  }

  public List<Value> getInputValues() {
    return new ArrayList<Value>();
  }

  public List<Type> getParameterTypes() {
    List<Type> ret = new ArrayList<Type>();
    Type t = existingMethod.getDeclaringClass().getType();
    ret.add(t);
    return ret;
  }

  public void makeCpuBodyForRuntimeBasicBlock(SootClass mClass) {
    mClonedClass = mClass;
  }

  public SootMethod getRootMethod() {
    return existingMethod;
  }

  public SootClass getRootSootClass() {
    return existingMethod.getDeclaringClass();
  }

}
