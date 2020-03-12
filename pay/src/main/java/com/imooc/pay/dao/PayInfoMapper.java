package com.imooc.pay.dao;

import com.imooc.pay.entity.PayInfo;

import java.util.List;

public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    PayInfo selectByPrimaryKey(Integer id);

    PayInfo selectByOrderNo(Long orderNo);

    List<PayInfo> selectAll();

    int updateByPrimaryKey(PayInfo record);


}