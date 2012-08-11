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

import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldPackingSorter {

  public List<OpenCLField> sort(List<OpenCLField> input) {

    SortableField[] fields = new SortableField[input.size()];
    for(int i = 0; i < input.size(); ++i){
      fields[i] = new SortableField(input.get(i));
    }

    Arrays.sort(fields);

    List<OpenCLField> ret = new ArrayList<OpenCLField>();
    for(int i = 0; i < fields.length; ++i){
      ret.add(fields[i].getField());
    }
    return ret;

  }

  private class SortableField implements Comparable<SortableField> {
    private OpenCLField m_Field;

    public SortableField(OpenCLField field){
      m_Field = field;
    }

    public int compareTo(SortableField o) {
      Integer this_size = m_Field.getSize();
      Integer other_size = o.m_Field.getSize();

      //sorting from highest to lowest
      int ret = other_size.compareTo(this_size);
      if(ret == 0){
        return m_Field.getName().compareTo(o.m_Field.getName());
      } else {
        return ret;
      }
    }

    public OpenCLField getField(){
      return m_Field;
    }
  }
}
