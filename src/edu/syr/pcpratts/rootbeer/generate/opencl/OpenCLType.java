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

package edu.syr.pcpratts.rootbeer.generate.opencl;

import soot.RefLikeType;
import soot.Type;

public class OpenCLType {
  private final Type mSootType;

  public OpenCLType(Type soot_type){
    mSootType = soot_type;
  }

  public String getRefString(){
    if(mSootType instanceof RefLikeType)
      return "int";
    String ret = getDerefString();
    if(ret.equals("long"))
      return "long long";
    return ret;
  }

  public String getDerefString(){
    String ret = mSootType.toString();
    ret = ret.replaceAll("\\.", "_");
    ret = ret.replaceAll("\\[\\]", "__array");
    return ret;
  }

  public boolean isRefType(){
    if(mSootType instanceof RefLikeType)
      return true;
    return false;
  }
  
  public int getSize(){
    if(mSootType instanceof RefLikeType)
      return 4;
    String type = mSootType.toString();
    if(type.equals("byte"))
      return 1;
    if(type.equals("boolean"))
      return 1;
    if(type.equals("char"))
      return 4;
    if(type.equals("short"))
      return 2;
    if(type.equals("int"))
      return 4;
    if(type.equals("long"))
      return 8;
    if(type.equals("float"))
      return 4;
    if(type.equals("double"))
      return 8;
    throw new RuntimeException("Unknown type");
  }
}
