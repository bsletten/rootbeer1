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


package edu.syr.pcpratts.rootbeer.compiler;

import java.util.ArrayList;
import java.util.List;

public class IgnorePackages {

  private List<String> m_ignorePackages;
  
  public IgnorePackages(){
    m_ignorePackages = new ArrayList<String>();
    m_ignorePackages.add("soot");
    m_ignorePackages.add("java_cup");
    m_ignorePackages.add("polyglot");
    m_ignorePackages.add("ppg");
    m_ignorePackages.add("antlr");
    m_ignorePackages.add("org.antlr");
    m_ignorePackages.add("jas");
    m_ignorePackages.add("jasmin");
    m_ignorePackages.add("scm");  
  }
  
  public List<String> get() {
    return m_ignorePackages;
  }
}
