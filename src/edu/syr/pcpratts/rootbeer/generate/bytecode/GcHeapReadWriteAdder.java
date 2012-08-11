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

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.TypeHistory;
import edu.syr.pcpratts.rootbeer.generate.codesegment.CodeSegment;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.Body;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;

public class GcHeapReadWriteAdder {

  private Set<SootMethod> mMethodsInspected;
  private TypeHistory mTypeHistory;
  private String gcObjectVisitorClassName;

  public GcHeapReadWriteAdder(){
  }

  public void add(CodeSegment block){
    mTypeHistory = OpenCLScene.v().getTypeHistory();;
    SootClass block_class = block.getRootSootClass();
    mMethodsInspected = new HashSet<SootMethod>();

    SootMethod root_method = block.getRootMethod();
    addToMethod(root_method);

    VisitorGen generate_visitor = new VisitorGen(block.getReadWriteFieldInspector(), block_class);
    generate_visitor.generate();
    gcObjectVisitorClassName = generate_visitor.getClassName();
  }

  private void addToMethod(SootMethod method) {
    if(mMethodsInspected.contains(method))
      return;
    mMethodsInspected.add(method);
    
    SootClass soot_class = method.getDeclaringClass();
    soot_class = RootbeerScene.v().getClass(soot_class.getName());
    
    while(true){
      mTypeHistory.addType(soot_class.getType());
      if(soot_class.hasSuperclass() == false)
        break;
      soot_class = soot_class.getSuperclass();
      soot_class = RootbeerScene.v().getClass(soot_class.getName());
      if(soot_class.getName().equals("java.lang.Object"))
        break;
    }

    Body body;
    try {
      body = method.getActiveBody();
    } catch(RuntimeException ex){
      //no body for method...
      return;
    }

    List<ValueBox> boxes = body.getUseAndDefBoxes();
    //foreach statement reachable in the block
    for(ValueBox box : boxes){
      Value v = box.getValue();
      //if it accesses a class in some way
      if(v instanceof InvokeExpr){
        InvokeExpr invoke_expr = (InvokeExpr) v;
        //make if adhere to the GcObject interface if it allready doesn't
        addToMethod(invoke_expr.getMethod());
      } else if(v instanceof FieldRef){
        FieldRef field_ref = (FieldRef) v;
        Type type = field_ref.getField().getType();
        if(type instanceof RefType){
          mTypeHistory.addType(type);
        } else if(type instanceof ArrayType){
          mTypeHistory.addType(type);
        }
      }
    }
  }

  String getGcObjectVisitorClassName() {
    return gcObjectVisitorClassName;
  }
}
