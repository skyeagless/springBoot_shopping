package com.skyeagle.mall.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {
    SUCCESS(0,"成功"),
    PASSWORD_ERROR(1,"密码错误"),
    USER_HAVE_BEEN_EXIST(2,"用户名已经存在"),
    NOT_LOGIN(10,"用户未登陆"),
    SERVER_ERROR(-1,"服务器异常"),
    PARAM_ERROR(3,"参数错误"),
    EMAIL_EXIST(4,"email已存在"),
    USERNAME_OR_PASSWORD_ERROR(11,"用户名或者密码错误"),
    PRODUCT_OFF_SALE_OR_DELETE(12,"商品下架或者删除"),
    PRODUCT_NOT_EXISTED(13,"商品不存在"),
    STOCK_NOT_ENOUGH(14,"库存有误"),
    CART_PRODUCT_NOT_EXISTED(15,"购物车商品不存在"),
    DELETE_FAILED(16,"删除失败"),
    SHIPPING_NOT_EXIST(17,"收货地址不存在"),
    CART_SELECTED_IS_EMPTY(18,"没有商品被选中"),
    STOCK_IS_NOT_ENOUGH(19,"商品库存不足"),
    NOT_ON_SALE(20,"商品下架或者已经删除"),
    ORDER_NOT_EXISTED(21,"订单不存在"),
    CANNOT_CANCELED(22,"无法被取消"),
    ;

    Integer code;
    String desc;

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
