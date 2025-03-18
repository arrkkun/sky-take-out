package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态查询购物车列表
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 修改购物车商品数量
     * @param shoppingCart
     */
    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);


    /**
     * 新增购物车商品
     * @param shoppingCart
     */
    @Insert("INSERT INTO shopping_cart(name,user_id,dish_id,setmeal_id,dish_flavor,number,amount,image,create_time)" +
            " VALUES(#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 删除购物车商品
     * @param shoppingCart
     */
    @Update("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void delete(ShoppingCart shoppingCart);

    /**
     * 删除购物车单个商品
     * @param shoppingCart
     */
    void deleteById(ShoppingCart shoppingCart);

    /**
     * 查询购物车中单个商品的数量
     * @param shoppingCart
     * @return
     */
     Integer count(ShoppingCart shoppingCart);

    /**
     * 修改购物车商品数量
     * @param shoppingCart
     */
    void updateNumberByUserId(ShoppingCart shoppingCart);
}

