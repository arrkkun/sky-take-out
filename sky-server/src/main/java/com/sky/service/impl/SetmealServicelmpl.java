package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServicelmpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;


    @Override
    @Transactional
    public void create(SetmealDTO setmealDTO) {
        log.info("创建套餐");
        //先在套餐表中创建套餐，然后再将向套餐-菜品中间表中插入数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.create(setmeal);
        //向套餐-菜品中间表中插入数据
        //获取套餐的id
        Long setmealId = setmeal.getId();
        //获取菜品id列表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        //循环遍历菜品id列表，为每一个菜品添加套餐id
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //向套餐-菜品中间表中插入数据
        setmealDishMapper.insertBatch(setmealDishes);

    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐");
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        log.info("批量删除套餐");
        //检测是否有在售中的套餐
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            //如果有在售中的套餐，则不能删除
            if (setmeal.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        );
        //删除套餐表中的数据
        setmealMapper.deleteBatch(ids);

        //删除套餐-菜品中间表中的数据
        setmealDishMapper.deleteBySetmealIds(ids);


    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    @Transactional
    @ApiOperation(value = "更新套餐")
    public void update(SetmealDTO setmealDTO) {
        log.info("更新套餐");
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //先更新套餐表中的数据
        setmealMapper.update(setmeal);

        //再删除原有的套餐-菜品中间表中的数据
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //再插入新的套餐-菜品中间表中的数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        try {
            log.info("更新套餐状态");





        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //套餐起售时，如果其中有菜品未上架，则不能起售
        if (status == StatusConstant.ENABLE) {
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            dishList.forEach(dish -> {
                if (StatusConstant.DISABLE == dish.getStatus()) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
