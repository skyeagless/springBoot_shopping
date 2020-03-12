package com.imooc.pay.service.impl;

import com.imooc.pay.Enum.PayPlatFormEnum;
import com.imooc.pay.dao.PayInfoMapper;
import com.imooc.pay.entity.PayInfo;
import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Slf4j
@Service
public class PayService implements IPayService {
    @Autowired
    private BestPayService bestPayService;
    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public PayResponse create(String orderId, BigDecimal amount,
                              BestPayTypeEnum bestPayTypeEnum) {
        if(bestPayTypeEnum != BestPayTypeEnum.WXPAY_NATIVE
        && bestPayTypeEnum != BestPayTypeEnum.ALIPAY_PC){
            throw new RuntimeException("暂不支持的支付类型");
        }
        //需要写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatFormEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),amount);
        payInfoMapper.insert(payInfo);

        //支付参数传输
        PayRequest request = new PayRequest();
        request.setOrderName("34522525-435325");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(bestPayTypeEnum);

        PayResponse response = bestPayService.pay(request);
        log.info("response={}",response);
        return response;
    }

    @Override
    @ResponseBody
    public String asyncNotify(String notifyData) {
        //1.签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}",payResponse);
        //2.从数据库查订单，金额校验
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if(payInfo == null){
            throw new RuntimeException("通过orderNo查询到的结果是NULL");
        }
        //支付状态不是已支付
        if(!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            if(payInfo.getPayAmount().
                    compareTo(BigDecimal.valueOf(payResponse.getOrderAmount()))!=0){
                throw new RuntimeException("金额不一致，orderNo="+payInfo.getOrderNo());
            }
            //3.修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            //更新时间完全用mysql来完成，所以这里并不设置，在mapper里删除
            payInfoMapper.updateByPrimaryKey(payInfo);
        }
        //TODO 4.pay发送MQ消息,mall接受MQ消息


        //5.告诉微信或者支付宝已经接收了通知（这里有个判断，省略了)
        return "支付成功的XML";
    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
