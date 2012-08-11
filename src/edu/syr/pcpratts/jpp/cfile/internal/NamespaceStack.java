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

package edu.syr.pcpratts.jpp.cfile.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NamespaceStack {

  private List<String> m_Type;
  private List<String> m_Identifiers;

  public NamespaceStack(){
    m_Type = new LinkedList<String>();
    m_Identifiers = new LinkedList<String>();
  }

  public boolean isEmpty(){
    return m_Type.isEmpty();
  }

  public void push(String scope, String identifier){
    m_Type.add(scope);
    m_Identifiers.add(identifier);
  }

  public String getNamespace(){
    List<String> namespaces = new ArrayList<String>();
    for(int i = 0; i < m_Type.size(); ++i){
      String type = m_Type.get(i);
      String id = m_Identifiers.get(i);
      if(type.equals("namespace"))
        namespaces.add(id);
    }
    String ret = "";
    for(int i = 0; i < namespaces.size(); ++i){
      ret += namespaces.get(i);
      if(i < namespaces.size()-1)
        ret += "::";
    }
    return ret;
  }

  public void pop(){
    m_Type.remove(m_Type.size()-1);
    m_Identifiers.remove(m_Identifiers.size()-1);
  }

  boolean nameSpaceOnTop() {
    String top = m_Type.get(m_Type.size()-1);
    if(top.equals("namespace"))
      return true;
    return false;
  }
}
