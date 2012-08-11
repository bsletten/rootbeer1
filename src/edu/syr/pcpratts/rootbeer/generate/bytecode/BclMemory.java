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

import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.Value;
import soot.VoidType;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;

public class BclMemory {
  private final BytecodeLanguage mBcl;
  private final Local mMem;

  public BclMemory(BytecodeLanguage bcl, Local mem){
    mBcl = bcl;
    mMem = mem;
  }

  public void writeByte(byte value){
    writeByte(IntConstant.v(value));
  }

  public void writeByte(Value value){
    mBcl.pushMethod(mBcl.getTypeString(mMem), "writeByte", VoidType.v(), ByteType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  void writeShort(Value value) {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "writeShort", VoidType.v(), ShortType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  private void writeChar(Value value) {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "writeChar", VoidType.v(), CharType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  void writeInt(Value value) {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "writeInt", VoidType.v(), IntType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  void writeInt(int size) {
    writeInt(IntConstant.v(size));
  }

  void writeFloat(Value value){
    mBcl.pushMethod(mBcl.getTypeString(mMem), "writeFloat", VoidType.v(), FloatType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  void writeDouble(Value value){
    mBcl.pushMethod(mBcl.getTypeString(mMem), "writeDouble", VoidType.v(), DoubleType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  void writeLong(Value value){
    mBcl.pushMethod(mBcl.getTypeString(mMem), "writeLong", VoidType.v(), LongType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  void writeVar(Local curr) {
    Type type = curr.getType();
    if(type instanceof ByteType){
      writeByte(curr);
    } else if(type instanceof ShortType){
      writeShort(curr);
    } else if(type instanceof CharType){
      writeChar(curr);
    } else if(type instanceof IntType){
      writeInt(curr);
    } else if(type instanceof FloatType){
      writeFloat(curr);
    } else if(type instanceof DoubleType){
      writeDouble(curr);
    } else if(type instanceof LongType){
      writeLong(curr);
    }
  }

  void pushAddress() {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "pushAddress", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);
  }

  void incrementAddress(int size) {
    incrementAddress(IntConstant.v(size));
  }

  void incrementAddress(Value value) {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "incrementAddress", VoidType.v(), IntType.v());
    mBcl.invokeMethodNoRet(mMem, value);
  }

  Local getPointer() {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "getPointer", LongType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  void popAddress() {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "popAddress", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);
  }

  void setAddress(Value address) {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "setAddress", VoidType.v(), LongType.v());
    mBcl.invokeMethodNoRet(mMem, address);
  }

  public void useInstancePointer(){
    mBcl.pushMethod(mBcl.getTypeString(mMem), "useInstancePointer", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);
  }
  
  public void useStaticPointer(){
    mBcl.pushMethod(mBcl.getTypeString(mMem), "useStaticPointer", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);    
  }
  
  Local readByte() {
    mBcl.pushMethod(mMem, "readByte", ByteType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  Local readShort(){
    mBcl.pushMethod(mMem, "readShort", ShortType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  Local readChar(){
    mBcl.pushMethod(mMem, "readChar", CharType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  Local readInt() {
    mBcl.pushMethod(mMem, "readInt", IntType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  public Local readRef() {
    mBcl.pushMethod(mMem, "readRef", LongType.v());
    return mBcl.invokeMethodRet(mMem);
  }
  
  public void writeRef(Value ref) {
    mBcl.pushMethod(mMem, "writeRef", VoidType.v(), LongType.v());
    mBcl.invokeMethodNoRet(mMem, ref);
  }
  
  Local readFloat(){
    mBcl.pushMethod(mMem, "readFloat", FloatType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  Local readDouble(){
    mBcl.pushMethod(mMem, "readDouble", DoubleType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  Local readLong(){
    mBcl.pushMethod(mMem, "readLong", LongType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  Local readVar(Type type) {
    if(type instanceof ByteType){
      return readByte();
    } else if(type instanceof ShortType){
      return readShort();
    } else if(type instanceof CharType){
      return readChar();
    } else if(type instanceof IntType){
      return readInt();
    } else if(type instanceof FloatType){
      return readFloat();
    } else if(type instanceof DoubleType){
      return readDouble();
    } else if(type instanceof LongType){
      return readLong();
    }
    throw new RuntimeException("How do we handle this case?");
  }

  void startIntegerList() {
    mBcl.pushMethod(mMem, "startIntegerList", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);
  }

  void addIntegerToList(Local array_elements) {
    mBcl.pushMethod(mMem, "addIntegerToList", VoidType.v(), LongType.v());
    mBcl.invokeMethodNoRet(mMem, array_elements);
  }

  void endIntegerList() {
    mBcl.pushMethod(mMem, "endIntegerList", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);
  }

  Local malloc() {
    mBcl.pushMethod(mMem, "malloc", LongType.v());
    return mBcl.invokeMethodRet(mMem);
  }

  void finishReading() {
    mBcl.pushMethod(mMem, "finishReading", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);
  }

  public void align() {
    mBcl.pushMethod(mMem, "align", VoidType.v());
    mBcl.invokeMethodNoRet(mMem);
  }

  public void readIntArray(Value ret, Value size) {
    mBcl.pushMethod(mMem, "readIntArray", VoidType.v(), ret.getType(), size.getType());
    mBcl.invokeMethodNoRet(mMem, ret, size);
  }

  public void mallocWithSize(Value size) {
    mBcl.pushMethod(mBcl.getTypeString(mMem), "mallocWithSize", LongType.v(), IntType.v());
    mBcl.invokeMethodNoRet(mMem, size);
  }



}
