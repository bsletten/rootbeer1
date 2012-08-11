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
import soot.SootClass;
import soot.util.Chain;

public class FindRunOnGpuClasses {
  
  public List<String> get(List<String> app_classes){
       
    List<String> ret = new ArrayList<String>();
    for(String cls : app_classes){
      try {
        SootClass soot_class = RootbeerScene.v().getClass(cls);
        if(matches(soot_class)){
          ret.add(soot_class.getName());
        }
      } catch(RuntimeException ex){
        continue;
      }
    }
    return ret;
  }
  
  public boolean matches(SootClass soot_class){
    Chain<SootClass> interfaces = soot_class.getInterfaces();
    Iterator<SootClass> iter = interfaces.iterator();
    while(iter.hasNext()){
      SootClass cls = iter.next();
      if(cls.getName().equals("edu.syr.pcpratts.rootbeer.runtime.Kernel"))
        return true;
    }
    return false;
  }
}
