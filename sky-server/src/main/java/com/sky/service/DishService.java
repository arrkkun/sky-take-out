package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {
    /**
     * 保存菜品信息
     * @param dishDto
     */
    void saveWithFlavor(DishDTO dishDto);
}
