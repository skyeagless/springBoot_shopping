package com.skyeagle.mall.controller;

import com.github.pagehelper.PageInfo;
import com.skyeagle.mall.constant.MallConstant;
import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.form.OrderCreateForm;
import com.skyeagle.mall.service.impl.OrderServiceImpl;
import com.skyeagle.mall.vo.OrderVo;
import com.skyeagle.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class OrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(HttpSession session,
                                      @Valid @RequestBody OrderCreateForm form){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return orderService.create(user.getId(),form.getShippingId());
    }

    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(HttpSession session,
                                     @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return orderService.list(user.getId(),pageNum,pageSize);
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(HttpSession session,@PathVariable Long orderNo){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return orderService.detail(user.getId(),orderNo);
    }

    @PutMapping("/orders/{orderNo}")
    public ResponseVo cancel(HttpSession session,@PathVariable Long orderNo){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return orderService.cancel(user.getId(),orderNo);
    }
}
