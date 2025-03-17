package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openId获取用户信息
     * @param openId
     * @return
     */
    @Select("SELECT * FROM user WHERE openid = #{openId}")
    User getByOpenid(String openId);

    /**
     * 插入用户信息
     * @param user
     */
    void insert(User user);
}
