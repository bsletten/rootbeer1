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

package edu.syr.pcpratts.rootbeer.generate.misc;

import edu.syr.pcpratts.rootbeer.generate.opencl.FindMethodsFieldsAndArrayTypes;
import edu.syr.pcpratts.rootbeer.generate.bytecode.FieldReadWriteInspector;
import edu.syr.pcpratts.rootbeer.generate.bytecode.UnitAssembler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

public class BasicBlock {
  Loop mLoop;
  int mStart;
  int mStop;
  List<Value> mOutputArgs;
  private SootClass mRuntimeBasicBlockClass;
  private FieldReadWriteInspector mReadWriteInspector;
  private final DetermineInputOutputParameters mInputOutputParameters;
  private boolean mUsingSharedState;
  private List<Integer> mSharedStateValuesToFilterOut;

  public BasicBlock(Loop loop, int start, int stop) throws Exception {
    mLoop = loop;
    mStart = start;
    mStop = stop;
    mUsingSharedState = false;
    determineOutputArgs();

    mInputOutputParameters = new DetermineInputOutputParameters();
    mInputOutputParameters.run(this);
  }

  public int getStart(){
    return mStart;
  }

  public int getStop(){
    return mStop;
  }
  
  private void determineOutputArgs(){
    mOutputArgs = new ArrayList<Value>();
    List<Unit> whole_body = mLoop.getWholeBodyOfMethod();
    for(int i = mStop + 1; i < whole_body.size(); ++i){
      Unit curr = whole_body.get(i);
      List<ValueBox> boxes = curr.getUseBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        if(value instanceof Local == false)
          continue;
        Local local = (Local) value;
        if(isLocalAssignedTo(local))
          mOutputArgs.add(value);
      }
    }
  }

  /**
   * Returns true if local was assigned to in the basic block
   * @param local
   * @return
   */
  public boolean isLocalAssignedTo(Local local) {
    List<Unit> units = mLoop.getWholeBodyOfMethod();
    for(int i = mStart; i <= mStop; ++i){
      Unit curr = units.get(i);
      List<ValueBox> def_boxes = curr.getDefBoxes();
      for(ValueBox box : def_boxes){
        Value v = box.getValue();
        if(v.equals(local))
          return true;
      }
    }
    return false;
  }

  public List<Value> getOutputValues() {
    return mOutputArgs;
  }

  public Loop getLoop() {
    return mLoop;
  }

  public List<Unit> getUnits() {
    List<Unit> whole_body = mLoop.getWholeBodyOfMethod();
    List<Unit> ret = new ArrayList<Unit>();
    for(int i = mStart; i <= mStop; ++i){
      ret.add(whole_body.get(i));
    }
    return ret;
  }

  public void findAllUsedMethodsAndFields(){
    PatchingChain<Unit> units = getBody().getUnits();
    Iterator<Unit> iter = units.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      List<ValueBox> boxes = next.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        FindMethodsFieldsAndArrayTypes.methods(value);
        FindMethodsFieldsAndArrayTypes.fields(value);
      }
    }
  }

  public Body getBody(){
    List<Unit> units = new ArrayList<Unit>();
    Local base = Jimple.v().newLocal("base", mRuntimeBasicBlockClass.getType());
    SootMethod runOnCpu = mRuntimeBasicBlockClass.getMethod("run", new ArrayList(), VoidType.v());
    InvokeExpr expr = Jimple.v().newVirtualInvokeExpr(base, runOnCpu.makeRef());
    Unit u = Jimple.v().newInvokeStmt(expr);
    units.add(u);

    UnitAssembler assembler = new UnitAssembler();
    assembler.addAll(units);

    JimpleBody body = Jimple.v().newBody();
    assembler.assemble(body);
    return body;
  }

  public SootClass getSootClass(){
    return mRuntimeBasicBlockClass;
  }

  public void addClass(SootClass soot_class) {
    mRuntimeBasicBlockClass = soot_class;    
    mReadWriteInspector = new FieldReadWriteInspector(mRuntimeBasicBlockClass);
  }

  public void findAllUsedArrayTypes() {
    PatchingChain<Unit> units = getBody().getUnits();
    Iterator<Unit> iter = units.iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      List<ValueBox> boxes = next.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        FindMethodsFieldsAndArrayTypes.arrayTypes(value);
      }
    }
  }

  public FieldReadWriteInspector getReadWriteFieldInspector() {
    return mReadWriteInspector;
  }

  public List<Value> getInputValues() {
    List<Value> ret = mInputOutputParameters.getInputValues();
    if(mUsingSharedState){
      List<Value> ret2 = new ArrayList<Value>();
      for(int i = 0; i < ret.size(); ++i){
        if(mSharedStateValuesToFilterOut.contains(i) == false)
          ret2.add(ret.get(i));
      }
      return ret2;
    }
    return ret;
  }

  public List getParameterTypes() {
    List ret = mInputOutputParameters.getParameterTypes();
    if(mUsingSharedState){
      List ret2 = new ArrayList();
      for(int i = 0; i < ret.size(); ++i){
        if(mSharedStateValuesToFilterOut.contains(i) == false)
          ret2.add(ret.get(i));
      }
      return ret2;
    }
    return ret;
  }

  public List<Local> getInputArguments() {
    List<Local> ret = mInputOutputParameters.getInputArguments();
    if(mUsingSharedState){
      List<Local> ret2 = new ArrayList<Local>();
      for(int i = 0; i < ret.size(); ++i){
        if(mSharedStateValuesToFilterOut.contains(i) == false)
          ret2.add(ret.get(i));
      }
      return ret2;
    }
    return ret;
  }

  public List<Local> getOutputArguments() {
    return mInputOutputParameters.getOutputArguments();
  }
}
