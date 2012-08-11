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

import edu.syr.pcpratts.rootbeer.util.Stack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;

public class MonitorGroups {
 
  public List<MonitorGroupItem> getItems(Body body){
    List<MonitorGroupItem> ret = new ArrayList<MonitorGroupItem>();
    Stack<MonitorGroupItem> stack = new Stack<MonitorGroupItem>();
    MonitorGroupItem curr_monitor = new MonitorGroupItem();
    ret.add(curr_monitor);
    stack.push(curr_monitor);
    List<Unit> units = getUnits(body);
    for(int i = 0; i < units.size(); ++i){
      Unit curr = units.get(i);
      if(curr instanceof EnterMonitorStmt){
        MonitorGroupItem item = new MonitorGroupItem();
        item.addEnterMonitor(curr);
        stack.top().addGroup(item);
        stack.push(item);
        MonitorGroupItem item2 = new MonitorGroupItem();
        stack.top().addGroup(item2);
        stack.push(item2);
      } else if(curr instanceof ExitMonitorStmt){
        if(isLastExit((ExitMonitorStmt) curr, i, units)){          
          stack.top().addUnit(curr); 
          stack.pop();
          stack.pop();
          stack.pop();
          if(stack.size() == 0){
            curr_monitor = new MonitorGroupItem();
            ret.add(curr_monitor);
            stack.push(curr_monitor);
          } else {      
            curr_monitor = new MonitorGroupItem();
            stack.top().addGroup(curr_monitor);
            stack.push(curr_monitor);
          }
        } else {
          stack.top().addUnit(curr); 
        }
      } else {
        stack.top().addUnit(curr); 
      }
    }
    //if(body.getMethod().getName().equals("gpuMethod")){
    //  System.out.println("gpuMethod");
    //  print(ret);
    //}
    return ret;
  }
  
  private boolean isLastExit(ExitMonitorStmt exit, int index, List<Unit> units){
    Value op = exit.getOp();
    Local op_local = (Local) op;
    for(int j = index +1; j < units.size(); ++j){
      Unit curr = units.get(j);
      if(curr instanceof EnterMonitorStmt){
        return true;
      } else if(curr instanceof ExitMonitorStmt){
        ExitMonitorStmt exit2 = (ExitMonitorStmt) curr;
        Local op_local2 = (Local) exit2.getOp();
        if(op_local.getName().equals(op_local2.getName())){
          return false;
        }
      }
    }
    return true;
  }
  
  private List<Unit> getUnits(Body body){
    List<Unit> ret = new ArrayList<Unit>();
    Iterator<Unit> iter = body.getUnits().iterator();
    while(iter.hasNext()){
      Unit curr = iter.next();
      ret.add(curr);
    }
    return ret;
  }

  private void print(List<MonitorGroupItem> ret) {
    for(MonitorGroupItem item : ret){
      System.out.println("<group_item>");     
      System.out.println("<prefix>");
      List<Unit> prefix = item.getPrefixUnits(); 
      for(Unit pre : prefix){
        System.out.println(pre.toString());
      }           
      System.out.println("</prefix>");      
      if(item.getEnterMonitor() != null){
        System.out.println("<enter>");
        System.out.println(item.getEnterMonitor().toString());
        System.out.println("</enter>");
      }
      print(item.getGroups());
      System.out.println("</group_item>");
    }
  }
}
