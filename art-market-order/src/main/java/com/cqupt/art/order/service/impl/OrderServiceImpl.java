package com.cqupt.art.order.service.impl;

import com.cqupt.art.order.dao.OrderMapper;
import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        Order order = new Order();
        BeanUtils.copyProperties(orderTo, order);

        //卖方id为0为首发订单
        order.setSellUserId("0");
        order.setNum(1);
        //每次只能买一个
        order.setSumPrice(orderTo.getPrice());
        //不发优惠卷
        order.setPayMoney(orderTo.getPrice());
        order.setStatus(1);
        this.save(order);
        //秒杀限制了总量，所以库存是不会出问题的，所以不用先锁库存，支付成功了锁库存就行了
    }
}
