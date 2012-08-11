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

import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueue<T> {

  private LinkedBlockingQueue<T> m_Queue;

  public BlockingQueue(){
    m_Queue = new LinkedBlockingQueue<T>();
  }

  public void put(T item){
    while(true){
      try {
        m_Queue.put(item);
        return;
      } catch (Exception ex){
        //continue;
      }
    }
  }

  public int size(){
    return m_Queue.size();
  }

  public T take(){
    while(true){
      try {
        return m_Queue.take();
      } catch (Exception ex){
        //continue;
      }
    }
  }
}