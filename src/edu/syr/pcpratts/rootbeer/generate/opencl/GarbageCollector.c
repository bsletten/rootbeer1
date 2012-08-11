#define GC_OBJ_TYPE_COUNT char
#define GC_OBJ_TYPE_COLOR char
#define GC_OBJ_TYPE_TYPE char
#define GC_OBJ_TYPE_CTOR_USED char
#define GC_OBJ_TYPE_SIZE int

typedef int boolean;
typedef int byte;

#define COLOR_GREY 0
#define COLOR_BLACK 1
#define COLOR_WHITE 2

$$__device__$$ void edu_syr_pcpratts_gc_collect($$__global$$ char * gc_info);
$$__device__$$ void edu_syr_pcpratts_gc_assign($$__global$$ char * gc_info, int * lhs, int rhs);
$$__device__$$ $$__global$$ char * edu_syr_pcpratts_gc_deref($$__global$$ char * gc_info, int handle);
$$__device__$$ int edu_syr_pcpratts_gc_malloc($$__global$$ char * gc_info, long long size);
$$__device__$$ long long edu_syr_pcpratts_gc_malloc_no_fail($$__global$$ char * gc_info, long long size);

#define CACHE_SIZE_BYTES 32
#define CACHE_SIZE_INTS (CACHE_SIZE_BYTES / sizeof(int))
#define CACHE_ENTRY_SIZE 4

#define TO_SPACE_OFFSET               0
#define TO_SPACE_FREE_POINTER_OFFSET  8
#define SPACE_SIZE_OFFSET             16

$$__device__$$
void edu_syr_pcpratts_exitMonitorRef($$__global$$ char * gc_info, int thisref, int old){
  char * mem = edu_syr_pcpratts_gc_deref(gc_info, thisref); 
  mem += 12;
  if(old == -1){    
    __threadfence();
    atomicExch((int *) mem, -1); 
  }
}

$$__device__$$
void edu_syr_pcpratts_exitMonitorMem($$__global$$ char * gc_info, char * mem, int old){
  if(old == -1){   
    __threadfence(); 
    atomicExch((int *) mem, -1); 
  }
}

$$__device__$$ 
long long java_lang_Double_doubleToLongBits($$__global$$ char * gc_info,  double value , int * exception){
  long long ret = *((long long *) ((double *) &value));
  return ret;
}

$$__device__$$ 
double java_lang_Double_longBitsToDouble($$__global$$ char * gc_info, long long value , int * exception){
  double ret = *((double *) ((long long *) &value));
  return ret;
}
    

$$__device__$$ double java_lang_StrictMath_exp( char * gc_info , double parameter0 , int * exception ) { 
  return exp(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_log( char * gc_info , double parameter0 , int * exception ) { 
  return log(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_log10( char * gc_info , double parameter0 , int * exception ) { 
  return log10(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_sqrt( char * gc_info , double parameter0 , int * exception ) { 
  return sqrt(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cbrt( char * gc_info , double parameter0 , int * exception ) { 
  return cbrt(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_IEEEremainder( char * gc_info , double parameter0 , double parameter1, int * exception ) { 
  return remainder(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_ceil( char * gc_info , double parameter0 , int * exception ) { 
  return ceil(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_floor( char * gc_info , double parameter0 , int * exception ) { 
  return floor(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_sin( char * gc_info , double parameter0 , int * exception ) { 
  return sin(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cos( char * gc_info , double parameter0 , int * exception ) { 
  return cos(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_tan( char * gc_info , double parameter0 , int * exception ) { 
  return tan(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_asin( char * gc_info , double parameter0 , int * exception ) { 
  return asin(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_acos( char * gc_info , double parameter0 , int * exception ) { 
  return acos(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_atan( char * gc_info , double parameter0 , int * exception ) { 
  return atan(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_atan2( char * gc_info , double parameter0 , double parameter1, int * exception ) { 
  return atan2(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_pow( char * gc_info , double parameter0 , double parameter1, int * exception ) { 
  return pow(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_sinh( char * gc_info , double parameter0 , int * exception ) { 
  return sinh(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cosh( char * gc_info , double parameter0 , int * exception ) { 
  return cosh(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_tanh( char * gc_info , double parameter0 , int * exception ) { 
  return tanh(parameter0); 
} 

$$__device__$$ 
boolean edu_syr_pcpratts_rootbeer_runtime_RootbeerGpu_isOnGpu($$__global$$ char * gc_info, int * exception){
  return 1;
}

$$__device__$$ 
int edu_syr_pcpratts_rootbeer_runtime_RootbeerGpu_getThreadId($$__global$$ char * gc_info, int * exception){
  return getThreadId();
}

$$__device__$$ char
edu_syr_pcpratts_cmp(long long lhs, long long rhs){
  if(lhs > rhs)
    return 1;
  if(lhs < rhs)
    return -1;
  return 0;
}

$$__device__$$ char
edu_syr_pcpratts_cmpl(double lhs, double rhs){
  if(lhs > rhs)
    return 1;
  if(lhs < rhs)
    return -1;
  if(lhs == rhs)
    return 0;
  return -1;
}

$$__device__$$ char
edu_syr_pcpratts_cmpg(double lhs, double rhs){
  if(lhs > rhs)
    return 1;
  if(lhs < rhs)
    return -1;
  if(lhs == rhs)
    return 0;
  return 1;
}


$$__device__$$ void
edu_syr_pcpratts_gc_memcpy($$__global$$ char * dest, $$__global$$ char * src, int len) {
  int i;
  for(i = 0; i < len; ++i){
    dest[i] = src[i];
  }
}

$$__device__$$ double edu_syr_pcpratts_modulus(double a, double b)
{
  long result = (long) ( a / b );
  return a - ((double) result) * b;
}

$$__device__$$ int
edu_syr_pcpratts_gc_get_loc($$__global$$ char * mem_loc, int count){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) +
    sizeof(GC_OBJ_TYPE_TYPE) + sizeof(GC_OBJ_TYPE_CTOR_USED) +sizeof(GC_OBJ_TYPE_SIZE) +
    count * sizeof(int);
  return (($$__global$$ int *) mem_loc)[0];
}

$$__device__$$ void
edu_syr_pcpratts_gc_set_count($$__global$$ char * mem_loc, GC_OBJ_TYPE_COUNT value){
  mem_loc[0] = value;
}

$$__device__$$ GC_OBJ_TYPE_COUNT
edu_syr_pcpratts_gc_get_count($$__global$$ char * mem_loc){
  return mem_loc[0];
}

$$__device__$$ void
edu_syr_pcpratts_gc_set_color($$__global$$ char * mem_loc, GC_OBJ_TYPE_COLOR value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT);
  mem_loc[0] = value;
}

$$__device__$$ void
edu_syr_pcpratts_gc_init_monitor($$__global$$ char * mem_loc){
  mem_loc += 12;
  int * addr = (int *) mem_loc;
  *addr = -1;
}

$$__device__$$ GC_OBJ_TYPE_COLOR
edu_syr_pcpratts_gc_get_color($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT);
  return mem_loc[0];
}

$$__device__$$ void
edu_syr_pcpratts_gc_set_type($$__global$$ char * mem_loc, GC_OBJ_TYPE_TYPE value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR);
  mem_loc[0] = value;
}

$$__device__$$ GC_OBJ_TYPE_TYPE
edu_syr_pcpratts_gc_get_type($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR);
  return (GC_OBJ_TYPE_TYPE) mem_loc[0];
}

$$__device__$$ void
edu_syr_pcpratts_gc_set_ctor_used($$__global$$ char * mem_loc, GC_OBJ_TYPE_CTOR_USED value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(GC_OBJ_TYPE_TYPE);
  mem_loc[0] = value;
}

$$__device__$$ GC_OBJ_TYPE_CTOR_USED
edu_syr_pcpratts_gc_get_ctor_used($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(GC_OBJ_TYPE_TYPE);
  return mem_loc[0];
}

$$__device__$$ void
edu_syr_pcpratts_gc_set_size($$__global$$ char * mem_loc, GC_OBJ_TYPE_SIZE value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(GC_OBJ_TYPE_TYPE) + sizeof(GC_OBJ_TYPE_CTOR_USED);
  *(($$__global$$ GC_OBJ_TYPE_SIZE *) &mem_loc[0]) = value;
}

$$__device__$$ GC_OBJ_TYPE_SIZE
edu_syr_pcpratts_gc_get_size($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(GC_OBJ_TYPE_TYPE) + sizeof(GC_OBJ_TYPE_CTOR_USED);
  return *(($$__global$$ GC_OBJ_TYPE_SIZE *) &mem_loc[0]);
}

$$__device__$$ char edu_syr_pcpratts_getchar($$__global$$ char * buffer, int pos){
  return buffer[pos];
}

$$__device__$$ void edu_syr_pcpratts_setchar($$__global$$ char * buffer, int pos, char value){
  buffer[pos] = value;
}

$$__device__$$ short edu_syr_pcpratts_getshort($$__global$$ char * buffer, int pos){
  return *(($$__global$$ short *) &buffer[pos]);
}

$$__device__$$ void edu_syr_pcpratts_setshort($$__global$$ char * buffer, int pos, short value){
  *(($$__global$$ short *) &buffer[pos]) = value;
}

$$__device__$$ int edu_syr_pcpratts_getint($$__global$$ char * buffer, int pos){
  return *(($$__global$$ int *) &buffer[pos]);
}

$$__device__$$ void edu_syr_pcpratts_setint($$__global$$ char * buffer, int pos, int value){
  *(($$__global$$ int *) &buffer[pos]) = value;
}

$$__device__$$ long long edu_syr_pcpratts_getlong($$__global$$ char * buffer, int pos){
  return *(($$__global$$ long *) &buffer[pos]);
}

$$__device__$$ void edu_syr_pcpratts_setlong($$__global$$ char * buffer, int pos, long long value){
  *(($$__global$$ long long *) &buffer[pos]) = value;
}

$$__device__$$ size_t edu_syr_pcpratts_getsize_t($$__global$$ char * buffer, int pos){
  return *(($$__global$$ size_t *) &buffer[pos]);
}

$$__device__$$ void edu_syr_pcpratts_setsize_t($$__global$$ char * buffer, int pos, size_t value){
  *(($$__global$$ size_t *) &buffer[pos]) = value;
}

$$__device__$$ void
edu_syr_pcpratts_gc_set_to_space_address($$__global$$ char * gc_info, $$__global$$ char * value){
  edu_syr_pcpratts_setlong(gc_info, TO_SPACE_OFFSET, (long long) value);
}

$$__device__$$ $$__global$$ long long *
edu_syr_pcpratts_gc_get_to_space_address($$__global$$ char * gc_info){
  long long value = edu_syr_pcpratts_getlong(gc_info, TO_SPACE_OFFSET);
  return ($$__global$$ long long *) value;
}

$$__device__$$ long long
edu_syr_pcpratts_gc_get_to_space_free_ptr($$__global$$ char * gc_info){
  return edu_syr_pcpratts_getlong(gc_info, TO_SPACE_FREE_POINTER_OFFSET);
}

$$__device__$$ void
edu_syr_pcpratts_gc_set_to_space_free_ptr($$__global$$ char * gc_info, long long value){
  edu_syr_pcpratts_setlong(gc_info, TO_SPACE_FREE_POINTER_OFFSET, value);
}

$$__device__$$ int
edu_syr_pcpratts_gc_get_space_size($$__global$$ char * gc_info){
  return edu_syr_pcpratts_getint(gc_info, SPACE_SIZE_OFFSET);
}

$$__device__$$ int
edu_syr_pcpratts_strlen(char * str_constant){
  int ret = 0;
  while(true){
    if(str_constant[ret] != '\0')
      ret++;
    else
      return ret;
  }
}

$$__device__$$ int 
char__array_new($$__global$$ char * gc_info, int size, int * exception);

$$__device__$$ void 
char__array_set($$__global$$ char * gc_info, int thisref, int parameter0, char parameter1, int * exception);

$$__device__$$ int 
java_lang_String_initab850b60f96d11de8a390800200c9a660_a1_($$__global$$ char * gc_info, int parameter0, int * exception);

$$__device__$$ int
edu_syr_pcpratts_string_constant($$__global$$ char * gc_info, char * str_constant, int * exception){
  int i;
  int len = edu_syr_pcpratts_strlen(str_constant);
  int characters = char__array_new(gc_info, len, exception);
  for(i = 0; i < len; ++i){
    char__array_set(gc_info, characters, i, str_constant[i], exception);
  }
  
  return java_lang_String_initab850b60f96d11de8a390800200c9a660_a1_(gc_info, characters, exception);
}

$$__device__$$ int
edu_syr_pcpratts_array_length($$__global$$ char * gc_info, int thisref){
  //if(thisref & 0x1000000000000000L){
  //  thisref &= 0x0fffffffffffffffL;
  //  thisref += 8;
  //  return edu_syr_pcpratts_cache_get_int(thisref);
  //} else {
    $$__global$$ char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);
    return edu_syr_pcpratts_getint(thisref_deref, 8);
  //}
}

$$__device__$$ void
edu_syr_pcpratts_gc_assign($$__global$$ char * gc_info, int * lhs_ptr, int rhs){
  *lhs_ptr = rhs;
}

$$__device__$$ void
edu_syr_pcpratts_gc_assign_global($$__global$$ char * gc_info, $$__global$$ int * lhs_ptr, int rhs){
  *lhs_ptr = rhs;
}
 
$$__device__$$ int java_lang_StackTraceElement__array_get($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void java_lang_StackTraceElement__array_set($$__global$$ char * gc_info, int thisref, int parameter0, int parameter1, int * exception);
$$__device__$$ int java_lang_StackTraceElement__array_new($$__global$$ char * gc_info, int size, int * exception);
$$__device__$$ int java_lang_StackTraceElement_initab850b60f96d11de8a390800200c9a660_3_3_3_4_($$__global$$ char * gc_info, int parameter0, int parameter1, int parameter2, int parameter3, int * exception);
$$__device__$$ void instance_setter_java_lang_RuntimeException_stackDepth($$__global$$ char * gc_info, int thisref, int parameter0);
$$__device__$$ int instance_getter_java_lang_RuntimeException_stackDepth($$__global$$ char * gc_info, int thisref);
$$__device__$$ int java_lang_StackTraceElement__array_get($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ int instance_getter_java_lang_Throwable_stackTrace($$__global$$ char * gc_info, int thisref, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_stackTrace($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);

$$__device__$$ int java_lang_Throwable_fillInStackTrace($$__global$$ char * gc_info, int thisref, int * exception){
  int trace = java_lang_StackTraceElement__array_new(gc_info, 8, exception);
  instance_setter_java_lang_Throwable_stackTrace(gc_info, thisref, trace, exception);
  return thisref;
}

$$__device__$$ int java_lang_Throwable_getStackTraceElement($$__global$$ char * gc_info, int thisref, int parameter0, int * exception){
  int array = instance_getter_java_lang_Throwable_stackTrace(gc_info, thisref, exception);
  return java_lang_StackTraceElement__array_get(gc_info, array, parameter0, exception);
}

$$__device__$$ int java_lang_Throwable_getStackTraceDepth($$__global$$ char * gc_info, int thisref, int * exception){
  return 0;
}

$$__device__$$ void edu_syr_pcpratts_fillInStackTrace($$__global$$ char * gc_info, int exception, char * class_name, char * method_name){
}

$$__device__$$ void instance_setter_java_lang_Throwable_cause($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_detailMessage($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_stackDepth($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void java_lang_VirtualMachineError_initab850b60f96d11de8a390800200c9a66_body0_($$__global$$ char * gc_info, int thisref, int * exception);

$$__device__$$ int java_lang_OutOfMemoryError_initab850b60f96d11de8a390800200c9a66($$__global$$ char * gc_info, int * exception){

int r0 = -1;
int thisref = edu_syr_pcpratts_gc_malloc_no_fail(gc_info, 40);
char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);

//class info
edu_syr_pcpratts_gc_set_count(thisref_deref, 0);
edu_syr_pcpratts_gc_set_color(thisref_deref, COLOR_GREY);
edu_syr_pcpratts_gc_set_type(thisref_deref, 9);
edu_syr_pcpratts_gc_set_ctor_used(thisref_deref, 1);
edu_syr_pcpratts_gc_set_size(thisref_deref, 40);
instance_setter_java_lang_Throwable_cause(gc_info, thisref, -1, exception);
instance_setter_java_lang_Throwable_detailMessage(gc_info, thisref, -1, exception);
instance_setter_java_lang_Throwable_stackTrace(gc_info, thisref, -1, exception);
//r0 := @this: java.lang.OutOfMemoryError
edu_syr_pcpratts_gc_assign(gc_info, & r0 ,  thisref );
//specialinvoke r0.<java.lang.VirtualMachineError: void <init>()>()
java_lang_VirtualMachineError_initab850b60f96d11de8a390800200c9a66_body0_(gc_info,
 thisref, exception);
//return
return thisref;
}
