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

package edu.syr.pcpratts.rootbeer.compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.internal.JSpecialInvokeExpr;

public class SpecialInvokeFixup {
 
  private Value m_CastLhs;
  private Value m_CastRhs;
  private Unit m_CastUnit;
  private List<Unit> m_ToDelete;
  
  public Body fixup(Body body){
    m_ToDelete = new ArrayList<Unit>();
    Iterator<Unit> iter = body.getUnits().iterator();
    while(iter.hasNext()){
      Unit next = iter.next();
      if(next instanceof AssignStmt){
        AssignStmt assign = (AssignStmt) next;
        Value lhs = assign.getLeftOp();
        Value rhs = assign.getRightOp();
        if(rhs instanceof CastExpr){
          m_CastLhs = lhs;
          CastExpr cast = (CastExpr) rhs;
          m_CastRhs = cast.getOp();
          m_CastUnit = next;
        }
      }      
      
      List<ValueBox> boxes = next.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value value = box.getValue();
        if(value instanceof JSpecialInvokeExpr){
          JSpecialInvokeExpr expr = (JSpecialInvokeExpr) value;
          Value base = expr.getBase();
          if(base.equals(m_CastLhs) == false)
            continue;
          expr.getBaseBox().setValue(m_CastRhs);
          m_ToDelete.add(m_CastUnit);
        }
      }
    }
    
    for(Unit unit : m_ToDelete){
      PatchingChain<Unit> units = body.getUnits();
      units.remove(unit);
    }
    
    return body;
  }
}
