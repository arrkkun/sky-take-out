package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据套餐id获取菜品id列表
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品关系
     */
    void insertBatch(@Param("setmealDishList") List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除菜品
     */
    void deleteBySetmealIds(@Param("ids") List<Long> ids);

    /**
     * 根据菜品id删除菜品
     */
    void deleteBySetmealId(Long id);

    /**
     * 根据菜品id获取套餐信息
     */
    List<SetmealDish> getBySetmealId(Long id);
}
