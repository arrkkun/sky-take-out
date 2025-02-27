package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorsMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServicelmpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorsMapper dishFlavorMapper;
    /**
     * 新增菜品和对应口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDto) {
        Dish dish = new Dish();
        //属性拷贝
        BeanUtils.copyProperties(dishDto, dish);
        //向菜品表插入1个数据
        dishMapper.insert(dish);
        //获取菜品id
        Long dishId = dish.getId();
        //向口味表插入n个数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors!= null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
