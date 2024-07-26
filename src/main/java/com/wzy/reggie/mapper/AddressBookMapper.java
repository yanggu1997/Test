package com.wzy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.wzy.reggie.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}
