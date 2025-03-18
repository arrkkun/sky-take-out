package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServicelmpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Transactional
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        log.info("添加购物车：{}",shoppingCartDTO);

        //判断当前商品是否已经在购物车中
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //获取当前用户id
        Long userId=BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);

        //如果已经在购物车中，则数量+1
        if(list!=null&&list.size()>0) {

            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);

        }else {
            //如果不在购物车中，则添加到购物车中
            //判断本次添加到购物车的商品是菜品还是套餐
            Long dishId=shoppingCartDTO.getDishId();
            if(dishId!=null) {
                //本次添加的是菜品
                Dish dish=dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            }else {
                //本次添加的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal =setmealMapper.getById(setmealId);
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());

            }

            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);

            }
        }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        //获取当前用户id
        Long userId=BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void clean() {
        //获取当前用户id
        Long userId=BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCartMapper.delete(shoppingCart);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //获取当前用户id
        Long userId=BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(userId);
        //查询购物车中该商品的数量
        // 本次删除的是菜品，则数量-1
        Integer number=shoppingCartMapper.count(shoppingCart)-1;
        //判断当前商品是套餐还是菜品
        Long dishId=shoppingCartDTO.getDishId();
        if(dishId!=null) {


            if (number > 0) {
                shoppingCart.setNumber(number);
                shoppingCartMapper.updateNumberByUserId(shoppingCart);
            } else {
                shoppingCartMapper.deleteById(shoppingCart);
            }
        }else {
            //本次删除的是套餐，则数量-1

            if (number > 0) {
                shoppingCart.setNumber(number);
                shoppingCartMapper.updateNumberByUserId(shoppingCart);
            } else {
                shoppingCartMapper.deleteById(shoppingCart);
            }
        }
    }

}
