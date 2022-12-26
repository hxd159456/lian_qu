package com.cqupt.art.order.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class ChainOrderMqConfig {
    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.directExchange("nft-order-event").durable(true).build();
    }

    @Bean
    public Queue queue(){
        return new Queue("nft.order.queue.chain",true,false,false);
    }

    @Bean
    public Binding binding(){
        return new Binding("nft.order.queue.chain",
                Binding.DestinationType.QUEUE,
                "nft-order-event",
                "nft.order.chain.transfer",null);
    }
}
