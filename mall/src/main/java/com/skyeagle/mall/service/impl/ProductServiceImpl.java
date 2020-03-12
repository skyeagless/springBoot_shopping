package com.skyeagle.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.skyeagle.mall.dao.ProductMapper;
import com.skyeagle.mall.entity.Product;
import com.skyeagle.mall.enums.ProductStatusEnum;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.service.ICategoryService;
import com.skyeagle.mall.service.IProductService;
import com.skyeagle.mall.vo.ProductDetailVo;
import com.skyeagle.mall.vo.ProductVo;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if(categoryId != null){
            categoryService.findSubCategoryId(categoryId,categoryIdSet);
            categoryIdSet.add(categoryId);
        }
        //加入分页
        PageHelper.startPage(pageNum,pageSize);

        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet.size() == 0 ? null : categoryIdSet);
        //List<Product>-->List<ProductVo>
        List<ProductVo> productVoList = productList2ProductvoList(productList);

        //加入分页返回json的对象
        PageInfo pageInfo = new PageInfo<>(productList);
        pageInfo.setList(productVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if(!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        return ResponseVo.success(productDetailVo);
    }



    private List<ProductVo> productList2ProductvoList(List<Product> productList){
        List<ProductVo> productVos =  new ArrayList<>();
        for(Product product:productList){
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(product,productVo);
            productVos.add(productVo);
        }
        return productVos;
    }
}
