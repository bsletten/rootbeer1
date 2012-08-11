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

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.concurrent.atomic.AtomicLong;

public class WhileTrueRunOnGpu implements Kernel {

  private AtomicLong m_along;
  private volatile long m_threadId;
  
  public WhileTrueRunOnGpu(AtomicLong along, long id){
    m_along = along;
    m_threadId = id;
  }
  
  public void gpuMethod() {
    int count = 0;
    while(count < 100){
      count++;
      if(count > 95 && m_threadId != -1){
        count = 0;
      }
      long last = m_along.get();
      if(m_along.compareAndSet(last, last + 1)){
        count = 200;
      } 
    }
  }
  
  public AtomicLong get(){
    return m_along;
  }
  
}
