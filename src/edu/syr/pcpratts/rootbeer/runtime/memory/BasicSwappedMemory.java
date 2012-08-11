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

public class BasicSwappedMemory extends BasicMemory {
  
  public BasicSwappedMemory(long size){
    super(size);
    
    loffset0 = 0;
    loffset1 = 1;
    loffset2 = 2;
    loffset3 = 3;
    loffset4 = 4;
    loffset5 = 5;
    loffset6 = 6;
    loffset7 = 7;
    
    ioffset0 = 0;
    ioffset1 = 1;
    ioffset2 = 2;
    ioffset3 = 3;
    
    soffset0 = 0;
    soffset1 = 1;
  }
}
