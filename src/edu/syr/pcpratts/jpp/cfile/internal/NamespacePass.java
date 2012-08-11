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
import edu.syr.pcpratts.jpp.cfile.CNamespace;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.List;

class NamespacePass implements Pass {

  private PutbackIterator<CFileItem> m_Iterator;
  private NamespaceStack m_ScopeStack;
  private CFile m_Ret;

  public NamespacePass(){
    m_ScopeStack = new NamespaceStack();
  }

  public CFile execute(PutbackIterator<CFileItem> iter) {
    m_Iterator = iter;
    m_Ret = new CFile();
    while(iter.hasNext()){
      CFileItem next = m_Iterator.next();
      List<Token> tokens = next.getTokens();

      if(next instanceof CStatement == false){
        handleNext(next);
      } else if(isNameSpaceStatement(tokens)){
        String namespace = tokens.get(1).getString();
        m_ScopeStack.push("namespace", namespace);
      } else {
        handleNext(next);
      }
    }
    return m_Ret;
  }

  private boolean isNameSpaceStatement(List<Token> tokens) {
    Token first = tokens.get(0);
    if(first.getString().equals("namespace"))
      return true;
    return false;
  }

  private void handleNext(CFileItem next) {
    next.addNamespace(new CNamespace(m_ScopeStack.getNamespace()));
    List<Token> tokens = next.getTokens();
    String last_token = tokens.get(tokens.size()-1).getString();
    if(last_token.equals("}")){
      if(m_ScopeStack.nameSpaceOnTop() == false){
        m_Ret.addItem(next);
      }
      m_ScopeStack.pop();
    } else if(last_token.equals("{")){
      m_ScopeStack.push(" ", " ");
      m_Ret.addItem(next);
    } else {
      m_Ret.addItem(next);
    }
  }

}
