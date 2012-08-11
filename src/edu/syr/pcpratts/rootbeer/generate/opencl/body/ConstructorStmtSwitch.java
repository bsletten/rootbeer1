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

package edu.syr.pcpratts.rootbeer.generate.opencl.body;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLMethod;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;

public class ConstructorStmtSwitch extends MethodStmtSwitch {

  private SootClass m_SootClass;
  private boolean m_EmitRetVoid;
  public ConstructorStmtSwitch(OpenCLBody parent, SootMethod soot_method,
    boolean emit_ret_void){
  
    super(parent, soot_method);
    
    m_EmitRetVoid = emit_ret_void;    
    m_SootClass = soot_method.getDeclaringClass();
    m_SootClass = RootbeerScene.v().getClass(m_SootClass.getName());
  }

  @Override
  protected boolean methodReturnsAValue(){
    if(m_EmitRetVoid){
      return false;
    } else {
      return super.methodReturnsAValue();
    }
  }
  
  @Override
  public void caseReturnStmt(ReturnStmt arg0) {
    //intentionally left blank
  }

  @Override
  public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
    if(m_EmitRetVoid)
      m_Output.append("return;\n");
  }
  
  @Override
  public void caseInvokeStmt(InvokeStmt arg0) {
    InvokeExpr expr = arg0.getInvokeExpr();
    if(expr instanceof SpecialInvokeExpr == false){
      super.caseInvokeStmt(arg0);
      return;
    }
    SpecialInvokeExpr sexpr = (SpecialInvokeExpr) expr;
    if(needsReWriting(sexpr) == false){
      super.caseInvokeStmt(arg0);
      return;
    }
    SootMethod soot_method = sexpr.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    if(soot_class.getName().equals("java.lang.Object"))
      return;
    OpenCLMethod ocl_method = new OpenCLMethod(soot_method, soot_class);
    m_Output.append(ocl_method.getConstructorBodyInvokeString(sexpr));
    m_Output.append(";\n");
  }

  private boolean needsReWriting(SpecialInvokeExpr sexpr) {
    if(m_SootClass.hasSuperclass() == false)
      return false;
    SootMethod method = sexpr.getMethod();
    SootClass soot_class = method.getDeclaringClass();
    String parent_name = m_SootClass.getSuperclass().getName();
    if(soot_class.getName().equals(parent_name) == false)
      return false;
    if(method.getName().equals("<init>") == false)
      return false;
    return true;
  }
}
