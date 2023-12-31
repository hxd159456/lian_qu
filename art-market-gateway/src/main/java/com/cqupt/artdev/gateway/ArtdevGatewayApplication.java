package com.cqupt.artdev.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

import java.util.HashMap;
import java.util.Map;

@EnableDiscoveryClient
@EnableRedisWebSession(saveMode = SaveMode.ALWAYS)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ArtdevGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArtdevGatewayApplication.class, args);
    }
}
