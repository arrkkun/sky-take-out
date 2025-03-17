package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorsMapper {

    /**
     * 批量插入菜品口味信息
     *
     */
    void insertBatch(List<DishFlavor> flavors);

    @Delete("DELETE FROM dish_flavor WHERE dish_id = #{dishid}")
    void deleteByDishId(Long dishid);

    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}
