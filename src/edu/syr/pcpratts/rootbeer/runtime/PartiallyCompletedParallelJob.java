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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PartiallyCompletedParallelJob {

  private Iterator<Kernel> m_RemainingJobs;
  private List<Kernel> m_ActiveJobs;
  private List<Kernel> m_NotWritten;

  public PartiallyCompletedParallelJob(Iterator<Kernel> remaining_jobs) {
    m_RemainingJobs = remaining_jobs;
    m_ActiveJobs = new LinkedList<Kernel>();
    m_NotWritten = new ArrayList<Kernel>();
  }

  public List<Kernel> getActiveJobs() {
    return m_ActiveJobs;
  }

  public Iterator<Kernel> getJobsToEnqueue(){
    return new CompositeIterator(m_NotWritten, m_RemainingJobs);
  }

  public void enqueueJob(Kernel job){
    m_ActiveJobs.add(job);
  }

  public void enqueueJobs(List<Kernel> items) {
    m_ActiveJobs.addAll(items);
  }

  public void addNotWritten(List<Kernel> not_written) {
    m_NotWritten = new ArrayList<Kernel>();
    m_NotWritten.addAll(not_written);
  }
  
  public class CompositeIterator implements Iterator<Kernel> {

    private Iterator<Kernel> m_NotWritten;
    private Iterator<Kernel> m_Remaining;
    
    private CompositeIterator(List<Kernel> not_written, Iterator<Kernel> remaining) {
      m_NotWritten = not_written.iterator();
      m_Remaining = remaining;
    }    

    public boolean hasNext() {
      if(m_NotWritten.hasNext())
        return true;
      if(m_Remaining.hasNext())
        return true;
      return false;
    }

    public Kernel next() {
      if(m_NotWritten.hasNext())
        return m_NotWritten.next();
      if(m_Remaining.hasNext())
        return m_Remaining.next();
      throw new RuntimeException("out of items");
    }

    public void remove() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
    
  }
}
