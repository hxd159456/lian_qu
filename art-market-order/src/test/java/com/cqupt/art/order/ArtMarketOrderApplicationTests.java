package com.cqupt.art.order;

import com.cqupt.art.constant.SeckillOrderMqConstant;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ArtMarketOrderApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testRelease(){
        SeckillOrderTo orderTo = new SeckillOrderTo();
        String orderSn = UUID.randomUUID().toString().replace("-", "");
        orderTo.setOrderSn(orderSn);
        orderTo.setPrice(new BigDecimal("19.90"));
        orderTo.setBuyUserId("2");
        orderTo.setGoodsId("154");
        orderTo.setToken("test_token_123");
        rabbitTemplate.convertAndSend(SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE,
                SeckillOrderMqConstant.CREATE_SECKILL_ORDER_ROUTING_KEY,orderTo);
    }

}
