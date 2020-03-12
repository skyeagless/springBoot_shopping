package com.imooc.pay.service;

import com.imooc.pay.entity.PayInfo;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;

import java.math.BigDecimal;

public interface IPayService {
    //创建,发起支付
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);
    //异步通知
    String asyncNotify(String notifyData);

    PayInfo queryByOrderId(String orderId);
}
