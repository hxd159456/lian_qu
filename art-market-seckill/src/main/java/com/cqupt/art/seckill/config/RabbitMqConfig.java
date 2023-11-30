package com.cqupt.art.seckill.config;

import com.cqupt.art.constant.SeckillOrderMqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class RabbitMqConfig {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Configuration
    static class Converter {
        @Bean
        public MessageConverter messageConverter() {
            return new Jackson2JsonMessageConverter();
        }
    }


    @PostConstruct
    public void initRabbitTemplate() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            // 消息ID需要封装到CorrelationData
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String s) {
                log.info("投递到exchange回调.{}", correlationData);
                if (ack) {
                    // 成功投递，删除缓存
                    redisTemplate.delete(SeckillOrderMqConstant.REDIS_SECKILL_ORDER_MESSAGE_PREFIX + correlationData.getId());
                } else {
                    // 没有成功投递，重试
                    String msg = redisTemplate.opsForValue().get(SeckillOrderMqConstant.REDIS_SECKILL_ORDER_MESSAGE_PREFIX + correlationData.getId());
                    rabbitTemplate.convertAndSend(SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE,
                            SeckillOrderMqConstant.CREATE_SECKILL_ORDER_ROUTING_KEY, msg);
                }
            }
        });

        // 没有路由到队列触发回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                // 是可以拿到消息信息的
//                Body:'{"orderSn":"41f71ebdab2f4eacada6ec4811d12964","buyUserId":"777","goodsId":"888","price":19.8999996185302734375,"token":"sss1234"}'
                log.info("消息体：{},properties:{}", message.getBody(), message.getMessageProperties());
                log.info("消息投递到队列失败：message:{},i:{},s:{},s1:{},s2:{}", message, i, s, s1, s2);
            }
        });
    }
}
