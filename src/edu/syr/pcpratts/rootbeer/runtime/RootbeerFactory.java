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

package edu.syr.pcpratts.rootbeer.runtime;

import java.lang.reflect.Constructor;

public class RootbeerFactory {

  //http://java.sun.com/developer/technicalArticles/ALT/Reflection/
  public IRootbeer create() {
    try {
      Class c = Class.forName("edu.syr.pcpratts.rootbeer.runtime.ConcreteRootbeer");
      Constructor<Rootbeer> ctor = c.getConstructor();
      return ctor.newInstance();
    } catch(Exception ex){
      ex.printStackTrace();
      return null;
    }
  }
}
