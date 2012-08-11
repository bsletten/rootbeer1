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

package edu.syr.pcpratts.rootbeer.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import soot.Body;
import soot.CoffiClassSource;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.util.JimpleWriter;
import java.io.FileNotFoundException;
import java.util.*;
import soot.ArrayType;
import soot.RefType;
import soot.Type;
import soot.javaToJimple.IInitialResolver.Dependencies;

public class RootbeerScene {

  private static RootbeerScene m_Instance;
  
  public static RootbeerScene v(){
    if(m_Instance == null)
      m_Instance = new RootbeerScene();
    return m_Instance;
  }

  public static void reset() {
    m_Instance = null;
  }
  
  private Map<String, SootClass> m_Classes;
  private Map<String, Body> m_Bodies;
  private Map<String, Integer> m_SootLoadedClasses;
  private List<String> m_GetClassClasses;
  
  private List<String> m_CopiedClasses;
  private List<String> m_ApplicationClasses;
  private List<String> m_Dependencies;
  private List<String> m_AddedClasses;
  private List<String> m_Fixups;
  
  private List<String> m_ignorePackages;
  private List<String> m_reachables;
  
  public RootbeerScene(){
    m_Classes = new WeakHashMap<String, SootClass>();  
    m_Bodies = new WeakHashMap<String, Body>();
    m_SootLoadedClasses = new LinkedHashMap<String, Integer>();
    m_CopiedClasses = new ArrayList<String>();
    m_GetClassClasses = new ArrayList<String>();
    m_Dependencies = new LinkedList<String>();
    m_AddedClasses = new ArrayList<String>();
    m_Fixups = new LinkedList<String>();
    IgnorePackages list = new IgnorePackages();
    m_ignorePackages = list.get();
    loadClasses();
  }
  
  public void init(String jar_filename) throws Exception {
    File cp = new File(Constants.JAR_CONTENTS_FOLDER);
    System.setProperty("soot.class.path", cp.getAbsolutePath());
    
    ExtractJavaRuntime extractor = new ExtractJavaRuntime();
    extractor.extract(Constants.JAR_CONTENTS_FOLDER);

    RootbeerCompilerSetup setup = new RootbeerCompilerSetup();
    List<String> app_class_filenames = setup.setup(jar_filename);    
    
    m_ApplicationClasses = new ArrayList<String>();
    for(String app_class_filename : app_class_filenames){
      String class_name = filenameToClass(app_class_filename);
      if(shouldIgnore(class_name) == false){
        m_ApplicationClasses.add(class_name);
      }
    }
    
    reinit();
  }
  
  private boolean shouldIgnore(String class_name){
    for(String ignore : m_ignorePackages){
      if(class_name.startsWith(ignore)){
        return true;
      }
    }
    return false;
  }
  
  public List<String> getApplicationClasses(){
    return m_ApplicationClasses;
  }
  
  public SootClass getClass(String name){
    if(m_Classes.containsKey(name))
      return m_Classes.get(name);
    SootClass cls = loadClass(name);
    if(cls == null){
      SootClass ret = new SootClass(name);
      ret.setPhantom(true);
      return ret;
    }
    cls.setResolvingLevel(SootClass.BODIES);
    m_Classes.put(name, cls);
    Scene.v().addClass(cls);
    while(!m_Dependencies.isEmpty()){
      String dep_cls = m_Dependencies.get(0);
      m_Dependencies.remove(0);
      if(!m_Classes.containsKey(dep_cls)){
        SootClass cls2 = loadClass(dep_cls);
        cls2.setResolvingLevel(SootClass.BODIES);
        m_Classes.put(dep_cls, cls2);
        Scene.v().addClass(cls2);
      }
    }
    while(!m_Fixups.isEmpty()){
      String fixup = m_Fixups.get(0);
      m_Fixups.remove(0);
      fixupClass(fixup);
    }
    return cls;
  }
  
  public Body getBody(SootMethod method){
    if(m_Bodies.containsKey(method.getSignature())){
      return m_Bodies.get(method.getSignature());
    }
    Body ret = null;
    try {
      ret = method.getActiveBody();
    } catch(Exception ex){
      if(method.getDeclaringClass().resolvingLevel() != SootClass.BODIES){
        throw new RaiseResolvingLevelException(method.getDeclaringClass().getName());
      }
      try {
        ret = method.retrieveActiveBody();
      } catch(RuntimeException ex2){
        System.out.println("Failed to load body for method: "+method.getSignature());
        ret = null;
      }
    }    
    m_Bodies.put(method.getSignature(), ret);
    return ret;
  }
  
  public void sootLoadClass(String name, int level){
    m_SootLoadedClasses.put(name, level);
  }
  
  private String extractClassName(Type type){
    RefType ref_type;
    if(type instanceof RefType){
      ref_type = (RefType) type;
      return ref_type.getClassName();
    } else if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      return extractClassName(array_type.baseType);
    } 
    return "";
  }
  
  private SootClass loadClass(String name) {
    List<String> filenames = getFilenames(name);
    for(String filename : filenames){
      try {
        File file = new File(filename);
        InputStream is = null;
        if(file.exists()){
          is = new FileInputStream(filename);  
        } else {
          is = RootbeerScene.class.getResourceAsStream(filename);
        }
        if(is != null){
          if(isFilename(name)){
            name = filenameToClass(name);
          }
          name = removeExtraPath(name);
          return doLoadClass(is, name);
        }
      } catch(Exception ex){
        continue;
      }
    }
    throw new RuntimeException("cannot find class: "+name);
  }
  
  private List<String> getFilenames(String name){
    List<String> ret = new ArrayList<String>();
    List<String> prefixes = new ArrayList<String>();
    prefixes.add("jar-contents"+File.separator);
    prefixes.add("src"+File.separator);

    if(isFilename(name)){
      ret.add(name);
    } else {
      for(String prefix : prefixes){
        String filename = classToFilename(name, prefix);
        ret.add(filename);
      }
    }
    return ret;
  }
  
  private SootClass doLoadClass(InputStream is, String name){
   try {
      SootClass cls = new SootClass(name);
      m_Fixups.add(name);
      CoffiClassSource source = new CoffiClassSource(name, is);
      Dependencies deps = source.resolve(cls);
      cls.setApplicationClass();
      Set types = deps.typesToSignature;
      Iterator<String> iter = types.iterator();
      while(iter.hasNext()){
        Object next = iter.next();
        String class_name;
        if(next instanceof String)
          class_name = (String) next;
        else if(next instanceof Type){
          class_name = extractClassName((Type) next);
          if(class_name.equals(""))
            continue;
        } else
          continue;
        if(!m_Dependencies.contains(class_name) && !m_Classes.containsKey(class_name))
          m_Dependencies.add(class_name);
      }
      return cls;
    } catch(Exception ex){
      throw new RuntimeException(ex);
    } finally {
      try {
        if(is != null)
          is.close();
      } catch(Exception ex){
        ex.printStackTrace();
      }
    } 
  }
    
  private void fixupClass(String cls){
    SootClass soot_class = RootbeerScene.v().getClass(cls);
    LinkedList<SootClass> queue = new LinkedList<SootClass>();
    queue.addAll( soot_class.getInterfaces() );
    soot_class.getInterfaces().clear();
    while( !queue.isEmpty() ) {
      SootClass iface = queue.removeFirst();
      iface = RootbeerScene.v().getClass(iface.getName());
      soot_class.addInterface(iface);
    }
    if(soot_class.hasSuperclass()){
      SootClass super_class = soot_class.getSuperclass();
      soot_class.setSuperclass(RootbeerScene.v().getClass(super_class.getName()));
    }
    if(soot_class.hasOuterClass()){
      SootClass outer_class = soot_class.getOuterClass();
      soot_class.setOuterClass(RootbeerScene.v().getClass(outer_class.getName()));
    } 
    if(soot_class.hasSuperclass()){
      fixupClass(soot_class.getSuperclass().getName());
    }
    if(soot_class.hasOuterClass()){
      fixupClass(soot_class.getOuterClass().getName());
    }
  }
  
  private String removeExtraPath(String name){
    String[] tokens = name.split("jar-contents\\.");
    return tokens[tokens.length-1];
  }

  public String classToFilename(String name) {
    return classToFilename(name, "jar-contents"+File.separator);
  }
  
  private String classToFilename(String name, String prefix){
    name = name.replace(".", File.separator);
    return prefix+name+".class";
  }

  private boolean isFilename(String name) {
    if(name.endsWith(".class"))
      return true;
    return false;
  }

  private String filenameToClass(String name) {
    String sep = "/";
    if(name.startsWith(sep))
      name = name.substring(sep.length());
    name = name.replace(".class", "");
    name = name.replace(sep, ".");
    return name;
  }

  public void reinit() {
    G.reset();
    m_Bodies.clear();
    m_Classes.clear();
    m_CopiedClasses.clear();
    
    Iterator<String> iter = m_SootLoadedClasses.keySet().iterator();
    while(iter.hasNext()){
      String name = iter.next();
      int level = m_SootLoadedClasses.get(name);
      if(level == 0)
        level++;
      Scene.v().addBasicClass(name, level);
    }
    Scene.v().loadNecessaryClasses();
    
    for(String cls : m_GetClassClasses){
      getClass(cls);
    }
  }

  public void sootElevateClasses() {
    Map<String, Integer> new_classes = new LinkedHashMap<String, Integer>();
    Iterator<String> iter = m_SootLoadedClasses.keySet().iterator();
    while(iter.hasNext()){
      String name = iter.next();
      new_classes.put(name, SootClass.BODIES);
    }
    m_SootLoadedClasses = new_classes;
    reinit();
  }

  public void addCopiedClass(SootClass soot_class) {
    if(m_CopiedClasses.contains(soot_class.getName()))
      return;
    
    m_CopiedClasses.add(soot_class.getName());
    try {
      RootbeerScene.v().addClass(soot_class);
    } catch(RuntimeException ex){
      //this happens when the class is already managed.
      //just ignore it.
    }
  }
  
  public void writeCopiedClasses(){
    for(String classname : m_Classes.keySet()){
      try {
        SootClass cls = getClass(classname);
        JimpleWriter writer = new JimpleWriter();
        writer.write("copied", cls);
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }

  void addGetClass(String cls) {
    m_GetClassClasses.add(cls);
  }

  public void addClass(SootClass ret) {
    Scene.v().addClass(ret);
    m_Classes.put(ret.getName(), ret);
    m_AddedClasses.add(ret.getName());
  }

  public Iterator<SootClass> getClasses() {
    Iterator<String> iter = m_Classes.keySet().iterator();
    List<SootClass> ret = new ArrayList<SootClass>();
    while(iter.hasNext()){
      String key = iter.next();
      ret.add(m_Classes.get(key));
    }
    return ret.iterator();
  }

  private void loadClasses() {
    m_SootLoadedClasses.put("java.lang.Object", SootClass.HIERARCHY);
  }

  public List<String> getAddedClasses() {
    return m_AddedClasses;
  }

  public void setBody(SootMethod method, Body body) {
    m_Bodies.put(method.getSignature(), body);
  }

  public void setReachableMethods(List<String> reachables) {
    m_reachables = reachables;
  }
  
  public List<String> getReachableMethods(){
    return m_reachables;
  }
}
