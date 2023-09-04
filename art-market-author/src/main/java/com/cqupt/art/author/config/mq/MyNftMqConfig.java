package com.cqupt.art.author.config.mq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.StringReader;

@Configuration
public class MyNftMqConfig {
    public static final String QUEUE_MINT_PRODUCT = "queue.mint.nft.product";
    public static final String MINT_EXCHANGE = "mint.nft.exchange";
    public static final String MINT_PRODUCT_ROUTING_KEY = "chain.mint.product.queue";

    @Bean(MINT_EXCHANGE)
    public Exchange mintExchange() {
        return ExchangeBuilder.directExchange(MyNftMqConfig.MINT_EXCHANGE).durable(true).build();
    }

    @Bean(QUEUE_MINT_PRODUCT)
    public Queue queueMintProduct() {
        return new Queue(QUEUE_MINT_PRODUCT, true, false, false);
    }


    @Bean
    public Binding mintProductBinding() {
        return new Binding(QUEUE_MINT_PRODUCT,
                Binding.DestinationType.QUEUE,
                MINT_EXCHANGE,
                MINT_PRODUCT_ROUTING_KEY, null
        );
    }
}
