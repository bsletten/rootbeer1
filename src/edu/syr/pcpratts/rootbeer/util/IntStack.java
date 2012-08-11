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

public class IntStack {

  long[] mData;
  int mAlloc;
  int mTopPtr;

  public IntStack(){
    mTopPtr = -1;
    mAlloc = 20;
    mData = new long[20];
  }

  public long pop(){
    long ret = mData[mTopPtr];
    --mTopPtr;
    return ret;
  }

  public void push(long value){
    ++mTopPtr;
    if(mTopPtr >= mAlloc){
      mAlloc *= 2;
      long[] temp = new long[mAlloc];
      for(int i = 0; i < mTopPtr-1; ++i)
        temp[i] = mData[i];
      mData = temp;
    }
    mData[mTopPtr] = value;
  }
}
