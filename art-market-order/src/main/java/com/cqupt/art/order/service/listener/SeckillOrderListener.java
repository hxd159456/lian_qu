package com.cqupt.art.order.service.listener;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.constant.SeckillOrderMqConstant;
import com.cqupt.art.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service
@Slf4j
public class SeckillOrderListener {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    OrderService orderService;

    private final int retryLimit = 3;
    
    @RabbitListener(queues = SeckillOrderMqConstant.SECKILL_CREATE_ORDER_QUEUE)
    @RabbitHandler
    @SneakyThrows
    public void createOrder(SeckillOrderTo orderTo, Message message, Channel channel) {
        log.info("收到秒杀消息：{}", JSON.toJSONString(orderTo));
        long tag = message.getMessageProperties().getDeliveryTag();
        String key = "seckill:create-order:retry:" + orderTo.getOrderSn();
        try {
            orderService.createSeckillOrder(orderTo);
            // 实际确认了，这里拒绝是放到死信队列
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("创建订单失败：{}. \n异常：{}",orderTo.toString(),e.getMessage());
            Boolean hasKey = redisTemplate.hasKey(key);
            if(hasKey){
                redisTemplate.opsForValue().increment(key);
            } else{
                redisTemplate.opsForValue().set(key,"1");
            }
            // 抛出异常，触发重试
            throw e;
//            channel.basicReject(tag,true);
        }finally {
            if(redisTemplate.hasKey(key)){
                String retryCount = redisTemplate.opsForValue().get(key);
                if(Integer.parseInt(retryCount)>retryLimit){
                    // 超过重试次数
                    //接受他，当消费了
                    channel.basicAck(tag,false);
                    //保存消息
                    redisTemplate.opsForValue()
                            .setIfAbsent("seckill:create-order:fail:"+orderTo.getOrderSn(),JSON.toJSONString(orderTo));
                    // 通知开发者
                    // sendMailToDeveloper();
                }
            }
        }
    }

    @RabbitListener(queues = SeckillOrderMqConstant.SECKILL_ORDER_DELAY_QUEUE)
    @RabbitHandler
    @SneakyThrows
    public void handleTransfer(String orderSnAndToken,Channel channel,Message message){
        String orderSn = orderSnAndToken.split("-")[0];
        long tag = message.getMessageProperties().getDeliveryTag();
        log.info("开始处理发货流程！{}",orderSn);
        String key = "seckill:transfer-mq:retry:" + orderSn;
        try {
            orderService.transferByMQ(orderSn);
            channel.basicAck(tag, false);
        }catch (Exception e){
            log.error("发货失败: {} \n {}",orderSn,e);
            Boolean hasKey = redisTemplate.hasKey(key);
            if(hasKey){
                redisTemplate.opsForValue().increment(key);
            }else{
                redisTemplate.opsForValue().set(key,"1");
            }
            throw e;
        }finally {
            if(redisTemplate.hasKey(key)){
                String retryCount = redisTemplate.opsForValue().get(key);
                if(Integer.parseInt(retryCount)>retryLimit){
                    // 超过重试次数
                    //接受他，当消费了
                    channel.basicAck(tag,false);
                    //保存消息
                    redisTemplate.opsForValue().setIfAbsent("seckill:transfer-mq:fail:"+orderSn,orderSn);
                    // 通知开发者
                    // sendMailToDeveloper();
                }
            }
        }
    }

    @RabbitListener(queues = SeckillOrderMqConstant.SECKILL_RELEASE_ORDER_QUEUE)
    @RabbitHandler
    @SneakyThrows
    public void releaseOrder(String msg, Channel channel, Message message){
        long tag = message.getMessageProperties().getDeliveryTag();
//        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("收到过期订单消息,准备关闭订单：{},{}",msg);
        try{
            orderService.releaseOrder(msg);
            channel.basicAck(tag,false);
            log.info("解锁成功！{}",msg);
        }catch (Exception e){
            log.info("解锁订单失败！{}",msg);
            channel.basicNack(tag,false,true);
        }
    }

}
