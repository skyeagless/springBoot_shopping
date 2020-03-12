package com.skyeagle.mall.service.impl;

import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.enums.RoleEnum;
import com.skyeagle.mall.service.IUserService;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private IUserService userService;

    @BeforeEach
    void register() {
        User user = new User("admin","admin","123@qq.com",
                RoleEnum.CUSTOMER.getCode());
        userService.register(user);
    }

    @Test
    public void login(){
        ResponseVo<User> responseVo = userService.login("admin","admin");
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(),
                responseVo.getStatus());
    }

}