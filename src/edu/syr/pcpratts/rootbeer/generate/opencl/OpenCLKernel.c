
__kernel void entry(__global char * gc_info_space, __global long * handles,
  __global long * to_space_free_ptr, long space_size, __global long * to_space, __local long * fast_to_space,
  $$__to_space_list__$$){

  fast_to_space[0] = space_size;
  $$__to_space_init__$$

  __global char * gc_info =
    edu_syr_pcpratts_gc_init(gc_info_space, *to_space_free_ptr);

  int loop_control = (get_group_id(0) * get_local_size(0)) + get_local_id(0);
  long handle = handles[loop_control];
  int exception = 0;
  %%invoke_run%%(gc_info, fast_to_space, handle, &exception);
}