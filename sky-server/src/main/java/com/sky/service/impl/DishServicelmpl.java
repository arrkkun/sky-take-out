package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorsMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServicelmpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorsMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


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

    /**
     * 根据id查询菜品信息
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否可以删除--判断是否为起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new RuntimeException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否有关联的套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0)
            throw new RuntimeException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        //删除菜品
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    @Override
    public void updateWithFlavor(DishDTO dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        dishMapper.updateById(dish);
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDto.getId());
        //插入新的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors!= null && flavors.size() > 0) {
            flavors.forEach(DishFlavor -> {
                DishFlavor.setDishId(dishDto.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品信息
        Dish dish = dishMapper.getById(id);
        if (dish == null) {
            return null;
        }
        //根据菜品id查询口味信息
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //封装返回结果
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

//    @Override
//    public List<DishVO> listByCategoryId(Long categoryId) {
//        //根据分类id查询菜品列表
//        List<Dish> dishes = dishMapper.listByCategoryId(categoryId);
//        //封装返回结果
//        List<DishVO> dishVOS = new ArrayList<>();
//        for (Dish dish : dishes) {
//            DishVO dishVO = new DishVO();
//            BeanUtils.copyProperties(dish, dishVO);
//            dishVOS.add(dishVO);
//        }
//        return dishVOS;
//
//
//    }


    /**
     * 根据分类id查询菜品列表
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 分页查询菜品列表
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }


}
