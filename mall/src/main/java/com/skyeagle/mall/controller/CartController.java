package com.skyeagle.mall.controller;

import com.skyeagle.mall.constant.MallConstant;
import com.skyeagle.mall.entity.User;
import com.skyeagle.mall.form.CartAddForm;
import com.skyeagle.mall.form.CartUpdateForm;
import com.skyeagle.mall.service.ICartService;
import com.skyeagle.mall.vo.CartVo;
import com.skyeagle.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

//@RequestBody一般用于在提交ajax时所带的请求参数插入到方法中。 简单的说就是帮你把提交的数据直接放到你定义的变量中。
@RestController
public class CartController {
    @Autowired
    private ICartService cartService;
    //uid从session里面拿
    @GetMapping("/carts")
    public ResponseVo<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return cartService.list(user.getId());
    }


    @PostMapping("/carts")
    public ResponseVo<CartVo> add(@Valid @RequestBody CartAddForm cartAddForm,
                                  HttpSession session){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return cartService.add(cartAddForm,user.getId());
    }

    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo> update(@Valid @RequestBody CartUpdateForm cartUpdateForm,
                                     HttpSession session,@PathVariable Integer productId){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return cartService.update(user.getId(),productId,cartUpdateForm);
    }

    @DeleteMapping("/carts/{productId}")
    public ResponseVo<CartVo> delete(HttpSession session,@PathVariable Integer productId){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return cartService.delete(user.getId(),productId);
    }

    @PutMapping("/carts/selectAll")
    public ResponseVo<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return cartService.selectAll(user.getId());
    }

    @PutMapping("/carts/unselectAll")
    public ResponseVo<CartVo> unselectAll(HttpSession session){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return cartService.unselectAll(user.getId());
    }

    @GetMapping("/carts/products/sum")
    public ResponseVo<Integer> sum(HttpSession session){
        User user = (User)session.getAttribute(MallConstant.CURRENT_USER);
        return cartService.sum(user.getId());
    }
}
