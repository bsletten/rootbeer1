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

public class BoundingBox {

  private Point m_UpperLeft;
  private Point m_LowerRight;
  private Point m_UpperRight;
  private Point m_LowerLeft;

  public BoundingBox(GpuList<Point> points, int border)
  {
    double min_y = Integer.MAX_VALUE;
    double min_x = Integer.MAX_VALUE;
    double max_y = Integer.MIN_VALUE;
    double max_x = Integer.MIN_VALUE;

    for (int i = 0; i < points.size(); ++i)
    {
      Point p = points.get(i);
      if (p.X < min_x)
      {
        min_x = p.X;
      }
      if (p.X > max_x)
      {
        max_x = p.X;
      }
      if (p.Y < min_y)
      {
        min_y = p.Y;
      }
      if (p.Y > max_y)
      {
        max_y = p.Y;
      }
    }

    m_UpperLeft = new Point(min_x - border, max_y + border);
    m_LowerRight = new Point(max_x + border, min_y - border);

    m_UpperRight = new Point(max_x + border, max_y + border);
    m_LowerLeft = new Point(min_x - border, min_y - border);
  }
  
  public BoundingBox(BoundingBox other){    
    m_UpperLeft = new Point(other.m_UpperLeft);
    m_LowerRight = new Point(other.m_LowerRight);
    m_UpperRight = new Point(other.m_UpperRight);
    m_LowerLeft = new Point(other.m_LowerLeft);
  }

  public double getArea(){
    double xdiff = Math.abs(m_LowerRight.X - m_UpperLeft.X);
    double ydiff = Math.abs(m_UpperLeft.Y - m_LowerRight.Y);
    return xdiff * ydiff;
  }

  public boolean contains(Point p)
  {
    if (p.X < m_UpperLeft.X)
      return false;
    if (p.X > m_LowerRight.X)
      return false;
    if (p.Y < m_LowerRight.Y)
      return false;
    if (p.Y > m_UpperLeft.Y)
      return false;
    return true;
  }

  public boolean touches(BoundingBox other){
    if(other.contains(m_UpperLeft))
      return true;
    if(other.contains(m_UpperRight))
      return true;
    if(other.contains(m_LowerLeft))
      return true;
    if(other.contains(m_LowerRight))
      return true;
    if(contains(other.m_UpperLeft))
      return true;
    if(contains(other.m_UpperRight))
      return true;
    if(contains(other.m_LowerLeft))
      return true;
    if(contains(other.m_LowerRight))
      return true;
    return false;
  }

  public Point getUpperLeft()
  {
    return m_UpperLeft;
  }

  public Point getLowerRight()
  {
    return m_LowerRight;
  }

  public static void main(String[] args){
    GpuList<Point> list1 = new GpuList<Point>();
    GpuList<Point> list2 = new GpuList<Point>();

    list1.add(new Point(100, 100));
    list1.add(new Point(100, 0));
    list1.add(new Point(0, 0));
    list1.add(new Point(0, 100));

    list2.add(new Point(50, 50));
    list2.add(new Point(50, 25));
    list2.add(new Point(25, 25));
    list2.add(new Point(25, 50));

    BoundingBox b1 = new BoundingBox(list1, 1);
    BoundingBox b2 = new BoundingBox(list2, 1);

    System.out.println(b1.touches(b2));
  }
}
