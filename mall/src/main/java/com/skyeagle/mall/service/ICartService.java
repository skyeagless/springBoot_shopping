package com.skyeagle.mall.service;

import com.skyeagle.mall.form.CartAddForm;
import com.skyeagle.mall.form.CartUpdateForm;
import com.skyeagle.mall.vo.CartVo;
import com.skyeagle.mall.vo.ResponseVo;

public interface ICartService {
    ResponseVo<CartVo> add(CartAddForm form, Integer uid);

    ResponseVo<CartVo> list(Integer uid);

    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm);

    ResponseVo<CartVo> delete(Integer uid, Integer productId);

    ResponseVo<CartVo> selectAll(Integer uid);
    ResponseVo<CartVo> unselectAll(Integer uid);

    ResponseVo<Integer> sum(Integer uid);
}
