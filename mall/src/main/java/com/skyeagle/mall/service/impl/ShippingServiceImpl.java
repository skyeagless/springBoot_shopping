package com.skyeagle.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.skyeagle.mall.dao.ShippingMapper;
import com.skyeagle.mall.entity.Shipping;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.form.ShippingForm;
import com.skyeagle.mall.service.IShippingService;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResponseVo<Map<String,Integer>> add(Integer uid, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form,shipping);
        shipping.setUserId(uid);

        //因为Id是自动生成的，如果直接用生成的方法是没有的，所以需要在mybatis里面注明
        int row = shippingMapper.insertSelective(shipping);

        if(row == 0){
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
        Map<String,Integer> map =  new HashMap<>();
        map.put("shippingId",shipping.getId());

        ResponseVo<Map<String,Integer>> responseVo = ResponseVo.success(map);
        responseVo.setMsg("新建地址成功");
        return responseVo;

    }

    @Override
    public ResponseVo delete(Integer uid, Integer shippingId) {
        int row = shippingMapper.deleteByIdandUid(uid,shippingId);
        if(row == 0){
            return ResponseVo.error(ResponseEnum.DELETE_FAILED);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form,shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);

        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if(row == 0){
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUid(uid);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ResponseVo.success(pageInfo);
    }
}
