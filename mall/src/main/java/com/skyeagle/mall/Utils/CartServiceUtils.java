package com.skyeagle.mall.Utils;

import com.skyeagle.mall.entity.Cart;
import lombok.Data;
import org.springframework.data.redis.core.HashOperations;

import java.util.List;

@Data
public class CartServiceUtils {
    private List<Cart> cartList;
    HashOperations<String,String,String> opsForHash;
    String redisKey;

}
