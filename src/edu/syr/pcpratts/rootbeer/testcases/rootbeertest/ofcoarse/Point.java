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

public class Point {

  public double X;
  public double Y;

  public Point(int x, int y){
    this.X = x;
    this.Y = y;
  }

  public Point(double x, double y){
    this.X = x;
    this.Y = y;
  }

  public Point(Point other){
    this.X = other.X;
    this.Y = other.Y;
  }
  
  public Point(){
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Point other = (Point) obj;
    if (this.X != other.X) {
      return false;
    }
    if (this.Y != other.Y) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    long hash = 5;
    hash = 37 * hash + (long) this.X ^ (long) this.X >>> 32;
    hash = 37 * hash + (long) this.Y ^ (long) this.Y >>> 32;
    return (int) hash;
  }

}
