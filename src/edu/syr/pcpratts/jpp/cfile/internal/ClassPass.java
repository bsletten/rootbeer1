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
import edu.syr.pcpratts.jpp.cfile.CClass;
import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClassPass implements Pass {

  private PutbackIterator<CFileItem> m_Iterator;

  public CFile execute(PutbackIterator<CFileItem> iter){
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

      if(isClassStatement(tokens)){
        CClass cclass = parseClass(stmt);
        ret.addItem(cclass);
      } else {
        ret.addItem(next);
      }
    }
    return ret;
  }

  private boolean isClassStatement(List<Token> tokens) {
    if(tokens.isEmpty())
      return false;
    Token first = tokens.get(0);
    if(first.getString().equals("class"))
      return true;
    if(first.getString().equals("struct"))
      return true;
    if(first.getString().equals("enum"))
      return true;
    if(first.getString().equals("union"))
      return true;
    if(first.getString().equals("typedef"))
      return true;
    return false;
  }

  private CClass parseClass(CStatement opening_statement) {
    List<CFileItem> stmts = new ArrayList<CFileItem>();
    stmts.add(opening_statement);
    List<Token> tokens = opening_statement.getTokens();
    if(endsWithSemi(tokens)){
      //do nothing
    } else {
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
      //there is a statement at the end, get it.
      if(m_Iterator.hasNext()){
        CFileItem stmt = m_Iterator.next();
        stmts.add(stmt);
      }
    }
    CClass ret = new CClass(stmts);
    return ret;
  }

  private boolean endsWithSemi(List<Token> tokens) {
    if(tokens.isEmpty())
      return false;
    Token last = tokens.get(tokens.size()-1);
    if(last.getString().equals(";"))
      return true;
    return false;
  }

}
