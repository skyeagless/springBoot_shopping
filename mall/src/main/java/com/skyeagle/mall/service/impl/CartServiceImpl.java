package com.skyeagle.mall.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.skyeagle.mall.Utils.CartServiceUtils;
import com.skyeagle.mall.dao.ProductMapper;
import com.skyeagle.mall.entity.Cart;
import com.skyeagle.mall.entity.Product;
import com.skyeagle.mall.enums.ProductStatusEnum;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.form.CartAddForm;
import com.skyeagle.mall.form.CartUpdateForm;
import com.skyeagle.mall.service.ICartService;
import com.skyeagle.mall.vo.CartProductVo;
import com.skyeagle.mall.vo.CartVo;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CartServiceImpl implements ICartService {
    private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseVo<CartVo> add(CartAddForm form,Integer uid) {
        Product product = productMapper.selectByPrimaryKey(form.getProductId());
        Integer quantity = 1;
        Cart cart;
        //1.判断商品是否存在
        if(product == null){
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXISTED);
        }
        //2.商品是否正常在售
        if(!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        //3.判断商品库存是否充足(加入购物车只需要判断1件商品)
        if(product.getStock() <= 0 ){
            return ResponseVo.error(ResponseEnum.STOCK_NOT_ENOUGH);
        }
        //写入redis key:cart_1
        //将对象转成json
        //key--productId(Hkey)--Json(HValue)
        HashOperations<String,String,String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        String value = opsForHash.get(redisKey,String.valueOf(product.getId()));
        if(StringUtil.isEmpty(value)){
            //新建一个购物车
            cart = new Cart(product.getId(),quantity,form.getSelected());
        }else{
            cart = new Gson().fromJson(value,Cart.class);
            cart.setQuantity(cart.getQuantity()+quantity);
        }

        opsForHash.put(redisKey,
                String.valueOf(product.getId()),new Gson().toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        HashOperations<String,String,String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        CartVo cartVo =  new CartVo();
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = new BigDecimal(0);


        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = new Gson().fromJson(entry.getValue(),Cart.class);

            //先拿数据库的内容
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product != null){
                CartProductVo cartProductVo = new CartProductVo(
                        productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()
                );

                if(!cart.getProductSelected()){
                    selectAll = false;
                }

                //只计算选中的
                if(cart.getProductSelected()){
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());

                }
                cartProductVoList.add(cartProductVo);
            }
            cartTotalQuantity += cart.getQuantity();
        }

        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm) {
        HashOperations<String,String,String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        String value = opsForHash.get(redisKey,String.valueOf(productId));
        Cart cart;

        if(StringUtil.isEmpty(value)){
            //redis中找不到这个商品
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXISTED);
        }
        //修改内容
        else
        {
            cart = new Gson().fromJson(value,Cart.class);
            if(cartUpdateForm.getQuantity()!= null &&
            cartUpdateForm.getQuantity() >= 0){
                cart.setQuantity(cartUpdateForm.getQuantity());
            }
            if(cartUpdateForm.getSelected()!= null){
                cart.setProductSelected(cartUpdateForm.getSelected());
            }
        }

        opsForHash.put(redisKey,
                String.valueOf(productId),new Gson().toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String,String,String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        String value = opsForHash.get(redisKey,String.valueOf(productId));

        if(StringUtil.isEmpty(value)){
            //redis中找不到这个商品
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXISTED);
        }
        opsForHash.delete(redisKey,String.valueOf(productId));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        CartServiceUtils cartServiceUtils = cartServiceCommon(uid);
        //重新序列化，存入redis
        for(Cart cart:cartServiceUtils.getCartList()){
            cart.setProductSelected(true);
            cartServiceUtils.getOpsForHash().put(cartServiceUtils.getRedisKey(),String.valueOf(cart.getProductId()),new Gson().toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unselectAll(Integer uid) {
        CartServiceUtils cartServiceUtils = cartServiceCommon(uid);
        //重新序列化，存入redis
        for(Cart cart:cartServiceUtils.getCartList()){
            cart.setProductSelected(false);
            cartServiceUtils.getOpsForHash().put(cartServiceUtils.getRedisKey(),String.valueOf(cart.getProductId()),new Gson().toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer cartTotalQuantity = 0;

        CartServiceUtils cartServiceUtils = cartServiceCommon(uid);

        //重新序列化，存入redis
        for(Cart cart:cartServiceUtils.getCartList()){
            if(cart.getProductSelected()){
                cartTotalQuantity += cart.getQuantity();
            }

        }

        return ResponseVo.success(cartTotalQuantity);
    }

    public CartServiceUtils cartServiceCommon(Integer uid){
        CartServiceUtils cartServiceUtils = new CartServiceUtils();

        HashOperations<String,String,String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        //反序列化，存入数组
        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(new Gson().fromJson(entry.getValue(),Cart.class));
        }

        cartServiceUtils.setCartList(cartList);
        cartServiceUtils.setOpsForHash(opsForHash);
        cartServiceUtils.setRedisKey(redisKey);
        return cartServiceUtils;
    }



}
