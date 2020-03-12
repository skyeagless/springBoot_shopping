package com.skyeagle.mall.controller;

import com.skyeagle.mall.constant.MallConstant;
import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.form.ShippingForm;
import com.skyeagle.mall.service.IShippingService;
import com.skyeagle.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class ShippingController {
    @Autowired
    private IShippingService shippingService;

    @PostMapping("/shippings")
    public ResponseVo add(@Valid @RequestBody ShippingForm form,
                          HttpSession session){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return shippingService.add(user.getId(),form);
    }

    @DeleteMapping("/shippings/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId,
                             HttpSession session){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);

        return shippingService.delete(user.getId(),shippingId);
    }

    @PutMapping("/shippings/{shippingId}")
    public ResponseVo update(@PathVariable Integer shippingId,
                             HttpSession session,
                             @Valid @RequestBody ShippingForm form){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);

        return shippingService.update(user.getId(),shippingId,form);
    }

    @GetMapping("/shippings")
    public ResponseVo list(HttpSession session,
                           @RequestParam(value = "pageNum",defaultValue = "1",required = false) Integer pageNum,
                           @RequestParam(value = "pageSize",defaultValue = "10",required = false) Integer pageSize){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return shippingService.list(user.getId(),pageNum,pageSize);
    }
}
