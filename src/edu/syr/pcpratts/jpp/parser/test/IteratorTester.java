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

package edu.syr.pcpratts.jpp.parser.test;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.BasicBlockNormalize;
import edu.syr.pcpratts.jpp.parser.CharacterReader;
import edu.syr.pcpratts.jpp.parser.DoNormalization;
import edu.syr.pcpratts.jpp.parser.ForSemiExpression;
import edu.syr.pcpratts.jpp.parser.DigraphReplacer;
import edu.syr.pcpratts.jpp.parser.NewlineRemover;
import edu.syr.pcpratts.jpp.parser.NewlineSplicer;
import edu.syr.pcpratts.jpp.parser.PreSemiExpression;
import edu.syr.pcpratts.jpp.parser.SemiExpression;
import edu.syr.pcpratts.jpp.parser.StringAndCommentTokenizer;
import edu.syr.pcpratts.jpp.parser.Token;
import edu.syr.pcpratts.jpp.parser.Tokenizer;
import edu.syr.pcpratts.jpp.parser.cpp.CppRunner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

public class IteratorTester {

  public static void main(String[] args){
    IteratorTester m = new IteratorTester();
    m.run();
  }
  
  private void run(){
    try {
      TestFilesListFactory factory = new TestFilesListFactory();
      List<String> files = factory.create();
      for(String file : files){
        if(testFile(file)){
          writeToFile(file);
          factory.setPassed(file);
        } else {
          return;
        }
      }
    } catch(Exception ex){
      ex.printStackTrace();
      return;
    }
  }
  
  private Iterator<CFileItem> createIter(String file) throws Exception {
    CppRunner runner = new CppRunner();
    Reader cpp_reader = runner.run(file);
    BufferedReader reader = new BufferedReader(cpp_reader);
    Iterator<Token>      iter0 = new CharacterReader(reader);
    Iterator<Token>      iter1 = new NewlineSplicer(iter0);
    Iterator<Token>      iter1b = new StringAndCommentTokenizer(iter1);
    Iterator<Token>      iter2 = new Tokenizer(iter1b);
    Iterator<Token>      iter3 = new PreSemiExpression(iter2);
    Iterator<Token>      iter2a = new DigraphReplacer(iter3);
    Iterator<Token>      iter3b = new NewlineRemover(iter2a);
    Iterator<Token>      iter4 = new BasicBlockNormalize(iter3b);
    Iterator<Token>      iter5 = new DoNormalization(iter4);
    Iterator<CStatement> iter6 = new SemiExpression(iter5);
    Iterator<CFileItem>  iter7 = new ForSemiExpression(iter6);
    return iter7;
  }
  
  public boolean test(Iterator iter) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      PrintWriter writer = new PrintWriter("iter_text.c");
      while(iter.hasNext()){
        Object next = iter.next();
        if(next instanceof Token){
          Token token = (Token) next;
          if(token.isNewLine())
            continue;
        }
        System.out.println(next.toString());
        writer.println(next.toString());
        writer.flush();
      }
      writer.close();
      System.out.println("Pass? [enter for yes]");
      String line = reader.readLine();
      if(line.equals("pass") || line.equals("yes") || line.equals(""))
        return true;
      return false;
    } catch(Exception ex){
      ex.printStackTrace();
      System.exit(-1);
      return false;
    }
  }

  private boolean testFile(String file) throws Exception {
    System.out.println("Running the iteration tester: "+file);
    copyTextFile(file, "iter_text.orig.c");
    Iterator<CFileItem> iter7 = createIter(file);
    return test(iter7);
  }

  private void writeToFile(String file) throws Exception {
    PrintWriter writer = new PrintWriter(file + ".passed");
    Iterator<CFileItem> iter = createIter(file);
    while(iter.hasNext()){
      CFileItem next = iter.next();
      writer.println(next.toString());
      writer.flush();
    }
    writer.close();
  }

  private void copyTextFile(String src, String dest) throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader(src));
    PrintWriter writer = new PrintWriter(dest);
    while(true){
      String line = reader.readLine();
      if(line == null)
        break;
      writer.println(line);
      writer.flush();
    }
    writer.close();
    reader.close();    
  }
}
