package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 分页查询菜品信息
     * @param dishPagequeryDTO
     * @return
     *
     */
     PageResult pageQuery(DishPageQueryDTO dishPagequeryDTO);


    /**
     * 保存菜品信息
     * @param dishDto
     */
    void saveWithFlavor(DishDTO dishDto);

    /**
     * 根据id删除菜品信息
     * @param ids
     */
    void deleteBatch(List<Long> ids);


    /**
     * 更新菜品信息
     * @param dishDto
     */
    void updateWithFlavor(DishDTO dishDto);

    /**
     * 根据id获取菜品信息与口味信息
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据分类id获取菜品信息
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

    void startOrStop(Integer status, Long id);
}
