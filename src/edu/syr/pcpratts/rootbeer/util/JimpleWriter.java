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

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import soot.Printer;
import soot.SootClass;
import soot.util.EscapedWriter;

public class JimpleWriter {

  public void write(String filename, SootClass cls) throws Exception {
    OutputStream streamOut = new FileOutputStream(filename);
    PrintWriter writerOut = null;

    writerOut = new PrintWriter(
                new EscapedWriter(new OutputStreamWriter(streamOut)));
    Printer.v().printTo(cls, writerOut);

    writerOut.flush();
    streamOut.close();
  }
  
}
