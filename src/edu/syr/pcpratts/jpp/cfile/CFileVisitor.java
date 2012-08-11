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

package edu.syr.pcpratts.jpp.cfile;

import edu.syr.pcpratts.jpp.parser.PutbackIterator;

public abstract class CFileVisitor {

  boolean m_UseSubIterators;
  
  public CFileVisitor(boolean use_subiterators){
    m_UseSubIterators = use_subiterators;  
  }
  
  public void visit(CFile cfile){
    PutbackIterator<CFileItem> iter = cfile.iterator();
    doVisit(iter);
  }  

  private void doVisit(PutbackIterator<CFileItem> iter) {
    while(iter.hasNext()){
      CFileItem next = iter.next();
      visitItem(next);
      if(next instanceof CClass){
        CClass cclass = (CClass) next;
        if(m_UseSubIterators)
          doVisit(cclass.subiterator());
        else 
          doVisit(cclass.iterator());
      } else if(next instanceof CMethod){
        CMethod cmethod = (CMethod) next;
        if(m_UseSubIterators)
          doVisit(cmethod.subiterator());
        else
          doVisit(cmethod.iterator());
      }
    }
  }
  
  protected abstract void visitItem(CFileItem item);
}
