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

import edu.syr.pcpratts.rootbeer.compiler.RaiseResolvingLevelException;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import soot.SootClass;

public class AddBasicClass {

  private String m_MissingClass;
  private int m_MissingLevel;
  
  public void add(RuntimeException ex){
    if(ex instanceof RaiseResolvingLevelException){
      RaiseResolvingLevelException raise = (RaiseResolvingLevelException) ex;
      RootbeerScene.v().sootLoadClass(raise.getClassName(), SootClass.BODIES);
      RootbeerScene.v().reinit();
      return;
    }
    String message = ex.getMessage();
    //ex.printStackTrace();
    if(message == null){
      throw ex;
    }
    if(extractMissingClassAddBasicClass(message)){
      RootbeerScene.v().sootLoadClass(m_MissingClass, m_MissingLevel);
      RootbeerScene.v().reinit();
      return;
    }
    if(extractMissingClassAborting(message)){
      RootbeerScene.v().addGetClass(m_MissingClass);
      RootbeerScene.v().reinit();
      return;
    }
    throw ex;
  }
  
  private boolean extractMissingClassAddBasicClass(String message) {
    String[] tokens = message.split(":");
    if(tokens.length != 2)
      return false;
    String str = tokens[1];
    String[] tokens2 = str.split("addBasicClass\\(");
    if(tokens2.length != 2)
      return false;
    String[] tokens3 = tokens2[1].split(",");
    String ret = tokens3[0].trim();
    m_MissingClass = ret;
    String[] tokens4 = tokens3[1].split("\\)");
    String level = tokens4[0];
    if(level.equals("BODIES"))
      m_MissingLevel = SootClass.BODIES;
    if(level.equals("DANGLING"))
      m_MissingLevel = SootClass.DANGLING;
    if(level.equals("HIERARCY"))
      m_MissingLevel = SootClass.HIERARCHY;
    if(level.equals("SIGNATURES"))
      m_MissingLevel = SootClass.SIGNATURES;
    return true;
  }

  private boolean extractMissingClassAborting(String message) {
    String begin_message = "Aborting: can't find classfile ";
    message = message.trim();
    if(message.startsWith(begin_message) == false)
      return false;
    m_MissingClass = message.substring(begin_message.length()).trim();
    return true;
  }  
}
