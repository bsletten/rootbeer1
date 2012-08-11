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

package edu.syr.pcpratts.jpp.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The first item putback is the first to be returned from the putback list
 * @author pcpratts
 * @param <T>
 */
public class PutbackIterator<T> implements Iterator<T> {

  private Iterator<T> m_Iterator;
  private List<T> m_Putbacks;

  public PutbackIterator(Iterator<T> iter){
    m_Iterator = iter;
    m_Putbacks = new LinkedList<T>();
  }

  public boolean hasNext() {
    if(m_Putbacks.isEmpty() == false)
      return true;
    return m_Iterator.hasNext();
  }

  public T next() {
    if(m_Putbacks.isEmpty() == false){
      T ret = m_Putbacks.get(0);
      m_Putbacks.remove(0);
      return ret;
    } else {
      return m_Iterator.next();
    }
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void putback(T item){
    m_Putbacks.add(item);
  }
}
