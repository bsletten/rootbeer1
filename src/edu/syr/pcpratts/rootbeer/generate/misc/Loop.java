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

import java.util.Collection;
import java.util.List;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

public interface Loop {
  public static final int DEP_STATUS_NO_DEP = 1;
  public static final int DEP_STATUS_DEP = 2;
  public static final int DEP_STATUS_UNKNOWN = 3;
  public static final int DEP_STATUS_PARTIAL_DEP = 3;


  public List<Unit> getWholeBodyOfMethod();
  public Unit getUnitAfterWholeBodyOfMethod();

  public int getInnerStart();

  public int getInnerStop();

  public int getIfStmtI();

  public Unit getCode(int ifStmtI);

  public int getGotoI();

  public List<Unit> getInnerBody();

  public int getDepStatus();

  public void setDepStatus(int DEP_STATUS_PARTIAL_DEP);

  public void setPartialDepI(int min_i);

  public boolean isInInnerLoop(int code_i);

  public boolean isLoopControl(Value right_op);

  public boolean isInnerLoopControl(Value left_op);

  public int sizeCode();

  public boolean isDependentOnLoopControl(Value op1);

  public void addInnerLoopInMethod(Loop inner);

  public SootMethod getMethod();

  public boolean isInLoop(int code_i);

  public List<Value> getLoopControls();

  public Collection<Unit> getLoopControlUnits();
}
