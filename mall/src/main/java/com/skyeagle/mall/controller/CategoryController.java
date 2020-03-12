package com.skyeagle.mall.controller;

import com.skyeagle.mall.service.impl.CategoryServiceImpl;
import com.skyeagle.mall.vo.CategoryVo;
import com.skyeagle.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping("/categories")
    public ResponseVo<List<CategoryVo>> selectAll(){
        return categoryService.selectAllCategory();
    }
}
