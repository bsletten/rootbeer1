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

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import soot.ArrayType;
import soot.Type;

public class MultiDimensionalArrayTypeCreator {

  private List<ArrayType> m_ArrayTypes;
  private List<Type> m_NotArrayTypes;
  private Map<Type, Set<Integer>> m_Dimensions;

  public MultiDimensionalArrayTypeCreator(){
    m_ArrayTypes = new ArrayList<ArrayType>();
    m_NotArrayTypes = new ArrayList<Type>();
    m_Dimensions = new HashMap<Type, Set<Integer>>();
  }

  public List<Type> create(List<Type> types){
    segregateTypes(types);
    cloneArrayTypes();
    return combine();
  }

  private void segregateTypes(List<Type> types) {
    for(Type type : types){
      if(type instanceof ArrayType){
        m_ArrayTypes.add((ArrayType) type);
      } else {
        m_NotArrayTypes.add(type);
      }
    }
  }

  private void cloneArrayTypes() {
    for(ArrayType type : m_ArrayTypes){
      addDimension(type.baseType, type.numDimensions);
    }
    Iterator<Type> iter = m_Dimensions.keySet().iterator();
    while(iter.hasNext()){
      Type key = iter.next();
      Set<Integer> dimensions = m_Dimensions.get(key);
      doClone(key, dimensions);
    }
  }

  private void addDimension(Type base_type, int num_dimensions) {
    Set<Integer> dim_set;
    if(m_Dimensions.containsKey(base_type)){
      dim_set = m_Dimensions.get(base_type);
    } else {
      dim_set = new TreeSet<Integer>();
      m_Dimensions.put(base_type, dim_set);
    }
    dim_set.add(num_dimensions);
  }

  private List<Type> combine() {
    List<Type> ret = new ArrayList<Type>();
    ret.addAll(m_ArrayTypes);
    ret.addAll(m_NotArrayTypes);
    return ret;
  }

  private void doClone(Type key, Set<Integer> dimensions) {
    int max_int = findMax(dimensions);
    for(int i = 1; i < max_int; ++i){
      if(dimensions.contains(i))
        continue;
      ArrayType curr = ArrayType.v(key, i);
      m_ArrayTypes.add(curr);
    }
  }

  private int findMax(Set<Integer> dimensions) {
    int ret = 1;
    Iterator<Integer> iter = dimensions.iterator();
    while(iter.hasNext()){
      int value = iter.next();
      if(value > ret)
        ret = value;
    }
    return ret;
  }


}
