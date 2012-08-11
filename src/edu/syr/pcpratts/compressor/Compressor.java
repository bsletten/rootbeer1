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

package edu.syr.pcpratts.compressor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

public class Compressor {

  private Map<String, String> compressMap;
  private int compressNumber;

  public Compressor(){
    compressMap = new HashMap<String, String>();
    compressNumber = 1;

    put("__global__");
    put("__device__");
    put("__shared__");
    put("char");
    put("int");
    put("boolean");
    put("short");
    put("long");
    put("float");
    put("double");
    put("void");
    put("unsigned");
    put("signed");
    put("return");
    put("goto");
    put("if");
    put("for");
    put("break");
    put("continue");
    put("else");
    put("#define");
    put("typedef");
    put("switch");
    put("while");
    put("case");
    put("default");
    put("sizeof");
    put("entry");
    put("barrier");
    put("CLK_GLOBAL_MEM_FENCE");
    put("#ifndef");
    put("#ifdef");
    put("#endif");
    put("atomicMax");
    put("atomicExch");
    put("atomicAdd");
    put("__syncthreads");
    put("blockIdx");
    put("blockDim");
    put("threadIdx");
    put("x");
    compressMap.put("true", "1");
    compressMap.put("false", "0");
    compressMap.put("bool", "int");
    
    //static math functions
    put("abs");
    put("acos");
    put("asin");
    put("atan");
    put("atan2");
    put("cbrt");
    put("ceil");
    put("cos");
    put("cosh");
    put("exp");
    put("expm1");
    put("floor");
    put("IEEEremainder");
    put("log");
    put("log10");
    put("log1p");
    put("max");
    put("min");
    put("pow");
    put("random");
    put("rint");
    put("round");
    put("signum");
    put("sin");
    put("sigh");
    put("sqrt");
    put("tan");
    put("tanh");
    put("toDegrees");
    put("toRadians");
    put("ulp");
  }

  private void put(String str){
    compressMap.put(str, str);
  }

  public String compress(String input) throws RecognitionException {
    List<String> list = new ArrayList<String>();
    String[] tokens = input.split("\\n");
    for(String token : tokens){
      list.add(token);
    }
    return run(list);
  }

  public static void main(String[] args) {
    Compressor m = new Compressor();
    try {
      String ret = m.run(m.getLines("../Product/generated.cu"));
      PrintWriter writer = new PrintWriter("../Product/gencompress.cu");
      writer.append(ret);
      writer.flush();
    } catch(NoViableAltException ex){
      Token token = ex.token;
      ex.printStackTrace();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }

  private String run(List<String> lines) throws RecognitionException {

    boolean string_state = false;
    StringBuilder ret = new StringBuilder();
    for(String line : lines){
      if(line.startsWith("#pragma")){
        ret.append(line+"\n");
        continue;
      }
      //temp for debug to see if things compile in gcc
      //else if(line.contains("barrier")){
      //  continue;
      //}
      string_state = false;
      ANTLRStringStream stream = new ANTLRStringStream(line);
      OpenCLLexer lexer = new OpenCLLexer(stream);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      OpenCLParser parser = new OpenCLParser(tokens);
      OpenCLParser.program_return prog = parser.program();
      List<Boolean> modify = prog.modify;
      List<String> prog_tokens = prog.ret;
      List<Boolean> is_string = prog.string;
      for(int i = 0; i < prog_tokens.size(); ++i){
        String text = prog_tokens.get(i);
        boolean mod = modify.get(i);
        if(is_string.get(i)) {
          string_state = !string_state;
          ret.append(text);
        } else if(mod == false || string_state){
          ret.append(text);
        } else {
          String new_token = compressToken(text);
          ret.append(new_token);
        }
        if(string_state && i+1 < prog_tokens.size() && is_string.get(i+1) ){
          //do nothing
        } else if(string_state && is_string.get(i) ){
          //do nothing
        } else if(text.equals("\\")){
          //do nothing
        } else {
          ret.append(" ");
        }
      }
      ret.append("\n");
    }
    return ret.toString();
  }

  private List<String> getLines(String filename) throws Exception {
    List<String> ret = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    while(true){
      String line = reader.readLine();
      if(line == null)
        break;
      ret.add(line);
    }
    return ret;
  }

  private String compressToken(String text) {
    if(compressMap.containsKey(text)){
      return compressMap.get(text);
    }
    String ret;
    do {
      ret = toBase52(compressNumber);
      compressNumber++;
    } while(compressMap.containsKey(ret));
    compressMap.put(text, ret);
    return ret;
  }

  private String toBase52(int value){
    String compressString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    int radix = compressString.length();
    String ret = "";
    while(value > 0){
      int mod = value % radix;
      value /= radix;
      ret += compressString.charAt(mod);
    }
    return ret;
  }

}
