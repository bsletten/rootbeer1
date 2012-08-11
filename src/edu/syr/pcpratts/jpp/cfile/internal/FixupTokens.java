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

import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.ForSemiExpression;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.SemiExpression;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FixupTokens {

  private CFile m_Ret;

  public CFile fixup(CFile file){

    PutbackIterator<CFileItem> iter = file.iterator();
    m_Ret = new CFile();

    while(iter.hasNext()){
      CFileItem next = iter.next();
      if(next instanceof CStatement == false){
        m_Ret.addItem(next);
        continue;
      }

      //now we have found a CStatement
      List<Token> tokens = new ArrayList<Token>();
      tokens.addAll(next.getTokens());

      while(iter.hasNext()){
        CFileItem nnext = iter.next();
        if(nnext instanceof CStatement){
          tokens.addAll(nnext.getTokens());
        } else {
          addFixedTokens(tokens);
          tokens.clear();
          m_Ret.addItem(nnext);
          break;
        }
      }
      if(tokens.isEmpty() == false){
        addFixedTokens(tokens);
      }
    }

    return m_Ret;
  }

  private void addFixedTokens(List<Token> tokens) {
    Iterator<CStatement>  iter = new SemiExpression(tokens.iterator());
    Iterator<CFileItem>   semi = new ForSemiExpression(iter);
    while(semi.hasNext()){
      CFileItem next = semi.next();
      m_Ret.addItem(next);
    }
  }
}
