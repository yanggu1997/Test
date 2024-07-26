package com.wzy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzy.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>{
}
