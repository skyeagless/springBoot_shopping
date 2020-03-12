package com.skyeagle.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * "productId": 1,
 * "quantity": 12,
 * "productName": "iphone7",
 * "productSubtitle": "双十一促销",
 * "productMainImage": "mainimage.jpg",
 * "productPrice": 7199.22,
 * "productStatus": 1,
 * "productTotalPrice": 86390.64,
 * "productStock": 86,
 * "productSelected": true
* */
@Data
public class CartProductVo {
    private Integer productId;

    //购买的数量
    private Integer quantity;

    private String productName;

    private String productSubtitle;

    private String productMainImage;

    private BigDecimal productPrice;

    private Integer productStatus;

    private BigDecimal productTotalPrice;

    //库存
    private Integer productStock;

    //商品是否选中
    private Boolean productSelected;

    public CartProductVo(Integer productId, Integer quantity, String productName, String productSubtitle, String productMainImage, BigDecimal productPrice, Integer productStatus, BigDecimal productTotalPrice, Integer productStock, Boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.productMainImage = productMainImage;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
        this.productTotalPrice = productTotalPrice;
        this.productStock = productStock;
        this.productSelected = productSelected;
    }
}
