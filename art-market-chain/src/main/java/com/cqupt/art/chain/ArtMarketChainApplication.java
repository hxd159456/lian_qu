package com.cqupt.art.chain;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ArtMarketChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtMarketChainApplication.class, args);
    }

}
