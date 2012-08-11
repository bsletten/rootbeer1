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

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.memory.BufferPrinter;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;

public class MemoryTest {

  public void run(Memory mem) {
    validate(testBytes(mem));
    System.out.println("TEST PASSED");
  }

  private boolean testBytes(Memory mem) {
    for(int i = 0; i < 500; ++i){
      mem.writeByte((byte) i);
    }
    BufferPrinter printer = new BufferPrinter();
    printer.print(mem, 0, 500);
    mem.setAddress(0);
    for(int i = 0; i < 500; ++i){
      byte b = mem.readByte();
      if(b != (byte) i){
        System.out.println("Byte fail at: "+i);
        return false;
      }
    }
    return true;
  }

  private void validate(boolean value) {
    if(value == false){
      System.out.println("TEST FAILED");
      System.exit(0);
    }
  }
  
  public static void main(String[] args){
    CudaRuntime2.v().memoryTest();
  }
}
