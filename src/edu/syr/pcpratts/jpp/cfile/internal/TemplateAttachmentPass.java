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

import edu.syr.pcpratts.jpp.cfile.Pass;
import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CTemplate;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.List;

class TemplateAttachmentPass implements Pass {

  private CFile m_Ret;
  private PutbackIterator<CFileItem> m_Iterator;
  private Stack<CTemplate> m_TemplateStack;
  private Stack<Integer> m_TemplateScope;
  private int m_Scope;
  
  public TemplateAttachmentPass() {
    m_TemplateStack = new Stack<CTemplate>();
    m_TemplateScope = new Stack<Integer>();
    m_TemplateScope.push(-1);
  }

  public CFile execute(PutbackIterator<CFileItem> iter) {
    m_Ret = new CFile();
    m_Iterator = iter;
    m_Scope = 0;
    while(iter.hasNext()){
      CFileItem next = m_Iterator.next();
      
      if(next instanceof CTemplate){
        m_TemplateScope.push(m_Scope);
        m_TemplateStack.push((CTemplate) next);
        continue;
      }
            
      List<Token> tokens = next.getTokens();
      String last = tokens.get(tokens.size()-1).getString();
      
      addTemplates(next);
      
      if(last.equals(";")){
        while(true){
          int top = m_TemplateScope.top();
          if(top == m_Scope){
            m_TemplateScope.pop();
            m_TemplateStack.pop();
          } else {
            break;
          }
        }
      } else if(last.equals("{")){
        m_Scope++;
      } else if(last.equals("}")){
        m_Scope--;
        while(true){
          int top = m_TemplateScope.top();
          if(top == m_Scope){
            m_TemplateScope.pop();
            m_TemplateStack.pop();
          } else {
            break;
          }
        }
      }
      
      m_Ret.addItem(next);
    }
    return m_Ret;
  }
  
  private void addTemplates(CFileItem next){
    List<CTemplate> templates = m_TemplateStack.getList();
    for(CTemplate template : templates)
      next.addTemplate(template); 
  }
}
