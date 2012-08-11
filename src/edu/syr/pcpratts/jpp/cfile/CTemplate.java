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

import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.List;

public class CTemplate extends CFileItem {

  private List<CFileItem> m_Items;

  public CTemplate(List<CFileItem> items){
    m_Items = items;
  }

  public List<Token> getTokens() {
    List<Token> ret = new ArrayList<Token>();
    for(CFileItem item : m_Items){
      ret.addAll(item.getTokens());
    }
    return ret;
  }

  @Override
  public String toString(){
    String ret = "";

    //ret += getNamespace();

    for(CFileItem stmt : m_Items){
      ret += stmt.toString() + " ";
    }
    return ret;
  }
}
