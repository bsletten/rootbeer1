#include "edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2.h"
#include <cuda.h>

static void * hostMemory;
static CUdeviceptr deviceMemory;

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_Cuda2DeviceMemory
 * Method:    read
 * Signature: ([BJI)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_Cuda2DeviceMemory_read
  (JNIEnv *env, jobject obj, jbyteArray arr, jlong ptr, jint size){

  (*env)->SetByteArrayRegion(env, arr, ptr, size, hostMemory);
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_Cuda2DeviceMemory
 * Method:    write
 * Signature: ([BJI)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_Cuda2DeviceMemory_write
  (JNIEnv *env, jobject obj, jbyteArray arr, jlong ptr, jint size){

  (*env)->GetByteArrayRegion(env, arr, ptr, size, hostMemory);
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_Cuda2DeviceMemory
 * Method:    setup
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_Cuda2DeviceMemory_setup
  (JNIEnv *env, jobject obj, jlong cpu_addr, jlong gpu_addr){

  hostMemory = (void *) cpu_addr;
  deviceMemory = (CUdeviceptr) gpu_addr;
}

