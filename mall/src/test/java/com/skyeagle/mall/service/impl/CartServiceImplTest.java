package com.skyeagle.mall.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skyeagle.mall.form.CartAddForm;
import com.skyeagle.mall.form.CartUpdateForm;
import com.skyeagle.mall.vo.CartVo;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class CartServiceImplTest {
    @Autowired
    private CartServiceImpl cartService;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    void add() {
        CartAddForm form = new CartAddForm();
        form.setProductId(29);
        form.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(form,1);
        log.info("list={}",gson.toJson(responseVo));
    }

    @Test
    void list(){
        ResponseVo<CartVo> list = cartService.list(1);
        log.info("list={}",gson.toJson(list));
    }

    @Test
    void update(){
        CartUpdateForm form = new CartUpdateForm();
        form.setQuantity(10);
        form.setSelected(false);
        ResponseVo<CartVo> responseVo = cartService.update(1,26,form);
        log.info("list={}",gson.toJson(responseVo));
    }

    @AfterEach
    void delete(){
        ResponseVo<CartVo> responseVo = cartService.delete(1,29);
        log.info("result={}",gson.toJson(responseVo));
    }

    @Test
    void selectAll(){
        ResponseVo<CartVo> responseVo = cartService.selectAll(1);
        log.info("result={}",gson.toJson(responseVo));
    }

    @Test
    void unselectAll(){
        ResponseVo<CartVo> responseVo = cartService.unselectAll(1);
        log.info("result={}",gson.toJson(responseVo));
    }

    @Test
    void sum(){
        ResponseVo<Integer> responseVo = cartService.sum(1);
        log.info("result={}",gson.toJson(responseVo));
    }
}