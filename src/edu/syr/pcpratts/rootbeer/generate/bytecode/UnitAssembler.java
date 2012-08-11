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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.util.Chain;

public class UnitAssembler {

  List<Unit> mOutputUnits;
  List<Local> mOutputLocals;
  Map<String, Local> mLocalMap;
  Map<String, List<UnitBox>> mLabelToUnitBoxMap;

  List<Unit> mInputUnits;
  List<List<String>> mLabels;
  Jimple mJimple;

  public UnitAssembler(){
    mOutputUnits = new ArrayList<Unit>();
    mOutputLocals = new ArrayList<Local>();
    mInputUnits = new ArrayList<Unit>();
    mLabels = new ArrayList<List<String>>();
    mLocalMap = new HashMap<String, Local>();
    mLabelToUnitBoxMap = new HashMap<String, List<UnitBox>>();
    mJimple = Jimple.v();
  }

  public void add(Unit u){
    mInputUnits.add(u);
  }

  public void addAll(Collection<Unit> units){
    for(Unit u : units){
      mInputUnits.add(u);
    }
  }

  void copyLocals(){
    for(Unit u : mOutputUnits){
      List<ValueBox> boxes = u.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value v = box.getValue();
        if(v instanceof Local == false)
          continue;
        Local local = (Local) v.clone();
        if(mLocalMap.containsKey(local.toString()) == false){
          mLocalMap.put(local.toString(), local);
          mOutputLocals.add(local);
        }
        local = mLocalMap.get(local.toString());
        box.setValue(local);
      }
    }
  }

  UnitBox getTarget(Unit input){
    if(input instanceof IfStmt){
      IfStmt if_stmt = (IfStmt) input;
      return if_stmt.getTargetBox();
    } else if(input instanceof GotoStmt){
      GotoStmt goto_stmt = (GotoStmt) input;
      return goto_stmt.getTargetBox();
    }
    return null;
  }

  void copyTargets(){
    for(int i = 0; i < mInputUnits.size(); ++i){
      Unit input = mInputUnits.get(i);
      Unit output = mOutputUnits.get(i);
      List<UnitBox> input_boxes = input.getUnitBoxes();
      List<UnitBox> output_boxes = output.getUnitBoxes();
      for(int j = 0; j < input_boxes.size(); ++j){
        UnitBox input_box = input_boxes.get(j);
        UnitBox output_box = output_boxes.get(j);

        Unit input_target = input_box.getUnit();
        //using the addIf method makes targets null
        if(input_target == null)
          continue;
        
        int target_i = findTarget(input_target);
        output_box.setUnit(mOutputUnits.get(target_i));
      }
    }
  }

  public Unit unitClone(Unit input){
    Unit output = (Unit) input.clone();
    List<UnitBox> input_boxes = input.getUnitBoxes();
    List<UnitBox> output_boxes = output.getUnitBoxes();
    for(int i = 0; i < input_boxes.size(); ++i){
      UnitBox input_box = input_boxes.get(i);
      UnitBox output_box = output_boxes.get(i);
      try {
        int j = findTarget(input_box.getUnit());
        output_box.setUnit(mInputUnits.get(j));
      } catch(Exception ex){
        ex.printStackTrace();
        continue;
      }
    }
    return output;
  }

  private boolean unitEquals(Unit lhs, Unit rhs){
    if(lhs.equals(rhs))
      return true;
    if(lhs instanceof GotoStmt && rhs instanceof GotoStmt){
      GotoStmt lhs_goto = (GotoStmt) lhs;
      GotoStmt rhs_goto = (GotoStmt) rhs;
      if(lhs_goto.getTarget().equals(rhs_goto.getTarget()))
        return true;
    }
    return false;
  }

  int findTarget(Unit target){
    for(int i = 0; i < mInputUnits.size(); ++i){
      Unit curr = mInputUnits.get(i);
      if(unitEquals(target, curr))
        return i;
    }
    throw new RuntimeException("Cannot find target while assembling units: " + target.toString());
  }

  public void assemble(Body body){
    assignLabels();
    cloneUnits();
    //debugPrintOutput();
    copyTargets();
    checkTargetBoxes();
    copyLocals();
    writeToBody(body);
  }

  void checkTargetBoxes(){
    Set<String> key_set = mLabelToUnitBoxMap.keySet();
    for(String key : key_set){
      List<UnitBox> boxes = mLabelToUnitBoxMap.get(key);
      for(UnitBox box : boxes){
        if(box.getUnit() == null)
          throw new RuntimeException("box unit is null!");
      }
    }
  }

  void cloneUnits(){
    for(Unit u : mInputUnits){
      mOutputUnits.add((Unit) u.clone());
    }
  }

  void debugPrintOutput(){
    for(Unit u : mOutputUnits){
      System.out.println(u.toString());
    }
    System.out.println("End of debugPrintOutput");
  }

  private void writeToBody(Body body) {
    PatchingChain<Unit> units = body.getUnits();
    Chain<Local> locals = body.getLocals();
    units.clear();
    locals.clear();

    for(Unit u : mOutputUnits){
      units.add(u);
    }

    for(Local l : mOutputLocals){
      locals.add(l);
    }
  }

  @Override
  public String toString(){
    String ret = "";
    for(int i = 0; i < mOutputUnits.size(); ++i){
      if(i < mLabels.size()){
        List<String> labels = mLabels.get(i);
        for(String label : labels)
          ret += label+":\n";
      }
      ret += mOutputUnits.get(i).toString() + "\n";
    }
    return ret;
  }

  private void addLabelToUnitBox(String label, UnitBox unit_box){
    List<UnitBox> boxes;
    if(mLabelToUnitBoxMap.containsKey(label))
      boxes = mLabelToUnitBoxMap.get(label);
    else
      boxes = new ArrayList<UnitBox>();
    boxes.add(unit_box);
    mLabelToUnitBoxMap.put(label, boxes);
  }

  public void addIf(Value condition, String target_label) {
    UnitBox target = mJimple.newStmtBox(null);
    addLabelToUnitBox(target_label, target);
    Unit u = mJimple.newIfStmt(condition, target);
    add(u);
  }

  public void addGoto(String target_label){
    UnitBox target = mJimple.newStmtBox(null);
    addLabelToUnitBox(target_label, target);
    Unit u = mJimple.newGotoStmt(target);
    add(u);
  }

  public void addLabel(String label){
    if(label.equals("phillabel2")){
      label = "phillabel2";
    }
    while(mInputUnits.size() >= mLabels.size())
      mLabels.add(new ArrayList<String>());

    mLabels.get(mLabels.size()-1).add(label);
  }

  public Unit getUnitByLabel(String label) {
    for(int i = 0; i < mLabels.size(); ++i){
      List<String> labelset = mLabels.get(i);
      if(labelset.contains(label))
        return mInputUnits.get(i);
    }
    throw new RuntimeException("Cannot find unit");
  }

  private void assignLabels() {
    for(int i = 0; i < mLabels.size(); ++i){
      List<String> labelset = mLabels.get(i);
      if(labelset.size() == 0)
        continue;

      Unit target = mInputUnits.get(i);
      for(String label : labelset){
        List<UnitBox> boxes = mLabelToUnitBoxMap.get(label);
        if(boxes == null){
          System.out.println("Cannot find boxes for label.  This could be caused by classes other than the BytecodeLanguage using the assembler and is not a fatal error.");
          continue;
        }
        for(UnitBox box : boxes){
          box.setUnit(target);
        }
      }
    }
  }

  Unit getLastUnitCreated() {
    return mInputUnits.get(mInputUnits.size()-1);
  }
}
