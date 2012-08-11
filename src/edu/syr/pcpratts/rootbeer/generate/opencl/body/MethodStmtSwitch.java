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

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLMethod;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLType;
import edu.syr.pcpratts.rootbeer.util.Stack;
import java.util.ArrayList;
import java.util.List;
import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;

public class MethodStmtSwitch implements StmtSwitch {
  protected StringBuilder m_Output;
  protected MethodJimpleValueSwitch m_ValueSwitch;
  protected final OpenCLBody m_Parent;
  protected SootMethod m_SootMethod;
  private List<TrapItem> m_TrapItems;
  private int m_VariableNumber;
  private Stack<String> m_OldValueFromMonitorStack;

  public MethodStmtSwitch(OpenCLBody parent, SootMethod soot_method){
    m_SootMethod = soot_method;
    m_Output = new StringBuilder();
    m_ValueSwitch = new MethodJimpleValueSwitch(m_Output);
    m_Parent = parent;
    m_VariableNumber = 1;
    m_OldValueFromMonitorStack = new Stack<String>();
  }
  
  private String getVarName(){
    String ret = "monitor_var"+m_VariableNumber;
    m_VariableNumber++;
    return ret;
  }
  
  public void popMonitor(){
    m_OldValueFromMonitorStack.pop();
  }

  protected boolean methodReturnsAValue(){
    OpenCLMethod ocl_method = new OpenCLMethod(m_SootMethod, m_SootMethod.getDeclaringClass());
    return ocl_method.returnsAValue();
  }

  public void caseBreakpointStmt(BreakpointStmt arg0) {
    //intentionally left blank
  }

  public void caseInvokeStmt(InvokeStmt arg0) {
    arg0.getInvokeExpr().apply(m_ValueSwitch);
    SootMethod method = arg0.getInvokeExpr().getMethod();
    m_Output.append(";\n");
    if(m_ValueSwitch.getCheckException()){
      checkException();
    }
  }

  private void caseDefinitionStmt(DefinitionStmt arg0){
    Value left_op = arg0.getLeftOp();
    Value right_op = arg0.getRightOp();
    Type left_op_type = left_op.getType();
    OpenCLType ocl_left_op_type = new OpenCLType(left_op_type);

    //if the left op is a field ref or array ref we use the setter
    if(left_op instanceof FieldRef || left_op instanceof ArrayRef){
      m_ValueSwitch.setLhs();
      left_op.apply(m_ValueSwitch);
      m_Output.append(", ");
      m_ValueSwitch.setRhs();
      right_op.apply(m_ValueSwitch);
      m_Output.append(", exception);\n");
      if(m_ValueSwitch.getCheckException()){
        checkException();
      }
      m_ValueSwitch.clearLhsRhs();
    }
    //if the right op is a field ref or array ref we use the getter
    else if(right_op instanceof FieldRef || right_op instanceof FieldRef){
      m_ValueSwitch.setLhs();
      left_op.apply(m_ValueSwitch);
      m_Output.append(" = ");
      m_ValueSwitch.setRhs();
      right_op.apply(m_ValueSwitch);
      m_Output.append(";\n");
      if(m_ValueSwitch.getCheckException()){
        checkException();
      }
      m_ValueSwitch.clearLhsRhs();
    }
    //if the left op is a ref type we use gc_assign
    else if(ocl_left_op_type.isRefType()){
      m_ValueSwitch.setLhs();
      m_ValueSwitch.resetNewCalled();
      m_Output.append("edu_syr_pcpratts_gc_assign(gc_info, &");
      arg0.getLeftOp().apply(m_ValueSwitch);
      m_Output.append(", ");
      m_ValueSwitch.setRhs();
      arg0.getRightOp().apply(m_ValueSwitch);
      m_Output.append(");\n");
      if(m_ValueSwitch.getCheckException()){
        checkException();
      }
      m_ValueSwitch.clearLhsRhs();
    }
    //otherwise just use normal assignment
    else {
      m_ValueSwitch.setLhs();
      arg0.getLeftOp().apply(m_ValueSwitch);
      m_Output.append(" = ");
      m_ValueSwitch.setRhs();
      arg0.getRightOp().apply(m_ValueSwitch);
      m_Output.append(";\n");
      if(m_ValueSwitch.getCheckException()){
        checkException();
      }
      m_ValueSwitch.clearLhsRhs();   
    }
  }

  public void caseAssignStmt(AssignStmt arg0) {
    caseDefinitionStmt(arg0);
  }

  public void caseIdentityStmt(IdentityStmt arg0) {
    caseDefinitionStmt(arg0);
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt arg0) {
    String id = getVarName();
    String mem = getVarName();
    String count = getVarName();
    String old = getVarName();
    m_OldValueFromMonitorStack.push(old);
    
    m_Output.append("int "+id+" = getThreadId();\n");
    m_Output.append("char * "+mem+" = edu_syr_pcpratts_gc_deref(gc_info, ");
    arg0.getOp().apply(m_ValueSwitch);
    m_Output.append(");\n");
    m_Output.append(mem+" += 12;\n");
    m_Output.append("int "+count+" = 0;\n");
    m_Output.append("int "+old+";\n");
    m_Output.append("while("+count+" < 100){\n");
    m_Output.append("  "+old+" = atomicCAS((int *) "+mem+", -1, "+id+");\n");
    m_Output.append("  if("+old+" != -1 && "+old+" != "+id+"){\n");
    m_Output.append("    "+count+"++;\n");
    m_Output.append("    if("+count+" >= 99){\n");
    m_Output.append("      "+count+" = 0;\n");
    m_Output.append("    }\n");
    m_Output.append("  } else {\n");
  }

  public void caseExitMonitorStmt(ExitMonitorStmt arg0) {
    m_Output.append("edu_syr_pcpratts_exitMonitorRef(gc_info, ");
    arg0.getOp().apply(m_ValueSwitch);
    m_Output.append(", "+m_OldValueFromMonitorStack.top()+");\n");
  }

  public void caseGotoStmt(GotoStmt arg0) {
    Unit target = arg0.getTarget();
    int label_num = m_Parent.labelNum(target);
    m_Output.append("goto label" + Integer.toString(label_num) + ";\n");
  }

  public void caseIfStmt(IfStmt arg0) {
    m_Output.append("if (");
    arg0.getCondition().apply(m_ValueSwitch);
    m_Output.append(" ) goto label");
    Unit target = arg0.getTarget();
    int label_num = m_Parent.labelNum(target);
    m_Output.append(Integer.toString(label_num)+";\n");
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt arg0) {
    
    m_Output.append("switch(");
    arg0.getKey().apply(m_ValueSwitch);
    m_Output.append("){\n");
    List<Value> values = arg0.getLookupValues();
    List<Unit> units = arg0.getTargets();
    Unit default_target = arg0.getDefaultTarget();
    
    int label_num;
    
    for(int i = 0; i < values.size(); ++i){
      m_Output.append("case ");
      values.get(i).apply(m_ValueSwitch);
      label_num = m_Parent.labelNum(units.get(i));
      m_Output.append(": goto label"+Integer.toString(label_num) + ";\n");
    }
    label_num = m_Parent.labelNum(default_target);
    m_Output.append("default: goto label"+Integer.toString(label_num) + ";\n");
    m_Output.append("}\n"); 
  }

  public void caseNopStmt(NopStmt arg0) {
    //intentionally left blank
  }

  public void caseRetStmt(RetStmt arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void caseReturnStmt(ReturnStmt arg0) {
    if(m_SootMethod.isSynchronized()){
      m_Output.append("edu_syr_pcpratts_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_Output.append("return ");
    arg0.getOp().apply(m_ValueSwitch);
    m_Output.append(";\n");
  }
  
  public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
    if(m_SootMethod.isSynchronized()){ 
      m_Output.append("edu_syr_pcpratts_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_Output.append("return;\n");
  }

  public void caseTableSwitchStmt(TableSwitchStmt arg0) {
    
    m_Output.append("switch(");
    arg0.getKey().apply(m_ValueSwitch);
    m_Output.append("){\n");
    List<Value> values = new ArrayList<Value>();
    for(int i = arg0.getLowIndex(); i < arg0.getHighIndex(); ++i){
      values.add(IntConstant.v(i));  
    }
    
    List<Unit> units = arg0.getTargets();
    Unit default_target = arg0.getDefaultTarget();
    
    int label_num;
    
    for(int i = 0; i < values.size(); ++i){
      m_Output.append("case ");
      values.get(i).apply(m_ValueSwitch);
      label_num = m_Parent.labelNum(units.get(i));
      m_Output.append(": goto label"+Integer.toString(label_num) + ";\n");
    }
    m_Output.append("}\n"); 
    
  }

  public void caseThrowStmt(ThrowStmt arg0) {
    m_Output.append(" *exception = ");
    Value op = arg0.getOp();
    op.apply(m_ValueSwitch);
    m_Output.append(";\n");
    if(m_SootMethod.isSynchronized()){
      m_Output.append("edu_syr_pcpratts_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_Output.append("return");
    if(methodReturnsAValue())
      m_Output.append(" 0;\n");
    else 
      m_Output.append(";\n");
  }

  public void defaultCase(Object arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void append(String string) {
    m_Output.append(string);
  }

  @Override
  public String toString(){
    return m_Output.toString();
  }

  void setTrapItems(List<TrapItem> trap_items) {
    m_TrapItems = trap_items;
  }

  void reset() {
    m_ValueSwitch.reset();
  }
  
  boolean hasCaughtExceptionRef(){
    return m_ValueSwitch.hasCaughtExceptionRef();
  }

  public String getThisRef() {
    return m_ValueSwitch.getThisRef();
  }

  private void checkException() {    
    m_Output.append("if(*exception != 0) { \n");
    if(m_TrapItems != null){    
      m_Output.append("  GC_OBJ_TYPE_TYPE ex_type;\n");
      //if exception is negative, then we didn't allocate memory for it.
      m_Output.append("  if(*exception < 0){\n");
      m_Output.append("    ex_type = *exception;\n");
      m_Output.append("  } else {\n");
      m_Output.append("    char * ex_deref = edu_syr_pcpratts_gc_deref(gc_info, *exception);\n");
      m_Output.append("    ex_type = edu_syr_pcpratts_gc_get_type(ex_deref);\n");
      m_Output.append("  }\n");
      m_Output.append("if(0){}\n");
      for(TrapItem item : m_TrapItems){
        m_Output.append("else if(");
        List<Integer> types = item.getTypeList();
        for(int i = 0; i < types.size(); ++i){
          m_Output.append("ex_type == "+types.get(i));
          if(i < types.size() - 1){
            m_Output.append(" || ");
          }
        }
        m_Output.append("){\n");
        m_Output.append("goto trap"+item.getTrapNum()+";\n");
        m_Output.append("}\n");
      }
    }
    //mOutput.append("edu_syr_pcpratts_fillInStackTrace(gc_info, *exception, \""+class_name+"\", \""+method_name+"\");\n");
    if(m_SootMethod.isSynchronized()){
      m_Output.append("edu_syr_pcpratts_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_Output.append("return ");
    if(methodReturnsAValue())
      m_Output.append("0");
    m_Output.append("; }\n");
  }
}
