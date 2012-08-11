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
import edu.syr.pcpratts.jpp.cfile.CMethod;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.List;

/**
 * Detects methods that are not written right in the body of a class
 * @author pcpratts
 */
public class MethodPass implements Pass {
  
  private PutbackIterator<CFileItem> m_Iterator;

  public CFile execute(PutbackIterator<CFileItem> iter) {
    m_Iterator = iter;
    CFile ret = new CFile();
    while(iter.hasNext()){
      CFileItem next = m_Iterator.next();
      if(next instanceof CStatement == false){
        ret.addItem(next);
        continue;
      }
      CStatement stmt = (CStatement) next;
      List<Token> tokens = stmt.getTokens();

      if(isMethodStatement(tokens)){
        CMethod cmethod = parseMethod(stmt);
        ret.addItem(cmethod);
      } else {
        ret.addItem(next);
      }
    }
    return ret;
  }

  private boolean isMethodStatement(List<Token> tokens) {
    if(tokens.isEmpty())
      return false;
    Token last = tokens.get(tokens.size()-1);
    if(last.getString().equals("{"))
      return true;
    return false;
  }

  private CMethod parseMethod(CStatement opening_statement) {
    List<CFileItem> stmts = new ArrayList<CFileItem>();
    stmts.add(opening_statement);
    List<Token> tokens = opening_statement.getTokens();
    int scope_count = 1;
    while(scope_count != 0 && m_Iterator.hasNext()){
      CFileItem stmt = m_Iterator.next();
      List<Token> new_tokens = stmt.getTokens();
      if(new_tokens.isEmpty())
        continue;
      Token last = new_tokens.get(new_tokens.size()-1);
      if(last.getString().equals("{")){
        scope_count++;
      } else if(last.getString().equals("}")){
        scope_count--;
      }
      stmts.add(stmt);
    }
    return new CMethod(stmts);
  }
}
