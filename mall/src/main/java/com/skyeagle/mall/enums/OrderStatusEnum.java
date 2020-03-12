package com.skyeagle.mall.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    CANCEL(0,"已取消"),
    NOT_PAID(10,"未付款"),
    HAS_BEEN_PAID(20,"已经付款"),
    HAS_BEEN_SENT(40,"已经发货"),
    TRADE_SUCCESS(50,"交易成功"),
    TRADE_CLOSED(60,"交易关闭"),;

    Integer code;
    String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
