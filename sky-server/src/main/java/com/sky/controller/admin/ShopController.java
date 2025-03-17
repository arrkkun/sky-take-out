package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "后台-商铺管理")
@Slf4j
public class ShopController {

    public static final String KEY="SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置商铺状态
     * @param status 状态
     * @return Result
     */
    @PutMapping("/{status}")
    @ApiOperation("设置商铺状态")
    public Result setStatus(@PathVariable Integer status){
        log .info("设置商铺状态：{}", status== 1? "营业中" : "休息中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 获取商铺状态
     * @return Result
     */
    @GetMapping("/status")
    @ApiOperation("获取商铺状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log .info("获取商铺状态为：{}",status==1? "营业中" : "休息中");
        return Result.success(status);
    }
}
