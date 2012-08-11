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

package edu.syr.pcpratts.rootbeer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader {

  private URLClassLoader m_loader;
  
  public JarClassLoader(String jar_name) throws MalformedURLException {
    URL[] urls = filenameToUrl(jar_name);
    m_loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader().getParent());
  }
 
  private URL[] filenameToUrl(String filename) throws MalformedURLException {
    File f = new File(filename);
    String path = f.getAbsolutePath();
    URL url = new URL("file:///"+path);
    URL[] urls = new URL[1];
    urls[0] = url;
    return urls;
  }
  
  public ClassLoader getLoader(){
    return m_loader;
  }
}
