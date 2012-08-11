#include "edu_syr_pcpratts_rootbeer_runtime_memory_BatchIntReader.h"
#include <stdlib.h>

jbyte * raw_elements;  
jint * int_elements;

JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime_memory_BatchIntReader_malloc
  (JNIEnv *env, jobject this_obj, jint size){
  
  raw_elements = (jbyte *) malloc(size);
  int_elements = (jint *) raw_elements;
}

JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime_memory_BatchIntReader_read
  (JNIEnv *env, jobject this_obj, jbyteArray to_convert, jint size, jintArray output){

  (*env)->GetByteArrayRegion(env, to_convert, 0, size, raw_elements);
  (*env)->SetIntArrayRegion(env, output, 0, size/4, int_elements);

}
