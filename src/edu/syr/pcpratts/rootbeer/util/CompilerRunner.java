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

package edu.syr.pcpratts.rootbeer.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CompilerRunner {

  public void run(String command) {
    try {
      System.out.println("Compiling CUDA code.");
      Process p = Runtime.getRuntime().exec(command);
      StreamReader reader1 = new StreamReader(p.getInputStream());
      StreamReader reader2 = new StreamReader(p.getErrorStream());
      reader1.join();
      reader2.join();
      int ret = p.waitFor();
      if(ret != 0){
        System.out.println("CUDA Compile failed: "+ret);
        reader1.print();
        reader2.print();
        System.exit(0);
      } 
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }  
  
  private class StreamReader implements Runnable {

    private InputStream m_InputStream;
    private Thread m_Thread;
    private List<String> m_Lines;
    
    public StreamReader(InputStream is){
      m_InputStream = is;
      m_Lines = new ArrayList<String>();
      m_Thread = new Thread(this);
      m_Thread.setDaemon(true);
      m_Thread.start();
    }   
    
    public void join(){
      try {
        m_Thread.join();
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
    
    public void run() {
      BufferedReader reader = new BufferedReader(new InputStreamReader(m_InputStream));
      while(true){
        try {
          String line = reader.readLine();
          if(line == null)
            break;
          m_Lines.add(line);
        } catch(Exception ex){
          break; 
        }        
      }
    }

    private void print() {
      for(String line : m_Lines){
        System.out.println(line);
      }
    }
    
  }
}
