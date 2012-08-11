#include "edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles.h"
#include <cuda.h>

static jint * longHostMemory;
static jint ptr;

static void * hostMemory;
static CUdeviceptr deviceMemory;

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles
 * Method:    setup
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles_setup
  (JNIEnv *env, jobject obj, jlong cpu_addr, jlong gpu_addr){

  hostMemory = (void *) cpu_addr;
  longHostMemory = (jint *) cpu_addr;
  deviceMemory = (CUdeviceptr) gpu_addr;
  ptr = 0;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles
 * Method:    resetPointer
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles_resetPointer
  (JNIEnv *env, jobject obj){
  ptr = 0;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles
 * Method:    writeLong
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles_writeLong
  (JNIEnv *env, jobject obj, jlong value){
  
  jint int_value;
  
  value = value >> 4;
  int_value = (jint) value;
  longHostMemory[ptr] = int_value;
  ++ptr;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles
 * Method:    readLong
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_Handles_readLong
  (JNIEnv *env, jobject obj){

  jint ret = longHostMemory[ptr];
  jlong long_ret = ret;
  long_ret = long_ret << 4;
  ++ptr;
  return long_ret;
}

