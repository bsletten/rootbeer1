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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

import edu.syr.pcpratts.rootbeer.test.TestException;
import edu.syr.pcpratts.rootbeer.test.TestExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception.NullPointer1Test;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception.NullPointer2Test;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired.ExceptionBasicTest;

public class ExMain implements TestExceptionFactory {

  public List<TestException> getProviders() {
    List<TestException> ret = new ArrayList<TestException>();
    ret.add(new ExceptionBasicTest());
    ret.add(new NullPointer1Test());
    ret.add(new NullPointer2Test());
    return ret;
  }

  
}
