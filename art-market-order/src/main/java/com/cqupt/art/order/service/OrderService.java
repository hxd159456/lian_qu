package com.cqupt.art.order.service;

import com.cqupt.art.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.order.entity.to.SeckillOrderTo;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
public interface OrderService extends IService<Order> {

    void createSeckillOrder(SeckillOrderTo orderTo);
}
