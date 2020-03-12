package com.skyeagle.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skyeagle.mall.service.IOrderService;
import com.skyeagle.mall.vo.OrderVo;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class OrderServiceImplTest {
    @Autowired
    private IOrderService orderService;

    private Integer uid = 1;

    private Integer shippingId = 9;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    void create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid,shippingId);
        log.info("responseVo:{}",gson.toJson(responseVo));
    }

    @Test
    void list(){
        ResponseVo<PageInfo> responseVo = orderService.list(uid,1,10);
        log.info("responseVo:{}",gson.toJson(responseVo));
    }

    @Test
    void detail(){
        ResponseVo<OrderVo> responseVo = orderService.detail(uid,1583924475999L);
        log.info("responseVo:{}",gson.toJson(responseVo));
    }

    @Test
    void cancel(){
        ResponseVo responseVo = orderService.cancel(uid,1583924475999L);
        log.info("responseVo:{}",gson.toJson(responseVo));
    }
}