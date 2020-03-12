package com.skyeagle.mall.intercepter;

import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.exception.UserLoginException;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.skyeagle.mall.constant.MallConstant.CURRENT_USER;

//拦截器
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {
    //请求之前拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle....");
        User user = (User)(request.getSession().getAttribute(CURRENT_USER));
        if(user == null){
            log.info("user = null");
            throw new UserLoginException();
//            return ResponseVo.error(ResponseEnum.NOT_LOGIN);
        }
        return true;
    }
}
