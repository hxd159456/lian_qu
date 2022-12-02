package com.cqupt.art.config.mq;

import com.cqupt.art.constant.SeckillOrderMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMqConfig {

    //队列
    @Bean
    public Queue orderHandle() {
        Queue queue = new Queue("nft.order.handle.queue", true, false, false);
        return queue;
    }

    //交换机
    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.directExchange("nft-order-event").durable(true).build();
    }

    @Bean
    public Binding mintProductBinding() {
        return new Binding("nft.order.handle.queue",
                Binding.DestinationType.QUEUE,
                "nft-order-event",
                "nft.order.transfer.local", null
        );
    }
}
