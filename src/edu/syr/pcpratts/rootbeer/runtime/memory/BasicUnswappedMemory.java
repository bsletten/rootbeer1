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

package edu.syr.pcpratts.rootbeer.runtime.memory;

public class BasicUnswappedMemory extends BasicMemory {

  public BasicUnswappedMemory(long i) {
    super(i);
    
    loffset0 = 7;
    loffset1 = 6;
    loffset2 = 5;
    loffset3 = 4;
    loffset4 = 3;
    loffset5 = 2;
    loffset6 = 1;
    loffset7 = 0;
    
    ioffset0 = 3;
    ioffset1 = 2;
    ioffset2 = 1;
    ioffset3 = 0;
    
    soffset0 = 1;
    soffset1 = 0;
  }

  @Override
  public void useInstancePointer() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void useStaticPointer() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
