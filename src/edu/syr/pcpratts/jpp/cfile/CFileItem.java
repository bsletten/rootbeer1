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

public abstract class CFileItem {

  private CNamespace m_Namespace;
  private List<CTemplate> m_Templates;

  public CFileItem(){
    m_Templates = new ArrayList<CTemplate>();
  }

  public void addNamespace(CNamespace namespace){
    m_Namespace = namespace;
  }

  public CNamespace getNamespace(){
    return m_Namespace;
  }

  public void addTemplate(CTemplate template){
    m_Templates.add(template);      
  }
  
  public List<CTemplate> getTemplates(){
    return m_Templates;
  }
  
  public abstract List<Token> getTokens();
}
