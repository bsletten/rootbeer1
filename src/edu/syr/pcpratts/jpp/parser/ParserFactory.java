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

package edu.syr.pcpratts.jpp.parser;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;

public class ParserFactory {

  public PutbackIterator<CFileItem> createCppFromFilename(String filename) throws FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    return createCpp(reader);
  }

  public PutbackIterator<CFileItem> createCppFromReader(Reader reader){
    BufferedReader breader = new BufferedReader(reader);
    return createCpp(breader);
  }

  public PutbackIterator<Token> createPreprocFromFilename(String filename) throws FileNotFoundException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    return createPreproc(reader);
  }

  public PutbackIterator<Token> createPreprocFromReader(Reader reader){
    BufferedReader breader = new BufferedReader(reader);
    return createPreproc(breader);
  }

  private PutbackIterator<Token> createPreproc(BufferedReader reader){
    Iterator<Token>      iter0 = new CharacterReader(reader);
    Iterator<Token>      iter1 = new NewlineSplicer(iter0);
    Iterator<Token>      iter2 = new TrigraphReader(iter1);
    Iterator<Token>      iter3 = new StringAndCommentTokenizer(iter2);
    Iterator<Token>      iter4 = new Tokenizer(iter3);
    Iterator<Token>      iter5 = new PreSemiExpression(iter4);
    Iterator<Token>      iter6 = new DigraphReplacer(iter5);
    return new PutbackIterator<Token>(iter6);
  }

  private PutbackIterator<CFileItem> createCpp(BufferedReader reader){
    Iterator<Token>      iter0 = new CharacterReader(reader);
    Iterator<Token>      iter1 = new NewlineSplicer(iter0);
    Iterator<Token>      iter2 = new StringAndCommentTokenizer(iter1);
    Iterator<Token>      iter3 = new Tokenizer(iter2);
    Iterator<Token>      iter4 = new PreSemiExpression(iter3);
    Iterator<Token>      iter5 = new DigraphReplacer(iter4);
    Iterator<Token>      iter6 = new NewlineRemover(iter5);
    return createFromTokenIterator(iter6);
  }

  public PutbackIterator<CFileItem> createFromTokenIterator(Iterator<Token> iter0) {
    Iterator<Token>       iter4 = new BasicBlockNormalize(iter0);
    Iterator<Token>       iter5 = new DoNormalization(iter4);
    Iterator<CStatement>  iter6 = new SemiExpression(iter5);
    Iterator<CFileItem>   iter7 = new ForSemiExpression(iter6);

    return new PutbackIterator<CFileItem>(iter7);
  }
}
