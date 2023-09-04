package com.cqupt.art.order.config.mq;

import com.cqupt.art.constant.SeckillOrderMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SeckillOrderMqConfig {

    //死信队列
    @Bean
    public Queue createSecKillOrrderQueue() {
        return new Queue(SeckillOrderMqConstant.SECKILL_CREATE_ORDER_QUEUE,
                true, false, false);
    }

    //交换机
    @Bean
    public Exchange mintExchange() {
        return ExchangeBuilder.topicExchange(SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE)
                .durable(true).build();
    }

    @Bean
    public Binding seckillOrderCreateBinding() {
        return new Binding(SeckillOrderMqConstant.SECKILL_CREATE_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE,
                SeckillOrderMqConstant.CREATE_SECKILL_ORDER_ROUTING_KEY,
                null
        );
    }

    // 订单消息发送到这里，延迟订单时间后发到关闭订单的队列
    @Bean
    public Queue delayOrderQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",
                SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE);//死信交换机
        // 死信发到那哪个队列，此处设置的是路由键
        args.put("x-dead-letter-routing-key", SeckillOrderMqConstant.RELEASE_SECKILL_ORDER_ROUTING_KEY);
        args.put("x-message-ttl", 60000*3); // 消息过期时间1分钟
        return new Queue(
                SeckillOrderMqConstant.SECKILL_ORDER_DELAY_QUEUE,
                true,false,false,
                args
        );
    }

    // 绑定延迟队列
    @Bean
    public Binding orderDelayBinding(){
        return new Binding(
                SeckillOrderMqConstant.SECKILL_ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE,
                SeckillOrderMqConstant.SECKILL_ORDER_DELAY_ROUTING_KEY,
                null
        );
    }

    @Bean
    public Binding releaseSeckillOrderBinding(){
        return new Binding(
                SeckillOrderMqConstant.SECKILL_RELEASE_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE,
                SeckillOrderMqConstant.RELEASE_SECKILL_ORDER_ROUTING_KEY,
                null
        );
    }

    //解锁库存的队列，死信会被路由到这里
    @Bean
    public Queue releaseOrderQueue(){
        return new Queue(SeckillOrderMqConstant.SECKILL_RELEASE_ORDER_QUEUE,
                true,false,false);
    }
}
