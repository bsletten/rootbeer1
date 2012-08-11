#include "edu_syr_pcpratts_rootbeer_runtime_nativecpu_NativeCpuDevice.h"
#ifdef _WIN32

#else
#include <dlfcn.h>
#endif
#include <stdlib.h>
#include <stdio.h>

jbyteArray array_get(JNIEnv * env, jobject array, int index){
   jmethodID mid;
   jclass list_interface = (*env)->FindClass(env, "java/util/List");
   mid = (*env)->GetMethodID(env, list_interface, "get", "(I)Ljava/lang/Object;");
   return (*env)->CallObjectMethod(env, array, mid, index);
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime_nativecpu_NativeCpuDevice
 * Method:    runOnCpu
 * Signature: (Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime_nativecpu_NativeCpuDevice_runOnCpu
  (JNIEnv * env, jobject this_ptr, jobject to_space_array, jint to_space_count, 
   jbyteArray handles, jbyteArray heap_end_ptr, jbyteArray gc_info, jbyteArray exceptions, 
   jint num_threads, jstring lib_name){

  int i;
  jbyte * nhandles = (*env)->GetByteArrayElements(env, handles, JNI_FALSE);
  jbyte * nheap_end_ptr = (*env)->GetByteArrayElements(env, heap_end_ptr, JNI_FALSE);
  jbyte * ngc_info = (*env)->GetByteArrayElements(env, gc_info, JNI_FALSE);
  jbyte * nexceptions = (*env)->GetByteArrayElements(env, exceptions, JNI_FALSE);

  const char *str = (*env)->GetStringUTFChars(env, lib_name, NULL);
  void * lib_handle = dlopen(str, RTLD_LAZY);
  (*env)->ReleaseStringUTFChars(env, lib_name, str);

  jlong * to_space = (jlong *) malloc(sizeof(jlong *) * to_space_count);
  for(i = 0; i < to_space_count; ++i){
    jbyteArray curr_to_space = array_get(env, to_space_array, i);
    to_space[i] = (jlong) (*env)->GetByteArrayElements(env, curr_to_space, JNI_FALSE);
  }

  void (*entry)(char * gc_info_space, jlong * to_space, jlong * handles, 
    jlong * to_space_free_ptr, jlong * exceptions, jlong space_size, int num_threads);

  entry = dlsym(lib_handle, "entry");
  (*entry)((char *) ngc_info, to_space, (jlong *) nhandles, (jlong *) nheap_end_ptr, (jlong *) nexceptions, 100*1024*1024, num_threads);  

  dlclose(lib_handle);

  for(i = 0; i < to_space_count; ++i){
    jbyteArray curr_to_space = array_get(env, to_space_array, i);
    (*env)->ReleaseByteArrayElements(env, curr_to_space, (jbyte *) to_space[i], 0);
  }

  (*env)->ReleaseByteArrayElements(env, handles, nhandles, 0);
  (*env)->ReleaseByteArrayElements(env, heap_end_ptr, nheap_end_ptr, 0);
  (*env)->ReleaseByteArrayElements(env, gc_info, ngc_info, 0);
  (*env)->ReleaseByteArrayElements(env, exceptions, nexceptions, 0);

  free(to_space);
}
