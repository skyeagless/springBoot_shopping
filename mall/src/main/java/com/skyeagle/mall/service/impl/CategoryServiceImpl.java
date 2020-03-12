package com.skyeagle.mall.service.impl;
import com.skyeagle.mall.dao.CategoryMapper;
import com.skyeagle.mall.entity.Category;
import com.skyeagle.mall.service.ICategoryService;
import com.skyeagle.mall.vo.CategoryVo;
import com.skyeagle.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVo<List<CategoryVo>> selectAllCategory() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVo> categoryVoList = new ArrayList<>();
        //查出parent_id = 0
        for(Category category: categories){
            if(category.getParentId().equals(0)){
                CategoryVo categoryVo = Category2CategoryVo(category);
                categoryVoList.add(categoryVo);
            }
        }
        //一级目录排序
        categoryVoList.sort(Comparator.comparing(
                CategoryVo::getSortOrder
        ).reversed());
        //查询子目录
        findSubCategory(categoryVoList,categories);
        return ResponseVo.success(categoryVoList);
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id,resultSet,categories);
    }

    //防止过多的mapper调用
    private void findSubCategoryId(Integer id, Set<Integer> resultSet,List<Category> categories) {
        for(Category category: categories){
            //是子目录
            if(category.getParentId().equals(id)){
                resultSet.add(category.getId());
                //递归
                findSubCategoryId(category.getId(),resultSet,categories);
            }
        }
    }


    private void findSubCategory(List<CategoryVo> categoryVoList,
                                 List<Category> categories){
        if(categoryVoList.isEmpty()){
            return;
        }

        for(CategoryVo categoryVo: categoryVoList){
            //子目录List
            List<CategoryVo> subCategoryList = new ArrayList<>();

            for(Category category: categories){
                if(category.getParentId().equals(categoryVo.getId())){
                    subCategoryList.add(Category2CategoryVo(category));
                }
                //子目录排序(降序，大的在前面）
                subCategoryList.sort(Comparator.comparing(
                        CategoryVo::getSortOrder
                ).reversed());

                //加入到catgoryVoList中
                categoryVo.setSubCategories(subCategoryList);
                //继续递归,找到所有子目录
                findSubCategory(subCategoryList,categories);
            }
        }
    }

    private CategoryVo Category2CategoryVo(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }
}
