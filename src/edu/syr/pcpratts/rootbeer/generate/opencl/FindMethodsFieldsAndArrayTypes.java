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

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.util.HashSet;
import java.util.Set;
import soot.ArrayType;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.CastExpr;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;

public class FindMethodsFieldsAndArrayTypes {

  private static Set<SootMethod> mSootMethodsVisited;

  public static void reset(){
    mSootMethodsVisited = new HashSet<SootMethod>();
  }

  public static void methods(Value value){
    SootMethod soot_method = null;
    if(value instanceof InvokeExpr){
      InvokeExpr invoke_expr = (InvokeExpr) value;
      soot_method = invoke_expr.getMethod();
    } else if(value instanceof NewMultiArrayExpr){
      NewMultiArrayExpr expr = (NewMultiArrayExpr) value;
      ArrayType array_type = expr.getBaseType();
      OpenCLScene.v().addType(array_type);
      OpenCLScene.v().addNewMultiArray(expr);
      return;
    } else if(value instanceof NewExpr){
      NewExpr new_expr = (NewExpr) value;
      RefType ref_type = new_expr.getBaseType();
      OpenCLScene.v().addType(ref_type);
      return;
    } else if(value instanceof NewArrayExpr){
      NewArrayExpr new_expr = (NewArrayExpr) value;
      Type base = new_expr.getType();
      OpenCLScene.v().addType(base);
      return;
    } else if(value instanceof CastExpr){
      CastExpr cast_expr = (CastExpr) value;
      Type type = cast_expr.getCastType();
      if(type instanceof RefType || type instanceof ArrayType)
        OpenCLScene.v().addType(type);
      return;
    } else if(value instanceof FieldRef){
      FieldRef ref = (FieldRef) value;
      Type type = ref.getField().getType();
      if(type instanceof RefType || type instanceof ArrayType)
        OpenCLScene.v().addType(type);
      return;
    } else {
      return;
    }

    SootClass soot_class = soot_method.getDeclaringClass();

    if(mSootMethodsVisited.contains(soot_method))
      return;
    mSootMethodsVisited.add(soot_method);
    
    OpenCLScene.v().addMethod(soot_method);
    OpenCLMethod ocl_method = new OpenCLMethod(soot_method, soot_class);

    ocl_method.findAllUsedMethodsAndFields();
  }

  public static void fields(Value value){

    if(value instanceof FieldRef == false)
      return;
    FieldRef field_ref = (FieldRef) value;
    
    //make sure SootField is what is really in the Scene
    SootField field = field_ref.getField();
    SootClass soot_class = field.getDeclaringClass();
    soot_class = RootbeerScene.v().getClass(soot_class.getName());
    field = soot_class.getField(field.getSubSignature());
    
    OpenCLScene.v().addField(field);
  }

  public static void arrayTypes(Value value) {
    Type type = value.getType();
    if(type instanceof ArrayType == false)
      return;
    OpenCLScene.v().addArrayType(new OpenCLArrayType((ArrayType) type));
  }
}