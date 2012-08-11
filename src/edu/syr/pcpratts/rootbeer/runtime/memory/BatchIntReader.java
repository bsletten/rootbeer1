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

import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BatchIntReader {
  
  static {
    System.load(new File("native/batchint.so.1").getAbsolutePath());
  }
  
  public native void read(byte[] buffer, int length, int[] ret_buffer);
  public native void malloc(int size);
}