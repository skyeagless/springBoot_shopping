package com.skyeagle.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.skyeagle.mall.dao.OrderItemMapper;
import com.skyeagle.mall.dao.OrderMapper;
import com.skyeagle.mall.dao.ProductMapper;
import com.skyeagle.mall.dao.ShippingMapper;
import com.skyeagle.mall.entity.*;
import com.skyeagle.mall.enums.OrderStatusEnum;
import com.skyeagle.mall.enums.ProductStatusEnum;
import com.skyeagle.mall.enums.ResponseEnum;
import com.skyeagle.mall.service.IOrderService;
import com.skyeagle.mall.vo.OrderItemVo;
import com.skyeagle.mall.vo.OrderVo;
import com.skyeagle.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        //1.收货地址校验
        Shipping shipping = shippingMapper.selectByUidandShippingId(uid, shippingId);
        if(shipping == null){
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        //2.uid-->购物车信息() 校验(是否有商品/库存)
        List<Cart> oriCartList = cartService.cartServiceCommon(uid).getCartList();
        List<Cart> cartList = new ArrayList<>();
        for(Cart cart:oriCartList){
            if(cart.getProductSelected()){
                cartList.add(cart);
            }
        }
        if(cartList.size() == 0){
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }
        //根据productId查数据库（获取所有的productId)
        Set<Integer> productIdSet =  new HashSet<>();
        for (Cart cart : cartList) {
            productIdSet.add(cart.getProductId());
        }
        List<Product> productList =
                productMapper.selectByProductIdSet(productIdSet.size() == 0 ? null : productIdSet);

        //判断商品是否都取出来了
        if(productIdSet.size() != productList.size()){
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXISTED);
        }

        //库存 建立一个productId-->product的Map
        Map<Integer,Product> map = new HashMap<>();
        for(Product product : productList) {
            map.put(product.getId(),product);
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOrderNo();

        //3-4.计算总价格(只计算被选中过的),生成订单，入库-->order/orderItem(事务控制两个表数据同时写入或者同时失败
        for(Cart cart:cartList){
           Product product = map.get(cart.getProductId());
            //商品的上下架状态
            if(!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())){
                return ResponseVo.error(ResponseEnum.NOT_ON_SALE,
                        "商品不在售"+product.getName());
            }
           if(product.getStock() < cart.getQuantity()){
               return ResponseVo.error(ResponseEnum.STOCK_IS_NOT_ENOUGH,
                       "库存不足"+product.getName());
           }

           //对购物车的每一项构造orderItem,order
            OrderItem orderItem = new OrderItem(
                    uid,
                    orderNo,
                    product.getId(),
                    product.getName(),
                    product.getMainImage(),
                    product.getPrice(),
                    cart.getQuantity(),
                    product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()))
            );

            orderItemList.add(orderItem);

            //5.减去库存
            product.setStock(product.getStock()-cart.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if(row <= 0){
                return ResponseVo.error(ResponseEnum.SERVER_ERROR);
            }
        }

        Order order = buildOrder(uid,orderNo,shippingId,orderItemList);
        int row = orderMapper.insertSelective(order);
        if(row <= 0){
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
        int itemrow = orderItemMapper.batchInsert(orderItemList);
        if(itemrow <= 0){
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }


        //6.清除购物车(选中的),redis有事务，但不能回滚
        for(Cart cart: cartList){
            cartService.delete(uid,cart.getProductId());
        }

        //7.返回（OrderVo)
        OrderVo orderVo = buildOrderVo(order,orderItemList,shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid);

        Set<Long> orderNoSet = new HashSet<>();
        Set<Integer> shippingIdSet = new HashSet<>();

        for(Order order:orderList){
            orderNoSet.add(order.getOrderNo());
            shippingIdSet.add(order.getShippingId());
        }
        //
        List<OrderItem> orderItemAllList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        //[]
        List<Shipping> shippingList = shippingMapper.selectByShippingIdSet(shippingIdSet);

        //列表是一个orderVo,然后里面有一个orderItemVo(一个人可以查出好几笔订单）

        List<OrderVo> orderVoList = new ArrayList<>();
        //每一个orderVo,需要找到对应的shippingId和List<OrderItemVo>

        //按照orderNo,将list转换成map(groupby)
        Map<Long,List<OrderItem>> orderItemMap = orderItemAllList.stream().
                collect(Collectors.groupingBy(OrderItem::getOrderNo));

        //一对一
        Map<Integer,Shipping> shippingMap = shippingList.stream().
                collect(Collectors.toMap(Shipping::getId,shipping -> shipping));

        for(Order order: orderList){
            OrderVo orderVo = buildOrderVo(order,orderItemMap.get(order.getOrderNo()),
                    shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }
        //先对POJO进行分页，再setList(...) VO,
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXISTED);
        }

        Set<Long> orderNoSet = new HashSet<>();
        orderNoSet.add(order.getOrderNo());
        List<OrderItem> orderItemList =
                orderItemMapper.selectByOrderNoSet(orderNoSet);

        Shipping shipping =
                shippingMapper.selectByPrimaryKey(order.getShippingId());

        OrderVo orderVo = buildOrderVo(order,orderItemList,shipping);

        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXISTED);
        }
        if(!OrderStatusEnum.NOT_PAID.getCode().equals(order.getStatus())){
            return ResponseVo.error(ResponseEnum.CANNOT_CANCELED);
        }
        order.setStatus(OrderStatusEnum.CANCEL.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if(row <= 0){
            return ResponseVo.error(ResponseEnum.SERVER_ERROR);
        }
        return ResponseVo.success();
    }


    private Order buildOrder(Integer uid,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList) {
        Order order =  new Order();
        order.setUserId(uid);
        order.setOrderNo(orderNo);
        order.setShippingId(shippingId);
        //省略了枚举，这里1代表在线支付
        order.setPaymentType(1);
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NOT_PAID.getCode());

        BigDecimal payment = BigDecimal.valueOf(0);
        for(OrderItem orderItem:orderItemList){
            payment = payment.add(orderItem.getTotalPrice());
        }
        order.setPayment(payment);
        return order;
    }
    //或者使用分布式唯一id
    private Long generateOrderNo() {
        return System.currentTimeMillis()+ new Random().nextInt(999);
    }

    //orderItemList --> orderItemVoList
    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shippingVo) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo);

       List<OrderItemVo> orderItemVoList = new ArrayList<>();
       for(OrderItem orderItem:orderItemList){
           OrderItemVo orderItemVo = new OrderItemVo();
           BeanUtils.copyProperties(orderItem,orderItemVo);
           orderItemVoList.add(orderItemVo);
       }
        orderVo.setOrderItemVoList(orderItemVoList);

       if(shippingVo!=null){
           orderVo.setShippingId(shippingVo.getId());
           orderVo.setShippingVo(shippingVo);
       }
        return orderVo;
    }
}
