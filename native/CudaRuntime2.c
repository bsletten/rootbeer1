#include "edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2.h"

#include <cuda.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static CUdevice cuDevice;
static CUmodule cuModule;
static CUfunction cuFunction;
static CUcontext cuContext;

static void * toSpace;
static void * textureMemory;
static void * handlesMemory;
static void * exceptionsMemory;

static CUdeviceptr gcInfoSpace;
static CUdeviceptr gpuToSpace;
static CUdeviceptr gpuTexture;
static CUdeviceptr gpuHandlesMemory;
static CUdeviceptr gpuExceptionsMemory;
static CUdeviceptr gpuHeapEndPtr;
static CUdeviceptr gpuBufferSize;
static CUtexref    cache;

static jclass thisRefClass;

static jlong heapEndPtr;
static jlong bufferSize;
static int maxGridDim;
static int numMultiProcessors;

static int textureMemSize;
static size_t gc_space_size;

size_t memSize(){
  size_t ret;
  int status;
  
  status = cuDeviceTotalMem(&ret, cuDevice);  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuDeviceTotalMem %d\n", status);
  }

  return ret;
}

void setLongField(JNIEnv *env, jobject obj, const char * name, jlong value){

  jfieldID fid = (*env)->GetFieldID(env, thisRefClass, name, "J");
  (*env)->SetLongField(env, obj, fid, value);
}

void getBestDevice(){
  int num_devices;
  int status;
  int i;
  CUdevice temp_device;
  int curr_multiprocessors;
  int max_multiprocessors = -1;
  int max_i = -1;
  
  status = cuDeviceGetCount(&num_devices);   
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuDeviceGetCount\n");
  }
  for(i = 0; i < num_devices; ++i){
    status = cuDeviceGet(&temp_device, i);
    if (CUDA_SUCCESS != status) 
    {
      printf("error in cuDeviceGet\n");
    }
    status = cuDeviceGetAttribute(&curr_multiprocessors, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, temp_device);    
    if (CUDA_SUCCESS != status) 
    {
      printf("error in cuDeviceGetAttribute CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT\n");
    }
    if(curr_multiprocessors > max_multiprocessors)
    {
      max_multiprocessors = curr_multiprocessors;
      max_i = i;
    }
  }

  status = cuDeviceGet(&cuDevice, max_i); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuDeviceGetName\n");
  }
  status = cuDeviceGetAttribute(&maxGridDim, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X, cuDevice);    
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuDeviceGetAttribute CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X\n");
  }
  numMultiProcessors = max_multiprocessors;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    setup
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_setup
  (JNIEnv *env, jobject this_ref, jint max_blocks_per_proc, jint max_threads_per_block)
{
  int status;
  jint num_blocks;
  int deviceCount = 0;
  size_t to_space_size;
  size_t free_space = 500L*1024L*1024L;
  textureMemSize = 1;
  
  status = cuInit(0);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuInit\n");
  }

  to_space_size = memSize();
  
  status = cuDeviceGetCount(&deviceCount);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuDeviceGet\n");
  }

  getBestDevice();
  num_blocks = numMultiProcessors * max_threads_per_block * max_blocks_per_proc;
  
  status = cuCtxCreate(&cuContext, CU_CTX_MAP_HOST, cuDevice);  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuCtxCreate %d\n", status);
  }
  
  gc_space_size = 1024;
  to_space_size -= (num_blocks * sizeof(jlong));
  to_space_size -= (num_blocks * sizeof(jlong));
  to_space_size -= gc_space_size;
  to_space_size -= free_space;
  //to_space_size -= textureMemSize;
  bufferSize = to_space_size;
  status = cuMemHostAlloc(&toSpace, to_space_size, 0);  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemHostAlloc toSpace %d\n", status);
  }

  status = cuMemAlloc(&gpuToSpace, to_space_size);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc toSpace %d\n", status);
  }	
/*
  status = cuMemHostAlloc(&textureMemory, textureMemSize, 0);  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemHostAlloc textureMemory %d\n", status);
  }

  status = cuMemAlloc(&gpuTexture, textureMemSize);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc gpuTexture %d\n", status);
  }
*/
  status = cuMemHostAlloc(&handlesMemory, num_blocks * sizeof(jlong), CU_MEMHOSTALLOC_WRITECOMBINED); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemHostAlloc handlesMemory %d\n", status);
  }

  status = cuMemAlloc(&gpuHandlesMemory, num_blocks * sizeof(jlong)); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc handlesMemory %d\n", status);
  }	

  status = cuMemHostAlloc(&exceptionsMemory, num_blocks * sizeof(jlong), 0); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemHostAlloc exceptionsMemory %d\n", status);
  }

  status = cuMemAlloc(&gpuExceptionsMemory, num_blocks * sizeof(jlong)); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc gpuExceptionsMemory %d\n", status);
  }	

  status = cuMemAlloc(&gcInfoSpace, gc_space_size);  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc gcInfoSpace %d\n", status);
  }	

  status = cuMemAlloc(&gpuHeapEndPtr, 8);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc heapEndPtr %d\n", status);
  }	

  status = cuMemAlloc(&gpuBufferSize, 8);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc bufferSizeMem %d\n", status);
  }	

  thisRefClass = (*env)->GetObjectClass(env, this_ref);
  setLongField(env, this_ref, "m_ToSpaceAddr", (jlong) toSpace);
  setLongField(env, this_ref, "m_GpuToSpaceAddr", (jlong) gpuToSpace);
  setLongField(env, this_ref, "m_TextureAddr", (jlong) textureMemory);
  setLongField(env, this_ref, "m_GpuTextureAddr", (jlong) gpuTexture);
  setLongField(env, this_ref, "m_HandlesAddr", (jlong) handlesMemory);
  setLongField(env, this_ref, "m_GpuHandlesAddr", (jlong) gpuHandlesMemory);
  setLongField(env, this_ref, "m_ExceptionsHandlesAddr", (jlong) exceptionsMemory);
  setLongField(env, this_ref, "m_GpuExceptionsHandlesAddr", (jlong) gpuExceptionsMemory);
  setLongField(env, this_ref, "m_ToSpaceSize", (jlong) bufferSize);
  setLongField(env, this_ref, "m_MaxGridDim", (jlong) maxGridDim);
  setLongField(env, this_ref, "m_NumMultiProcessors", (jlong) numMultiProcessors);
}

void * readCubinFile(const char * filename){

  int i;
  jlong size;
  char * ret;
  int block_size;
  int num_blocks;
  int leftover;
  char * dest;
  
  FILE * file = fopen(filename, "r");
  fseek(file, 0, SEEK_END);
  size = ftell(file);
  fseek(file, 0, SEEK_SET);

  ret = (char *) malloc(size);
  block_size = 4096;
  num_blocks = (int) (size / block_size);
  leftover = (int) (size % block_size);

  dest = ret;
  for(i = 0; i < num_blocks; ++i){
    fread(dest, 1, block_size, file);
    dest += block_size;
  }
  if(leftover != 0){
    fread(dest, 1, leftover, file);
  }

  fclose(file);
  return (void *) ret;
}

void * readCubinFileFromBuffers(JNIEnv *env, jobject buffers, jint size, jint total_size){
  int i, j;
  int dest_offset = 0;
  int len;
  char * data;
  char * ret = (char *) malloc(total_size);

  jclass cls = (*env)->GetObjectClass(env, buffers);
  jmethodID mid = (*env)->GetMethodID(env, cls, "get", "(I)Ljava/lang/Object;");
  for(i = 0; i < size; ++i){
    jobject buffer = (*env)->CallObjectMethod(env, buffers, mid, i);
    jbyteArray * arr = (jbyteArray*) &buffer;
    len = (*env)->GetArrayLength(env, *arr);
    data = (*env)->GetByteArrayElements(env, *arr, NULL);
    memcpy((void *) (ret + dest_offset), (void *) data, len);
    dest_offset += len;
    (*env)->ReleaseByteArrayElements(env, *arr, data, 0);
  }

  return (void *) ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    loadFunction
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_loadFunction
  (JNIEnv *env, jobject this_obj, jlong heap_end_ptr, jobject buffers, jint size, 
   jint total_size, jint num_blocks){

  void * cubin_file;
  int offset;
  CUresult status;
  heapEndPtr = heap_end_ptr;
  
  //void * cubin_file = readCubinFile("code_file.cubin");
  cubin_file = readCubinFileFromBuffers(env, buffers, size, total_size);
  status = cuModuleLoadData(&cuModule, cubin_file);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuModuleLoad %d\n", status);
  }
  free(cubin_file);

  status = cuModuleGetFunction(&cuFunction, cuModule, "_Z5entryPcS_PiPxS1_S0_i"); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuModuleGetFunction %d\n", status);
  }

  status = cuFuncSetCacheConfig(cuFunction, CU_FUNC_CACHE_PREFER_L1);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuFuncSetCacheConfig %d\n", status);
  }

  status = cuParamSetSize(cuFunction, (6 * sizeof(CUdeviceptr) + sizeof(int))); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetSize %d\n", status);
  }

  offset = 0;
  status = cuParamSetv(cuFunction, offset, (void *) &gcInfoSpace, sizeof(CUdeviceptr)); 
  offset += sizeof(CUdeviceptr);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetv gcInfoSpace %d\n", status);
  }

  status = cuParamSetv(cuFunction, offset, (void *) &gpuToSpace, sizeof(CUdeviceptr)); 
  offset += sizeof(CUdeviceptr);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetv gpuToSpace %d\n", status);
  }

  status = cuParamSetv(cuFunction, offset, (void *) &gpuHandlesMemory, sizeof(CUdeviceptr)); 
  offset += sizeof(CUdeviceptr);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetv gpuHandlesMemory %d\n", status);
  }

  status = cuParamSetv(cuFunction, offset, (void *) &gpuHeapEndPtr, sizeof(CUdeviceptr)); 
  offset += sizeof(CUdeviceptr);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetv heapEndPtr %d\n", status);
  }

  status = cuParamSetv(cuFunction, offset, (void *) &gpuBufferSize, sizeof(CUdeviceptr)); 
  offset += sizeof(CUdeviceptr); 
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetv bufferSizeMem %d\n", status);
  }

  status = cuParamSetv(cuFunction, offset, (void *) &gpuExceptionsMemory, sizeof(CUdeviceptr)); 
  offset += sizeof(CUdeviceptr);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetv gpuExceptionsMemory %d\n", status);
  }

  status = cuParamSeti(cuFunction, offset, num_blocks); 
  offset += sizeof(int);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuParamSetv gpuExceptionsMemory %d\n", status);
  }
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    runBlocks
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_runBlocks
  (JNIEnv *env, jobject this_obj, jint num_blocks, jint block_shape, jint grid_shape){

  CUresult status;
  jlong * infoSpace = (jlong *) malloc(gc_space_size);
  infoSpace[1] = heapEndPtr;
  cuMemcpyHtoD(gcInfoSpace, infoSpace, gc_space_size);
  cuMemcpyHtoD(gpuToSpace, toSpace, heapEndPtr);
  //cuMemcpyHtoD(gpuTexture, textureMemory, textureMemSize);
  cuMemcpyHtoD(gpuHandlesMemory, handlesMemory, num_blocks * sizeof(jlong));
  cuMemcpyHtoD(gpuHeapEndPtr, &heapEndPtr, sizeof(jlong));
  cuMemcpyHtoD(gpuBufferSize, &bufferSize, sizeof(jlong));
  
/*
  status = cuModuleGetTexRef(&cache, cuModule, "m_Cache");  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuModuleGetTexRef %d\n", status);
  }

  status = cuTexRefSetAddress(0, cache, gpuTexture, textureMemSize);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuTextRefSetAddress %d\n", status);
  }
*/

  status = cuFuncSetBlockShape(cuFunction, block_shape, 1, 1);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuFuncSetBlockShape %d\n", status);
    return (jint) status;
  }

  status = cuLaunchGrid(cuFunction, grid_shape, 1);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuLaunchGrid %d\n", status);
    fflush(stdout);
    return (jint) status;
  }

  status = cuCtxSynchronize();  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuCtxSynchronize %d\n", status);
    return (jint) status;
  }

  cuMemcpyDtoH(infoSpace, gcInfoSpace, gc_space_size);
  heapEndPtr = infoSpace[1];
  cuMemcpyDtoH(toSpace, gpuToSpace, heapEndPtr);
  cuMemcpyDtoH(exceptionsMemory, gpuExceptionsMemory, num_blocks * sizeof(jlong));
  free(infoSpace);
  
  return 0;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    unload
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_unload
  (JNIEnv *env, jobject this_obj){

  cuModuleUnload(cuModule);
  cuFunction = (CUfunction) 0;  
}
