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

package edu.syr.pcpratts.jpp.typetable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Combinatorics {
    
  public Combinatorics(){
  }
  
  /**
   * @param size the number of digits in the result
   * @return 
   */
  public List<List<Integer>> generate(int size){
    List<List<Integer>> ret = new ArrayList<List<Integer>>();
    
    if(size == 1){
      List<Integer> zero = new ArrayList<Integer>();
      zero.add(0);
      ret.add(zero);
      List<Integer> one = new ArrayList<Integer>();
      one.add(1);
      ret.add(one);
      return ret;
    }
    
    for(int i = 0; i < Math.pow(size, size); ++i){
      List<Integer> curr = baseN(i, size);
      addZeros(curr, size);  
      ret.add(curr);
    }
    return ret;
  }
  
  public List<List<Integer>> generateNoRepeat(int size){
    List<List<Integer>> repeating = generate(size);
    List<List<Integer>> ret = new ArrayList<List<Integer>>();
    for(List<Integer> curr : repeating){
      Set<Integer> visited = new TreeSet<Integer>();
      boolean should_add = true;
      for(Integer i : curr){
        if(visited.contains(i)){
          should_add = false;
          break;
        }
        visited.add(i);
      }
      if(should_add)
        ret.add(curr);
    }
    return ret;
  }
  
  private void display(List<List<Integer>> list){
    for(List<Integer> sublist : list){
      for(int i = 0; i < sublist.size(); ++i){
        System.out.print(sublist.get(i)+" ");
        if(i == sublist.size()-1)
          System.out.println();
      }
    }
  }
  
  public static void main(String[] args){
    Combinatorics brute = new Combinatorics();
    //brute.display(brute.generate(0));
    //brute.display(brute.generate(1));
    //brute.display(brute.generate(2));
    brute.display(brute.generateNoRepeat(3));
    //brute.display(brute.generate(4));
  }

  private List<Integer> baseN(int number, int n) {
    List<Integer> ret = new ArrayList<Integer>();
    if(number == 0){
      ret.add(0);
      return ret;
    }
    while(number > 0){
      int mod = number % n;
      ret.add(mod);
      number /= n;
    }
    
    return ret;
  }

  private void addZeros(List<Integer> curr, int size) {
    List<Integer> ret = new ArrayList<Integer>();
    for(Integer num : curr)
      ret.add(num);
    for(int i = curr.size(); i < size; ++i){
      ret.add(0);
    }
    curr.clear();
    curr.addAll(ret);
  }
}
