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

package edu.syr.pcpratts.rootbeer.runtime;

import java.util.Iterator;
import java.util.List;

public class ResultIterator implements Iterator<Kernel> {

  private Iterator<Kernel> m_CurrIter;
  private Iterator<Kernel> m_JobsToEnqueue;
  private ParallelRuntime m_Runtime;

  public ResultIterator(PartiallyCompletedParallelJob partial, ParallelRuntime runtime){
    readPartial(partial);
    m_Runtime = runtime;
  }

  private void readPartial(PartiallyCompletedParallelJob partial){
    List<Kernel> active_jobs = partial.getActiveJobs();
    m_CurrIter = active_jobs.iterator();
    m_JobsToEnqueue = partial.getJobsToEnqueue();
  }

  public boolean hasNext() {
    if(m_CurrIter.hasNext())
      return true;
    if(m_JobsToEnqueue.hasNext() == false)
      return false;
    try {
      readPartial(m_Runtime.run(m_JobsToEnqueue));
    } catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
    return m_CurrIter.hasNext();
  }

  public Kernel next() {
    return m_CurrIter.next();
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
