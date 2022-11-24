package com.cqupt.art.seckill;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRabbit
@EnableRedisHttpSession
public class ArtMarketSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtMarketSeckillApplication.class, args);
    }

}
