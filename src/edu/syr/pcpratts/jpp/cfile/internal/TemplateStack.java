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

import edu.syr.pcpratts.jpp.cfile.CTemplate;
import java.util.ArrayList;
import java.util.List;

public class TemplateStack {

  private Stack<String> m_Type;
  private Stack<CTemplate> m_Template;
  
  public TemplateStack(){
    m_Type = new Stack<String>();
    m_Template = new Stack<CTemplate>();
  }

  public boolean isEmpty(){
    return m_Type.isEmpty();
  }

  public void push(String scope, CTemplate template){
    m_Type.push(scope);
    m_Template.push(template);
  }

  public void pop(){
    m_Type.pop();
    m_Template.pop();
  }
  
  public List<CTemplate> getList(){
    List<CTemplate> ret = new ArrayList<CTemplate>();
    List<String> type = m_Type.getList();
    List<CTemplate> templates = m_Template.getList();
    for(int i = 0; i < type.size(); ++i){
      if(type.get(i).equals("template")){
        ret.add(templates.get(i));
      }
    }
    return ret;
  }
}
