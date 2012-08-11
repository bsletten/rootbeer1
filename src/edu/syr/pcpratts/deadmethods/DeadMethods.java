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

package edu.syr.pcpratts.deadmethods;

import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CMethod;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.cfile.internal.CFileFactory;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DeadMethods {
  
  private List<String> m_MethodNames;
  private Set<String> m_MethodsToFollow;
  
  public DeadMethods(String root_method_name){
    m_MethodNames = new ArrayList<String>();
    m_MethodsToFollow = new HashSet<String>();
    m_MethodsToFollow.add(root_method_name);
  }
  
  public void printMethodsToFollow(){
    System.out.println("Methods To Follow: ");
    Iterator<String> iter = m_MethodsToFollow.iterator();
    while(iter.hasNext()){
      String method = iter.next();
      System.out.println(method);
    }
  }
  
  private void findMethodNames(CFile cfile){
    PutbackIterator<CFileItem> iter2 = cfile.iterator();
    while(iter2.hasNext()){
      CFileItem item = iter2.next();
      if(item instanceof CMethod){
        CMethod method = (CMethod) item;
        String name = method.getMethodName();
        m_MethodNames.add(name);
      } 
    }
  }
  
  private List<String> getMethods(CFileItem item) {
    List<String> ret = new ArrayList<String>();
    List<Token> tokens = item.getTokens();
    for(Token token : tokens){
      String curr = token.getString();
      if(m_MethodNames.contains(curr))
        ret.add(curr);
    }    
    return ret;
  }
  
  private void followMethods(CFile cfile) {
    PutbackIterator<CFileItem> iter2 = cfile.iterator();
    while(iter2.hasNext()){
      CFileItem item = iter2.next();
      if(item instanceof CMethod){
        CMethod method = (CMethod) item;
        String name = method.getMethodName();
        if(m_MethodsToFollow.contains(name)){
          List<String> methods = getMethods(item);  
          m_MethodsToFollow.addAll(methods);
        }
      } 
    }
  }

  private String trim(CFile cfile) {
    StringBuilder ret = new StringBuilder();    
    PutbackIterator<CFileItem> iter2 = cfile.iterator();
    while(iter2.hasNext()){
      CFileItem item = iter2.next();
      if(item instanceof CMethod){
        CMethod method = (CMethod) item;
        String name = method.getMethodName();
        if(m_MethodsToFollow.contains(name)){
          ret.append(item.toString());
        }
      } else if(item instanceof CStatement){
        CStatement stmt = (CStatement) item;
        ret.append(stmt.toPrettyString());
      } else {
        ret.append(item.toString());
      }
    }
    return ret.toString();
  }

  
  public String filter(String code){
    
    ParserFactory pfactory = new ParserFactory();
    StringReader reader = new StringReader(code);
    PutbackIterator<CFileItem> iter = pfactory.createCppFromReader(reader);

    CFileFactory cfactory = new CFileFactory();
    CFile cfile = cfactory.create(iter);
    
    findMethodNames(cfile);
    int prev_size;
    do {
      prev_size = m_MethodsToFollow.size();
      followMethods(cfile);
    } while(prev_size != m_MethodsToFollow.size());
        
    return trim(cfile);
  }
  
  private String readFile(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      StringBuilder ret = new StringBuilder();
      while(true){
        String line = reader.readLine();
        if(line == null)
          break;
        ret.append(line);
        ret.append("\n");
      }
      return ret.toString();
    } catch(Exception ex){
      ex.printStackTrace();
      return null;
    }     
  }  
  
  private void save(String results) {
    try {
      PrintWriter writer = new PrintWriter("trimmed.cu");
      writer.println(results);
      writer.flush();
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public static void main(String[] args){
    DeadMethods dead = new DeadMethods("entry");
    String code = dead.readFile("/home/pcpratts/code/Rootbeer/Rootbeer-Product/generated.cu");
    String results = dead.filter(code);
    dead.save(results);
  }
}
