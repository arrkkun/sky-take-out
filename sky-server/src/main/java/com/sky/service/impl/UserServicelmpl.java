package com.sky.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServicelmpl implements UserService {

    //微信登录接口地址
    public static final String WX_LOGIN = "http://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;


    /**
     * 微信登录
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {

        String openid = getOpenid(userLoginDTO.getCode());

        //判断OPENID是否存在,如果为空则抛出异常
        if (openid == null) {
            log.error("微信登录失败，OPENID为空");
            throw new RuntimeException(MessageConstant.LOGIN_FAILED);
        }

        //根据OPENID查询用户信息，如果不存在则新增用户信息
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            user =
            User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insert(user);
        }
        //返回用户信息
        return user;
    }

    private String getOpenid(String code) {

        //调用微信登录接口，获取用户信息
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN,map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
