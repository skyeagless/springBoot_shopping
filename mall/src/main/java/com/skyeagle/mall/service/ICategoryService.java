package com.skyeagle.mall.service;

import com.skyeagle.mall.vo.CategoryVo;
import com.skyeagle.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    ResponseVo<List<CategoryVo>> selectAllCategory();
    //查询所有子类目的id
    void findSubCategoryId(Integer id, Set<Integer> resultSet);
}
