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

import java.util.ArrayList;
import java.util.List;
import soot.Unit;

public class MonitorGroupItem {
  
  private List<Unit> m_PrefixUnits;
  private Unit m_EnterMonitorUnit;
  private List<MonitorGroupItem> m_MonitorGroups;
  
  public MonitorGroupItem(){
    m_PrefixUnits = new ArrayList<Unit>();
    m_MonitorGroups = new ArrayList<MonitorGroupItem>();
  }

  public List<Unit> getPrefixUnits() {
    return m_PrefixUnits;
  }
    
  public List<MonitorGroupItem> getGroups(){
    return m_MonitorGroups;
  }
  
  public Unit getEnterMonitor(){
    return m_EnterMonitorUnit;
  }

  public void addUnit(Unit curr) {
    m_PrefixUnits.add(curr);
  }
  
  public void addGroup(MonitorGroupItem item) {
    m_MonitorGroups.add(item);
  }

  public void addEnterMonitor(Unit curr) {
    m_EnterMonitorUnit = curr;
  }
}
