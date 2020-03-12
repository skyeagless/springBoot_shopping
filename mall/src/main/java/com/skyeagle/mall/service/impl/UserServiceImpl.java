package com.skyeagle.mall.service.impl;

import com.skyeagle.mall.dao.UserMapper;
import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.enums.RoleEnum;
import com.skyeagle.mall.service.IUserService;
import com.skyeagle.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseVo register(User user) {
        //1.校验：用户名/邮箱
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if(countByUsername > 0){
            return ResponseVo.error(ResponseEnum.USER_HAVE_BEEN_EXIST);
        }
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if(countByEmail > 0){
            return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
        }
        //2.MD5散列算法
        user.setPassword(DigestUtils.
                md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        //3.写入数据库
        user.setRole(RoleEnum.CUSTOMER.getCode());
        int resultCount = userMapper.insertSelective(user);
        if(resultCount == 0){
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }

        return ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if(user == null){
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        if(!Objects.requireNonNull(user).getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))){
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        //不需要给前端返回密码的MD5值
        user.setPassword("");
        return ResponseVo.success(user);
    }
}
