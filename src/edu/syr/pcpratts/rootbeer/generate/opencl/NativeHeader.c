#include <stdio.h>
#include <pthread.h>
#include <math.h>

pthread_mutex_t atom_add_mutex;

long long atom_add(long long * addr, long long value){
  pthread_mutex_lock(&atom_add_mutex);
  long long ret = *addr;
  *addr += value;
  pthread_mutex_unlock(&atom_add_mutex);
  return ret;
}

unsigned long long atomicAdd(unsigned long long * addr, long long value){
  pthread_mutex_lock(&atom_add_mutex);
  long long ret = *addr;
  *addr += value;
  pthread_mutex_unlock(&atom_add_mutex);
  return ret;
}

int atomicCAS(int * addr, int compare, int set){
  pthread_mutex_lock(&atom_add_mutex);
  int ret = *addr;
  if(ret == compare)
    *addr = set;
  pthread_mutex_unlock(&atom_add_mutex);
  return ret;
}

int atomicExch(int * addr, int value){
  pthread_mutex_lock(&atom_add_mutex);
  int ret = *addr;
  *addr = value;
  pthread_mutex_unlock(&atom_add_mutex);
  return ret;
}

int getThreadId();

void __threadfence(){ }

#define true 1
#define false 0

long long m_Local[2];
int * m_Cache;