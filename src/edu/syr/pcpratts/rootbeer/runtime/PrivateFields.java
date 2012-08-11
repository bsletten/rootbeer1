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

package edu.syr.pcpratts.rootbeer.runtime;

import java.lang.reflect.Field;

public class PrivateFields {

  public byte readByte(Object obj, String name){
    try {
      return getField(obj, name).getByte(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public short readShort(Object obj, String name){
    try {
      return getField(obj, name).getShort(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public char readChar(Object obj, String name){
    try {
      return getField(obj, name).getChar(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public boolean readBoolean(Object obj, String name){
    try {
      return getField(obj, name).getBoolean(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public int readInt(Object obj, String name){
    try {
      return getField(obj, name).getInt(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public long readLong(Object obj, String name){
    try {
      return getField(obj, name).getLong(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public float readFloat(Object obj, String name){
    try {
      return getField(obj, name).getFloat(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
    
  public double readDouble(Object obj, String name){
    try {
      return getField(obj, name).getDouble(obj);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
  
  public void writeByte(Object obj, String name, byte value){
    try {
      getField(obj, name).setByte(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public void writeShort(Object obj, String name, short value){
    try {
      getField(obj, name).setShort(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public void writeChar(Object obj, String name, char value){
    try {
      getField(obj, name).setChar(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeBoolean(Object obj, String name, boolean value){
    try {
      getField(obj, name).setBoolean(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeInt(Object obj, String name, int value){
    try {
      getField(obj, name).setInt(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeLong(Object obj, String name, long value){
    try {
      getField(obj, name).setLong(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeFloat(Object obj, String name, float value){
    try {
      getField(obj, name).setFloat(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
    
  public void writeDouble(Object obj, String name, double value){
    try {
      getField(obj, name).setDouble(obj, value);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }  
  }
  
  public Field getField(Object base, String name){
    Class cls = base.getClass();
    while(true){
      try {
        Field f = cls.getDeclaredField(name);
        f.setAccessible(true);
        return f;      
      } catch(Exception ex){
        cls = cls.getSuperclass();
      }
    } 
  }
}
