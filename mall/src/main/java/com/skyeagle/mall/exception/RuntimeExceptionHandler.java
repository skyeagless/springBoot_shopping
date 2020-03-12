package com.skyeagle.mall.exception;

import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.vo.ResponseVo;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

//捕获异常，并返回相应的vo
@ControllerAdvice
public class RuntimeExceptionHandler{
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody //返回的是json格式
    public ResponseVo handle(RuntimeException e){
        return ResponseVo.error(ResponseEnum.SERVER_ERROR,e.getMessage());
    }

    @ExceptionHandler(UserLoginException.class)
    @ResponseBody
    public ResponseVo userLoginHandle(){
        return ResponseVo.error(ResponseEnum.NOT_LOGIN);
    }

    //表单的统一异常处理  @Responsebody表示该方法的返回结果直接写入HTTP response body中
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo notValidExceptionHandle(MethodArgumentNotValidException e){
        return ResponseVo.error(ResponseEnum.PARAM_ERROR,
                Objects.requireNonNull(e.getBindingResult().getFieldError()).getField()+
                "-"+ e.getBindingResult().getFieldError().getDefaultMessage());
    }
}
