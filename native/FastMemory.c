#include "edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory.h"
#include <cuda.h>

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    readByte
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doReadByte
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  jbyte ret = charHostMemory[ptr];
  return ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    readBoolean
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doReadBoolean
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  jboolean ret = charHostMemory[ptr];
  return ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    readShort
 * Signature: ()S
 */
JNIEXPORT jshort JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doReadShort
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  short * shortHostMemory = (short *) cpu_base;
  jshort ret = shortHostMemory[ptr / 2];

  return ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    readInt
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doReadInt
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  int * intHostMemory = (int *) cpu_base;
  jint ret = intHostMemory[ptr / 4];

  return ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    readFloat
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doReadFloat
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  float * floatHostMemory = (float *) cpu_base;
  jfloat ret = floatHostMemory[ptr / 4];

  return ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    readDouble
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doReadDouble
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  double * doubleHostMemory = (double *) cpu_base;
  jdouble ret = doubleHostMemory[ptr / 8];

  return ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    readLong
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doReadLong
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  jlong * longHostMemory = (jlong *) cpu_base;
  jlong ret = longHostMemory[ptr / 8];

  return ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    writeByte
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doWriteByte
  (JNIEnv *env, jobject this_obj, jlong ptr, jbyte value, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  charHostMemory[ptr] = value;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    writeBoolean
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doWriteBoolean
  (JNIEnv *env, jobject this_obj, jlong ptr, jboolean value, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  charHostMemory[ptr] = value;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    writeShort
 * Signature: (S)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doWriteShort
  (JNIEnv *env, jobject this_obj, jlong ptr, jshort value, jlong cpu_base){

  short * shortHostMemory = (short *) cpu_base;
  shortHostMemory[ptr / 2] = value;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    writeInt
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doWriteInt
  (JNIEnv *env, jobject this_obj, jlong ptr, jint value, jlong cpu_base){

  int * intHostMemory = (int *) cpu_base;
  intHostMemory[ptr / 4] = value;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    writeFloat
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doWriteFloat
  (JNIEnv *env, jobject this_obj, jlong ptr, jfloat value, jlong cpu_base){

  float * floatHostMemory = (float *) cpu_base;
  floatHostMemory[ptr / 4] = value;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    writeDouble
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doWriteDouble
  (JNIEnv *env, jobject this_obj, jlong ptr, jdouble value, jlong cpu_base){

  double * doubleHostMemory = (double *) cpu_base;
  doubleHostMemory[ptr / 8] = value;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory
 * Method:    writeLong
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_FastMemory_doWriteLong
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong value, jlong cpu_base){

  jlong * longHostMemory = (jlong *) cpu_base;
  longHostMemory[ptr / 8] = value;
}
