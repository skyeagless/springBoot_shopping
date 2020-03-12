package com.imooc.pay.Enum;

import com.lly835.bestpay.enums.BestPayTypeEnum;


public enum PayPlatFormEnum {
    ALIPAY(1),WX(2);
    Integer code;

    PayPlatFormEnum(Integer code) {
        this.code = code;
    }

    public static PayPlatFormEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum){
        if(bestPayTypeEnum.getPlatform().name().equals(PayPlatFormEnum.ALIPAY.name())){
            return PayPlatFormEnum.ALIPAY;
        }else if(bestPayTypeEnum.getPlatform().name().equals(PayPlatFormEnum.WX.name())){
            return PayPlatFormEnum.WX;
        }
        throw new RuntimeException("找不到支付平台:"+ bestPayTypeEnum.name());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
