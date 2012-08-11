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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse;

import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerFactory;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerializationFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main implements TestSerializationFactory {

  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new OfCoarse());
    return ret;
  }

  public static void main(String[] args){
    Main m = new Main();
    List<TestSerialization> providers = m.getProviders();
    for(TestSerialization provider : providers){
      m.test(provider);
    }
  }

  private void test(TestSerialization provider) {
    System.out.print("Testing provider: "+provider.toString()+"...");
    List<Kernel> objects = provider.create();
    List<Kernel> known_goods = provider.create();

    Rootbeer rootbeer = new Rootbeer();
    int i = 0;
    Iterator<Kernel> iter = rootbeer.run(objects.iterator());
    while(iter.hasNext()){
      Kernel result = iter.next();
      Kernel known_good = known_goods.get(i);
      known_good.gpuMethod();

      if(!provider.compare(result, known_good)){
        System.out.println("Failed at: "+i);
        System.exit(-1);
      }

      ++i;
    }
    System.out.println("PASSED");
  }
}
