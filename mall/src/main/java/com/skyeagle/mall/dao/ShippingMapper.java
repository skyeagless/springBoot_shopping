package com.skyeagle.mall.dao;

import com.skyeagle.mall.entity.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByIdandUid(@Param("uid") Integer uid,
                         @Param("shippingId") Integer shippingId);

    List<Shipping> selectByUid(Integer uid);

    Shipping selectByUidandShippingId(@Param("uid") Integer uid,
                                      @Param("shippingId") Integer shippingId);

    //通过orderNo的集合查orderItem
    List<Shipping> selectByShippingIdSet(@Param("shippingIdSet")
                                                 Set<Integer> shippingIdSet);
}