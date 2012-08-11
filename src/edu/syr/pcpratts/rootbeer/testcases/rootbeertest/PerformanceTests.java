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

import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerializationFactory;
import java.util.ArrayList;
import java.util.List;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.baseconversion.BaseConversionTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired.BruteForceFFTTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse.OfCoarse;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.MMult;

public class PerformanceTests implements TestSerializationFactory {
  
  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new MMult());
    ret.add(new OfCoarse());
    ret.add(new BaseConversionTest());
    ret.add(new BruteForceFFTTest());
    return ret;
  }
}
