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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CFile {

  private List<CFileItem> m_Items;

  public CFile(){
    m_Items = new ArrayList<CFileItem>();
  }

  public CFile(PutbackIterator<CFileItem> iter){
    m_Items = new ArrayList<CFileItem>();
    while(iter.hasNext()){
      m_Items.add(iter.next());
    }
  }

  public void addItem(CFileItem item) {
    m_Items.add(item);
  }

  @Override
  public String toString(){
    String ret = "";
    for(CFileItem item : m_Items){
      ret += item.toString() + "\n";
    }
    return ret;
  }

  public PutbackIterator<CFileItem> iterator(){
    Iterator<CFileItem> iter = m_Items.iterator();
    return new PutbackIterator<CFileItem>(iter);
  }
}
