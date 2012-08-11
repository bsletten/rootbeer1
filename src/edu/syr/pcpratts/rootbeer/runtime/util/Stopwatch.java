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

package edu.syr.pcpratts.rootbeer.runtime.util;

public class Stopwatch {
  private long start;
  private long stop;

  public void start() {
    start = System.currentTimeMillis(); // start timing
  }

  public void stop() {
    stop = System.currentTimeMillis(); // stop timing
  }

  public long elapsedTimeMillis() {
    return stop - start;
  }

  public @Override String toString() {
    return "elapsedTimeMillis: " + Long.toString(elapsedTimeMillis()); // print execution time
  }

  public void stopAndPrint(String str){
    stop();
    System.out.println(str + toString());
  }
}
