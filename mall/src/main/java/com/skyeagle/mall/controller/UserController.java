package com.skyeagle.mall.controller;


import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.form.UserLoginForm;
import com.skyeagle.mall.form.UserRegisterForm;
import com.skyeagle.mall.service.impl.UserServiceImpl;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.skyeagle.mall.constant.MallConstant.CURRENT_USER;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/user/register")
    public ResponseVo register(@Valid @RequestBody UserRegisterForm userRegisterForm,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //日志
            log.error("注册提交的参数有误，{}{}",
                    bindingResult.getFieldError().getField(),
                    bindingResult.getFieldError().getDefaultMessage());
            //返回前端
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
        }
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm, user);
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                            BindingResult bindingResult,
                            HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
        }
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(),userLoginForm.getPassword());
        //设置Session
        session.setAttribute(CURRENT_USER,userResponseVo.getData());
        log.info("/login sessionId={}", session.getId());
        return userResponseVo;
    }

    //session保存在内存里，一般要使用token(sessionId)+redis
    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session){
        User user = (User)session.getAttribute(CURRENT_USER);
        return ResponseVo.success(user);
    }

    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session){
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success();
    }
}
