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

package edu.syr.pcpratts.rootbeer.generate.opencl.body;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLType;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import edu.syr.pcpratts.rootbeer.util.Stack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.JimpleBody;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.util.Chain;

public class OpenCLBody {
  private Body m_Body;
  private List<Unit> m_Labels;
  private boolean m_IsConstructor;
  private boolean m_IsConstructorBodyWithoutHeader;
  private String m_ClassName;
  private int m_RefFieldsSize;
  private MethodStmtSwitch m_StmtSwitch;
  private int m_DerivedType;
  private int m_AllocSize;
  private SootClass m_SootClass;
  private SootMethod m_SootMethod;
  private Map<Unit, List<TrapItem>> m_TrapMap;
  private List<TrapItem> m_AllTraps;

  /**
   * Use this for either a constructor with header or a normal body
   * @param body
   * @param is_constructor 
   */
  public OpenCLBody(SootMethod method, boolean is_constructor){
    m_Labels = new ArrayList<Unit>();
    m_TrapMap = new HashMap<Unit, List<TrapItem>>();
    m_AllTraps = new ArrayList<TrapItem>();
    m_IsConstructor = is_constructor;
    m_IsConstructorBodyWithoutHeader = false;
    m_SootMethod = method;

    m_SootClass = method.getDeclaringClass();
    Body body = RootbeerScene.v().getBody(method);
    setup(body);
  }
  
  /**
   * Use this ctor for a body that is a constructor body without a header
   * @param body 
   */
  public OpenCLBody(Body body){
    m_Labels = new ArrayList<Unit>();
    m_TrapMap = new HashMap<Unit, List<TrapItem>>();
    m_AllTraps = new ArrayList<TrapItem>();
    m_IsConstructor = false;
    m_IsConstructorBodyWithoutHeader = true;
    
    setup(body);
  }

  private void setup(Body body){
    //convert ShimpleBody to JimpleBody
    if(body instanceof ShimpleBody){
      ShimpleBody sbody = (ShimpleBody) body;
      body = sbody.toJimpleBody();
    } else if(body instanceof JimpleBody){
      try {
      ShimpleBody sbody = Shimple.v().newBody(body);
      body = sbody.toJimpleBody();
      } catch(Exception ex){
        SootMethod soot_method = body.getMethod();
        SootClass soot_class = soot_method.getDeclaringClass();
        System.out.println(soot_class+"."+soot_method);
      }
    }
    m_Body = body;  
  }
  
  private Iterator<Unit> bodyIterator(){
    PatchingChain<Unit> chain_units = m_Body.getUnits();
    return chain_units.iterator();
  }

  public String getBody(){
    determineLabels();

    StringBuilder ret = new StringBuilder();
    ret.append(writeLocals());
    ret.append(writeBody());
    return ret.toString();
  }
  
  private void addTrapItem(Unit unit, TrapItem item){
    if(m_TrapMap.containsKey(unit)){
      List<TrapItem> traps = m_TrapMap.get(unit);
      traps.add(item);
    } else {
      List<TrapItem> traps = new ArrayList<TrapItem>();
      traps.add(item);
      m_TrapMap.put(unit, traps);
    }
  }

  private String writeMethodBody(){
    Iterator<Trap> traps = m_Body.getTraps().iterator();
    int trap_num = 0;
    while(traps.hasNext()){
      Trap t = traps.next();
      TrapItem item = new TrapItem(t, bodyIterator(), trap_num, t.getException());
      List<Unit> trap_units = item.getUnits();
      for(Unit u : trap_units){
        addTrapItem(u, item);
      }
      trap_num++;
      m_AllTraps.add(item);
    }

    MonitorGroups monitor_groups = new MonitorGroups();
    List<MonitorGroupItem> root_items = monitor_groups.getItems(m_Body);
    for(MonitorGroupItem item : root_items){
      handleMonitorGroupItem(item);
    }
    return m_StmtSwitch.toString();
  }
  
  private void handleMonitorGroupItem(MonitorGroupItem item){
    List<Unit> units = item.getPrefixUnits();
    for(Unit next : units){
      handleUnit(next);
    } 
    Unit enter = item.getEnterMonitor();
    if(enter != null){
      handleUnit(enter); 
    }
    List<MonitorGroupItem> items = item.getGroups();
    for(MonitorGroupItem sub_item : items){
      handleMonitorGroupItem(sub_item);
    }
    if(enter != null){     
      m_StmtSwitch.append("  }\n");
      m_StmtSwitch.append("}\n");
      m_StmtSwitch.popMonitor(); 
    }
  }
  
  private void handleUnit(Unit next){
    int label_num = labelNum(next);
    if(label_num != -1){
      m_StmtSwitch.append("label" + Integer.toString(label_num) + ":\n");
    }
    int trap_num2 = trapNum(next);
    if(trap_num2 != -1){
      m_StmtSwitch.append("trap" + Integer.toString(trap_num2) + ":\n");
    }
    m_StmtSwitch.append("//"+next.toString()+"\n");
    List<TrapItem> trap_items = null;
    if(m_TrapMap.containsKey(next)){
      trap_items = m_TrapMap.get(next);
    }
    m_StmtSwitch.reset();
    m_StmtSwitch.setTrapItems(trap_items);
    next.apply(m_StmtSwitch);
    if(m_StmtSwitch.hasCaughtExceptionRef()){
      m_StmtSwitch.append("*exception = 0;\n");
    } 
  }
  
  private void determineConstructorInfo(){
    SootMethod soot_method = m_Body.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);

    m_ClassName = ocl_class.getName();
    m_AllocSize = ocl_class.getSize();
    m_RefFieldsSize = ocl_class.getRefFieldsSize();
    m_DerivedType = OpenCLScene.v().getClassType(soot_class);
  }
  
  private String writeConstructorBody(){
    determineConstructorInfo();
    
    StringBuilder ret = new StringBuilder();
    String pointer_namespace_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    ret.append("int thisref = -1;\n");
    int alloc_size = m_AllocSize;
    int mod = m_AllocSize % 8;
    if(mod != 0)
      alloc_size += (8 - mod);
    ret.append("edu_syr_pcpratts_gc_assign(gc_info, &thisref, edu_syr_pcpratts_gc_malloc(gc_info, "+Integer.toString(alloc_size)+"));\n");
    ret.append("if(thisref == -1){\n");
    ret.append("  *exception = -1;\n");
    ret.append("  return -1;\n");
    ret.append("}\n");
    ret.append(pointer_namespace_qual+" char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n");
    ret.append("\n//class info\n");
    ret.append("edu_syr_pcpratts_gc_set_count(thisref_deref, "+Integer.toString(m_RefFieldsSize)+");\n");
    ret.append("edu_syr_pcpratts_gc_set_color(thisref_deref, COLOR_GREY);\n");
    ret.append("edu_syr_pcpratts_gc_set_type(thisref_deref, "+Integer.toString(m_DerivedType)+");\n");
    ret.append("edu_syr_pcpratts_gc_set_ctor_used(thisref_deref, 1);\n");
    ret.append("edu_syr_pcpratts_gc_set_size(thisref_deref, "+Integer.toString(alloc_size)+");\n");
    ret.append("edu_syr_pcpratts_gc_init_monitor(thisref_deref);\n");
    
    if(m_SootClass != null){
      ret.append(initFields());
    }
    
    m_StmtSwitch = new ConstructorStmtSwitch(this, m_Body.getMethod(), false);
    ret.append(writeMethodBody());

    ret.append("return "+m_StmtSwitch.getThisRef()+";\n");
    return ret.toString();
  }
  
  private String initFields(){
    StringBuilder ret = new StringBuilder();
    SootClass soot_class = m_SootClass;
    while(true){
      initFieldsForClass(soot_class, ret);      
      if(soot_class.getName().equals("java.lang.Object"))
        break;
      soot_class = RootbeerScene.v().getClass(soot_class.getSuperclass().getName());
      if(soot_class.getName().equals("java.lang.Object"))
        break;
    }
    return ret.toString();
  }

  private void initFieldsForClass(SootClass soot_class, StringBuilder ret){
    List<OpenCLField> ref_fields = getFields(soot_class, true);
    for(OpenCLField field : ref_fields){
      ret.append(field.getInstanceSetterInvokeWithoutThisref()+"thisref, -1, exception);\n");
    }    
    List<OpenCLField> non_ref_fields = getFields(soot_class, false);
    for(OpenCLField field : non_ref_fields){
      ret.append(field.getInstanceSetterInvokeWithoutThisref()+"thisref, 0, exception);\n");
    }   
  }
  
  private List<OpenCLField> getFields(SootClass soot_class, boolean ref_fields){
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(soot_class);  
    if(ref_fields)
      return ocl_class.getInstanceRefFields();
    else
      return ocl_class.getInstanceNonRefFields();
  }
  
  private String writeBody(){
    if(m_IsConstructorBodyWithoutHeader == false){
      if(m_IsConstructor)
        return writeConstructorBody();
      else {
        m_StmtSwitch = new MethodStmtSwitch(this, m_Body.getMethod());
        return writeMethodBody();
      }
    } else {
      StringBuilder ret = new StringBuilder();
      m_StmtSwitch = new ConstructorStmtSwitch(this, m_Body.getMethod(), true);
      ret.append(writeMethodBody());
      return ret.toString();
    }
  }
  
  private String writeLocals() {
    Chain<Local> locals = m_Body.getLocals();
    String ret = "";
    for(Local local : locals){
      OpenCLType type = new OpenCLType(local.getType());
      ret += type.getRefString()+" "+local.getName();
      if(type.isRefType())
        ret += " = -1";
      ret += ";\n";
    }
    return ret;
  }

  private void determineLabels() {
    Iterator<Unit> iter = bodyIterator();
    while(iter.hasNext()){
      Unit curr = iter.next();
      if(curr instanceof IfStmt){
        IfStmt ifstmt = (IfStmt) curr;
        m_Labels.add(ifstmt.getTarget());
      } else if(curr instanceof GotoStmt){
        GotoStmt gotostmt = (GotoStmt) curr;
        m_Labels.add(gotostmt.getTarget());
      } else if(curr instanceof LookupSwitchStmt){
        LookupSwitchStmt lookup = (LookupSwitchStmt) curr;
        addTargets(lookup.getTargets());
        m_Labels.add(lookup.getDefaultTarget());
      } else if(curr instanceof TableSwitchStmt){
        TableSwitchStmt table = (TableSwitchStmt) curr;
        addTargets(table.getTargets());
      }
    }
  }
  
  private void addTargets(List<Unit> units){
    for(Unit unit : units){
      m_Labels.add(unit);
    }
  }

  int labelNum(Unit unit){
    for(int i = 0; i < m_Labels.size(); ++i){
      if(m_Labels.get(i).equals(unit))
        return i;
    }
    return -1;
  }

  private int trapNum(Unit next) {
    for(TrapItem trap : m_AllTraps){
      if(trap.unitIsHandler(next)){
        return trap.getTrapNum();
      }
    }
    return -1;
  }
}
