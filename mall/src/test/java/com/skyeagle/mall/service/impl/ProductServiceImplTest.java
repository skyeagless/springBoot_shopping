package com.skyeagle.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.service.IProductService;
import com.skyeagle.mall.vo.ProductVo;
import com.skyeagle.mall.vo.ResponseVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ProductServiceImplTest {
    @Autowired
    private IProductService productService;

    @Test
    void list() {
        ResponseVo<PageInfo> responseVo = productService.list(null,1,2);
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}