package com.wzy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzy.reggie.dto.SetmealDto;
import com.wzy.reggie.pojo.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);


    void updateStatus(Integer status, List<Long> ids);

    void removeWithDish(List<Long> ids);
}
