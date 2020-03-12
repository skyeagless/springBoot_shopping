package com.skyeagle.mall.service;

import com.github.pagehelper.PageInfo;
import com.skyeagle.mall.vo.ProductDetailVo;
import com.skyeagle.mall.vo.ResponseVo;



public interface IProductService {
    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    ResponseVo<ProductDetailVo> detail(Integer productId);
}
