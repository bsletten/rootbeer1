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
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CMethod extends CFileItem {

  private List<CFileItem> m_Items;

  public CMethod(List<CFileItem> items) {
    CFileItem first = items.get(0);
    addNamespace(first.getNamespace());
    List<CTemplate> templates = first.getTemplates();
    for(CTemplate template : templates){
      addTemplate(template);
    }
    m_Items = items;
  }
  
  @Override
  public String toString(){
    String ret = "";
    //ret += "/////////////////////////////////////////////////\n";
    //ret += "//  Parsed Method. "+getNamespace()+" \n";
    //List<CTemplate> templates = getTemplates();
    //for(CTemplate template : templates){
    //  ret += "//  Template: "+template.toString()+"\n";
    //}
    //ret += "/////////////////////////////////////////////////\n";

    for(CFileItem stmt : m_Items){
      ret += stmt.toString() + "\n";
    }
    //ret += "/////////////////////////////////////////////////\n";
    //ret += "//  End Method. \n";
    //ret += "/////////////////////////////////////////////////\n";
    return ret;
  }

  public List<Token> getTokens() {
    List<Token> ret = new ArrayList<Token>();
    for(CFileItem stmt : m_Items){
      ret.addAll(stmt.getTokens());
    }
    return ret;
  }

  public PutbackIterator<CFileItem> subiterator(){
    List<CFileItem> sub_items = new ArrayList<CFileItem>();
    for(int i = 1; i < m_Items.size()-1; ++i){
      sub_items.add(m_Items.get(i));
    }
    Iterator<CFileItem> iter = sub_items.iterator();
    return new PutbackIterator<CFileItem>(iter);
  }

  public PutbackIterator<CFileItem> iterator(){
    Iterator<CFileItem> iter = m_Items.iterator();
    return new PutbackIterator<CFileItem>(iter);
  }
  
  public void subreplace(List<CFileItem> new_list) {
    List<CFileItem> ret = new ArrayList<CFileItem>();
    ret.add(m_Items.get(0));
    for(CFileItem item : new_list)
      ret.add(item);

    ret.add(m_Items.get(m_Items.size()-1));
    m_Items = ret;
  }

  public String getMethodName() {
    List<Token> tokens = getTokens();
    for(int i = 1; i < tokens.size(); ++i){
      Token prev = tokens.get(i-1);
      Token curr = tokens.get(i);
      
      if(curr.getString().equals("(")){
        return prev.getString(); 
      }
    }
    throw new RuntimeException("Could not find method name");
  }
}
