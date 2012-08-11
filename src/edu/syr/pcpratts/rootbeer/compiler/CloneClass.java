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

import java.util.Iterator;
import java.util.List;
import soot.Body;
import soot.Modifier;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class CloneClass {
  
  public SootClass execute(SootClass soot_class, String new_class_name) {
    SootClass ret = new SootClass(new_class_name, Modifier.PUBLIC);
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      SootMethod new_method = new SootMethod(method.getName(), method.getParameterTypes(), method.getReturnType(), method.getModifiers(), method.getExceptions());
      if(method.isConcrete()){
        Body body = RootbeerScene.v().getBody(method);
        new_method.setActiveBody((Body) body.clone());
      }
      ret.addMethod(new_method);
    }
    Iterator<SootField> iter = soot_class.getFields().iterator();
    while(iter.hasNext()){
      SootField next = iter.next();
      SootField cloned = new SootField(next.getName(), next.getType(), next.getModifiers());
      ret.addField(cloned);
    }
    Iterator<SootClass> iter2 = soot_class.getInterfaces().iterator();
    while(iter2.hasNext()){
      SootClass next = iter2.next();
      ret.addInterface(next);
    }
    if(soot_class.hasSuperclass()){
      ret.setSuperclass(soot_class.getSuperclass());
    }
    if(soot_class.hasOuterClass()){
      ret.setOuterClass(soot_class.getOuterClass());
    }
    return ret;
  }
}
