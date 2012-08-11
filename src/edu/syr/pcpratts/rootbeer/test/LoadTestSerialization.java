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

package edu.syr.pcpratts.rootbeer.test;

import java.util.List;

public class LoadTestSerialization {

  public List<TestSerialization> load(ClassLoader loader, String class_name) throws Exception {
    Object instance = doLoad(loader, class_name);
    TestSerializationFactory factory = (TestSerializationFactory) instance;
    return factory.getProviders();
  }

  public List<TestException> loadException(ClassLoader loader, String class_name) throws Exception {
    Object instance = doLoad(loader, class_name);
    TestExceptionFactory factory = (TestExceptionFactory) instance;
    return factory.getProviders();
  }
  
  private Object doLoad(ClassLoader loader, String class_name) throws Exception {
    Class classToLoad = Class.forName(class_name, true, loader);
    Object instance = classToLoad.newInstance();
    return instance;
  }
}
