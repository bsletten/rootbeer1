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

public class CType extends CFileItem {

  private List<Token> m_Tokens;
  private List<CTemplate> m_Templates;
  private CNamespace m_Namespace;
  
  public CType(List<String> tokens){
    m_Tokens = new ArrayList<Token>();
    for(String token : tokens){
      m_Tokens.add(new Token(token));
    }
    m_Templates = new ArrayList<CTemplate>();
  }

  public CType(String type_name) {
    m_Tokens = new ArrayList<Token>();
    m_Tokens.add(new Token(type_name));
    m_Templates = new ArrayList<CTemplate>();
  }
  
  public void addTemplates(List<CTemplate> templates){
    m_Templates.addAll(templates);
  }
  
  public void setNamespace(CNamespace namespace){
    m_Namespace = namespace;
  }
  
  @Override 
  public String toString(){
    String ret = "";
    for(CTemplate template : m_Templates){
      ret += template.toString()+"\n";
    }
    if(m_Namespace != null){
      ret += m_Namespace.toString()+"::";
    }
    for(Token token : m_Tokens){
      ret += token.getString() + " ";
    }
    return ret;
  }

  @Override
  public List<Token> getTokens() {
    return m_Tokens;
  }
}
