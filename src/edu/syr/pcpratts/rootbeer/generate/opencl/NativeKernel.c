#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>

pthread_key_t threadIdKey = 0;

int getThreadId(){
  return (int) pthread_getspecific(threadIdKey);
}

char *
edu_syr_pcpratts_gc_deref(char * gc_info, int handle){
  long long lhandle = handle;
  lhandle = lhandle << 4;
  long long * to_space = edu_syr_pcpratts_gc_get_to_space_address(gc_info);
  long space_size = edu_syr_pcpratts_getlong(gc_info, 16);
  long long array = lhandle / space_size;
  long long offset = lhandle % space_size;

  long long address = to_space[array];
  char * data_arr = (char *) address;
  return &data_arr[offset];
}

int
edu_syr_pcpratts_gc_malloc(char * gc_info, long long size){
  long long * addr = (long long *) (gc_info + TO_SPACE_FREE_POINTER_OFFSET);
  long long space_size = edu_syr_pcpratts_getlong(gc_info, 16);
  size += 8;
  long long ret;
  while(true){
    ret = atom_add(addr, (long) size);
    int mod = ret % 8;
    if(mod != 0)
      ret += (8 - mod);

    long long start_array = ret / space_size;
    long long end_array = (ret + size) / space_size;

    if(start_array != end_array){
      continue;
    }

    ret = ret >> 4;
    return (int) ret;
  }
}

char *
edu_syr_pcpratts_gc_init(char * gc_info_space,
                         long long * to_space,
                         long long to_space_free_ptr,
                         long long space_size){

  edu_syr_pcpratts_setlong(gc_info_space, 0, (long long) to_space);
  edu_syr_pcpratts_setlong(gc_info_space, 8, to_space_free_ptr);
  edu_syr_pcpratts_setlong(gc_info_space, 16, space_size);
    
  return (char *) gc_info_space;
}

char * global_gc_info;
long long * global_handles;
int thread_id;
int global_num_threads;
long long * global_exceptions;

pthread_mutex_t thread_id_mutex;

static void * run(void * data){

  while(true){
    int index;
    pthread_mutex_lock(&thread_id_mutex);
    index = thread_id;
    ++thread_id;
    pthread_mutex_unlock(&thread_id_mutex);
    
    if(index >= global_num_threads)
      break;

    pthread_setspecific(threadIdKey, (void *) index);
    long long lhandle = global_handles[index];
    lhandle = lhandle >> 4;
    int handle = (int) lhandle;
    long long exception = 0;
    %%invoke_run%%(global_gc_info, handle, &exception);
    global_exceptions[index] = exception;
  }
  pthread_exit(NULL);
}

void entry(char * gc_info_space,
           long long * to_space,
           long long * handles,
           long long * to_space_free_ptr,
           long long * exceptions,
           long long space_size,
           int num_threads){
  
  pthread_attr_t attr;
  int i;
  int rc;
  char * gc_info = edu_syr_pcpratts_gc_init(gc_info_space, to_space,
    *to_space_free_ptr, space_size);
  global_num_threads = num_threads;
  thread_id = 0;
  global_gc_info = gc_info;
  global_handles = handles;
  global_exceptions = exceptions;
  
  pthread_mutex_init(&thread_id_mutex, NULL);
  pthread_mutex_init(&atom_add_mutex, NULL);
  int create_ret = pthread_key_create(&threadIdKey, NULL);
   
  pthread_attr_init(&attr);
  pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);

  int num_cores = 4;
  pthread_t ** threads = (pthread_t **) malloc(sizeof(pthread_t *)*num_cores);

  for(i = 0; i < num_cores; ++i){
    pthread_t * thread = (pthread_t *) malloc(sizeof(pthread_t));
    pthread_create(thread, &attr, &run, NULL);
    threads[i] = thread;
  }

  for(i = 0; i < num_cores; ++i){
    pthread_t* thread = threads[i];
    rc = pthread_join(*thread, NULL);
    if (rc) {
      printf("ERROR; return code from pthread_join() is %d\n", rc);
      exit(-1);
    }
  } 

  fflush(stdout);

}