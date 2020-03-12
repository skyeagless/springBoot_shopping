package com.skyeagle.mall.service;

import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.vo.ResponseVo;

public interface IUserService {
    //注册
    ResponseVo register(User user);

    //登陆
    ResponseVo<User> login(String username,String password);
}
