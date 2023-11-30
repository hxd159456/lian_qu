package com.cqupt.art.order.service;

import com.cqupt.art.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.order.entity.vo.AlipayAsyncVo;
import com.cqupt.art.order.entity.vo.PayVo;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@Service
@LocalTCC
public interface OrderService extends IService<Order> {

    void createSeckillOrder(SeckillOrderTo orderTo);

    PayVo getOrderPay(String orderSn,String goodsId,String name);

    boolean handlerPayResult(AlipayAsyncVo alipayAsyncVo);


    void releaseOrder(String msg);

    void transferByMQ(String orderSn);
}
