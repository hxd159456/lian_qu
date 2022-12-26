package com.cqupt.art.chain.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChainMqConfig {
    //队列
    @Bean
    public Queue orderSecKillOrrderQueue() {
        Queue queue = new Queue("nft.seckill.queue.transfer", true, false, false);
        return queue;
    }
    //交换机
    @Bean
    public Exchange mintExchange() {
        return ExchangeBuilder.directExchange("nft-order-event").durable(true).build();
    }
    @Bean
    public Binding mintProductBinding() {
        return new Binding("nft.seckill.queue.transfer",
                Binding.DestinationType.QUEUE,
                "nft-order-event",
                "nft.order.chain.transfer", null
        );
    }

}
