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

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLArrayType;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLMethod;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLType;
import edu.syr.pcpratts.rootbeer.generate.opencl.UnitsSuitableForGpu;
import java.util.List;
import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EqExpr;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.ThisRef;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;

public class MethodJimpleValueSwitch implements JimpleValueSwitch {
  protected final StringBuilder mOutput;
  private boolean mLhs;
  private boolean mRhs;
  private boolean mNewCalled;
  private boolean mCaughtExceptionRef;
  private String m_ThisRef;
  private String m_PreviousLocal;
  private boolean m_CheckException;

  public MethodJimpleValueSwitch(StringBuilder output) {
    mOutput = output;
    mNewCalled = false;
    clearLhsRhs();
  }
  
  public boolean newHasBeenCalled(){
    return mNewCalled;
  }

  public void resetNewCalled(){
    mNewCalled = false;
  }
  
  void setLhs(){
    mLhs = true;
    mRhs = false;
  }

  void setRhs(){
    mRhs = true;
    mLhs = false;
  }

  void clearLhsRhs(){
    mLhs = false;
    mRhs = false;
  }

  boolean isLhs(){
    if(mLhs == false && mRhs == false)
      throw new IllegalStateException("Lhs/Rhs in invalid state");
    return mLhs;
  }

  private void writeBinOpExpr(BinopExpr arg0){
    String symbol = arg0.getSymbol().trim();
    if(needDoubleMod(arg0, symbol)){
      mOutput.append("edu_syr_pcpratts_modulus(");      
      arg0.getOp1().apply(this);
      mOutput.append(", ");
      arg0.getOp2().apply(this);
      mOutput.append(")");
    } else if(symbol.equals("cmp")){
      mOutput.append("edu_syr_pcpratts_cmp(");      
      arg0.getOp1().apply(this);
      mOutput.append(", ");
      arg0.getOp2().apply(this);
      mOutput.append(")");
    } else if(symbol.equals("cmpl")){    
      mOutput.append("edu_syr_pcpratts_cmpl((double)");      
      arg0.getOp1().apply(this);
      mOutput.append(", (double)");
      arg0.getOp2().apply(this);
      mOutput.append(")");
    } else if(symbol.equals("cmpg")){
      mOutput.append("edu_syr_pcpratts_cmpg((double)");      
      arg0.getOp1().apply(this);
      mOutput.append(", (double)");
      arg0.getOp2().apply(this);
      mOutput.append(")");    
    } else {
      arg0.getOp1().apply(this);
      mOutput.append(" "+symbol+" ");
      arg0.getOp2().apply(this);
      mOutput.append(" ");
    }
  }
   
  private boolean needDoubleMod(BinopExpr arg0, String symbol) {
    if(symbol.equals("%") == false)
      return false;
    if(!arg0.getOp1().getType().equals(DoubleType.v()) && !arg0.getOp1().getType().equals(FloatType.v()))
      return false;
    if(!arg0.getOp2().getType().equals(DoubleType.v()) && !arg0.getOp2().getType().equals(FloatType.v()))
      return false;
    return true;
  }

  public void caseAddExpr(AddExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseAndExpr(AndExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseCmpExpr(CmpExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseCmpgExpr(CmpgExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseCmplExpr(CmplExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseDivExpr(DivExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseEqExpr(EqExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseNeExpr(NeExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseGeExpr(GeExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseGtExpr(GtExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseLeExpr(LeExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseLtExpr(LtExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseMulExpr(MulExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseOrExpr(OrExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseRemExpr(RemExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseShlExpr(ShlExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseShrExpr(ShrExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseUshrExpr(UshrExpr arg0) {
    mOutput.append("(");
    arg0.getOp1().apply(this);
    mOutput.append(" >> ");
    arg0.getOp2().apply(this);
    mOutput.append(" ) & ");

    OpenCLType lhs_ocl_type = new OpenCLType(arg0.getOp1().getType());
    OpenCLType rhs_ocl_type = new OpenCLType(arg0.getOp2().getType());
    int max_size = lhs_ocl_type.getSize();
    if(rhs_ocl_type.getSize() > max_size){
      max_size = rhs_ocl_type.getSize();
    }

    String mask = "";
    switch(max_size){
      case 1:
        mask = "0x7f";
        break;
      case 2:
        mask = "0x7fff";
        break;
      case 4:
        mask = "0x7fffffff";
        break;
      case 8:
        mask = "0x7fffffffffffffffL";
        break;
    }
    mOutput.append(mask);
  }

  public void caseSubExpr(SubExpr arg0) {
    writeBinOpExpr(arg0);
  }

  public void caseXorExpr(XorExpr arg0) {
    writeBinOpExpr(arg0);
  }

  private void caseInstanceInvokeExpr(InstanceInvokeExpr arg0){
    SootMethod soot_method = arg0.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    OpenCLMethod ocl_method = new OpenCLMethod(soot_method, soot_class);
    
    mOutput.append(ocl_method.getInstanceInvokeString(arg0));
    setCheckException();
  }

  public void caseInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
    caseInstanceInvokeExpr(arg0);
    setCheckException();
  }

  public void caseSpecialInvokeExpr(SpecialInvokeExpr arg0) {
    SootMethod soot_method = arg0.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    if(soot_class.getName().equals("java.lang.Object"))
      return;
    OpenCLMethod ocl_method = new OpenCLMethod(soot_method, soot_class);
    mOutput.append(ocl_method.getInstanceInvokeString(arg0));
    setCheckException();
  }

  public void caseStaticInvokeExpr(StaticInvokeExpr arg0) {    
    SootMethod soot_method = arg0.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    if(soot_class.getName().equals("java.lang.Object"))
      return;
    OpenCLMethod ocl_method = new OpenCLMethod(soot_method, soot_class);
    mOutput.append(ocl_method.getStaticInvokeString(arg0));
    setCheckException();
  }

  public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
    caseInstanceInvokeExpr(arg0);
  }

  public void caseCastExpr(CastExpr arg0) {
    Type cast_type = arg0.getCastType();
    OpenCLType ocl_type = new OpenCLType(cast_type);
    mOutput.append("("+ocl_type.getRefString()+") ");
    Value rhs = arg0.getOp();
    rhs.apply(this);
  }

  public void caseInstanceOfExpr(InstanceOfExpr arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void caseNewArrayExpr(NewArrayExpr arg0) {
    OpenCLScene.v().setUsingGarbageCollector();
    OpenCLArrayType array_type = new OpenCLArrayType((ArrayType) arg0.getType());
    mOutput.append(array_type.invokeNewArrayExpr(arg0));
    mNewCalled = true;
  }

  public void caseNewMultiArrayExpr(NewMultiArrayExpr arg0) {
    OpenCLScene.v().setUsingGarbageCollector();
    OpenCLArrayType array_type = new OpenCLArrayType((ArrayType) arg0.getType());
    mOutput.append(array_type.invokeNewMultiArrayExpr(arg0));  
    mNewCalled = true;  
  }

  public void caseNewExpr(NewExpr arg0) {
    OpenCLScene.v().setUsingGarbageCollector();
    mOutput.append(" -1 ");
  }

  public void caseLengthExpr(LengthExpr arg0) {
    Value op = arg0.getOp();
    mOutput.append("edu_syr_pcpratts_array_length(gc_info, ");
    op.apply(this);
    mOutput.append(")");
  }

  public void caseNegExpr(NegExpr arg0) {
    Value op = arg0.getOp();
    mOutput.append("! ");
    op.apply(this);
  }

  public void defaultCase(Object arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void caseLocal(Local arg0) {
    mOutput.append(" "+arg0.getName()+" ");
    m_PreviousLocal = arg0.getName();
  }

  public void caseDoubleConstant(DoubleConstant arg0) {
    mOutput.append(" "+replaceNumber(arg0.toString())+" ");
  }

  public void caseFloatConstant(FloatConstant arg0) {
    mOutput.append(" "+replaceNumber(arg0.toString())+" ");
  }

  public void caseIntConstant(IntConstant arg0) {
    mOutput.append(" "+replaceNumber(arg0.toString())+" ");
  }

  public void caseLongConstant(LongConstant arg0) {
    mOutput.append(" "+replaceNumber(arg0.toString())+" ");
  }

  public void caseNullConstant(NullConstant arg0) {
    mOutput.append(" -1 ");
  }

  private String replaceNumber(String number){
    if(number.equals("#Infinity"))  
      return "INFINITY";
    if(number.equals("#-Infinity"))
      return "-INFINITY";
    if(number.equals("#NaN"))
      return "NAN";
    return number;
  }
  
  public void caseStringConstant(StringConstant arg0) {
    mOutput.append(" edu_syr_pcpratts_string_constant(gc_info, (char *) "+arg0.toString()+", exception) ");
  }

  public void caseClassConstant(ClassConstant arg0) {
    mOutput.append("$$CLASS_CONSTANT$$");
  }

  public void caseArrayRef(ArrayRef arg0) {
    OpenCLArrayType array = new OpenCLArrayType((ArrayType) arg0.getBase().getType());
    if(isLhs()){
      mOutput.append(array.getArrayRefSetter(arg0));
      setCheckException();
    } else {
      mOutput.append(array.getArrayRefGetter(arg0));
      setCheckException();
    }
  }

  public void caseStaticFieldRef(StaticFieldRef arg0) {
    SootField field = arg0.getField();
    OpenCLField ocl_field = new OpenCLField(arg0.getField(), field.getDeclaringClass());
    if(isLhs()){
      mOutput.append(ocl_field.getStaticSetterInvoke());
    } else {
      mOutput.append(ocl_field.getStaticGetterInvoke());
    }
  }

  public void caseInstanceFieldRef(InstanceFieldRef arg0) {
    Value base = arg0.getBase();
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("How do I handle base is not a local?");
    Local local = (Local) base;
    Type type = local.getType();
    if(type instanceof RefType == false)
      throw new UnsupportedOperationException("How do I handle type is not a ref type?");
    RefType ref = (RefType) type;
    OpenCLField ocl_field = new OpenCLField(arg0.getField(), ref.getSootClass());
    if(isLhs()){
      mOutput.append(ocl_field.getInstanceSetterInvoke(arg0.getBase()));
    } else {
      mOutput.append(ocl_field.getInstanceGetterInvoke(arg0.getBase()));
    }
    setCheckException();
  }

  public void caseParameterRef(ParameterRef arg0) {
    mOutput.append(" parameter"+Integer.toString(arg0.getIndex())+" ");
  }

  public void caseCaughtExceptionRef(CaughtExceptionRef arg0) {
    mOutput.append(" *exception ");
    mCaughtExceptionRef = true;
  }

  public void caseThisRef(ThisRef arg0) {
    mOutput.append(" thisref ");
    m_ThisRef = m_PreviousLocal;
  }

  public void caseDynamicInvokeExpr(DynamicInvokeExpr die) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  void reset() {
    mCaughtExceptionRef = false;
    m_CheckException = false;
  }

  boolean hasCaughtExceptionRef() {
    return mCaughtExceptionRef;
  }

  public String getThisRef() {
    return m_ThisRef;
  }

  private void setCheckException() {
    m_CheckException = true;
  }
  
  public boolean getCheckException(){
    return m_CheckException;
  }
}
