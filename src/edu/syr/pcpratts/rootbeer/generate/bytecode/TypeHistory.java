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

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.RefType;
import soot.SootClass;
import soot.Type;

public class TypeHistory {
  private Set<Type> mHistory;

  public TypeHistory(SootClass runtime_basic_block){
    mHistory = new HashSet<Type>();
    addType(runtime_basic_block.getType());
  }

  public void addType(Type type){
    mHistory.add(type);
  }
  
  public List<Type> getHistory() {
    List<Type> ret = new ArrayList<Type>();
    Iterator<Type> iter = mHistory.iterator();
    while(iter.hasNext()){
      ret.add(iter.next());
    }
    return ret;
  }
}
