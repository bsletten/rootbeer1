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

package edu.syr.pcpratts.rootbeer.compiler4;

import edu.syr.pcpratts.rootbeer.compiler.Transform2;
import java.util.List;
import java.util.Map;
import soot.SceneTransformer;

public class RootbeerTransform extends SceneTransformer {

  private List<String> m_kernelClasses;
  
  public RootbeerTransform(List<String> kernel_classes){
    m_kernelClasses = kernel_classes;
  }
  
  @Override
  protected void internalTransform(String string, Map map) {
    for(String kernel_class : m_kernelClasses){
      Transform2 transform = new Transform2();
      transform.run(kernel_class);
    }
  }
  
}
