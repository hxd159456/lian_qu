package com.cqupt.art.seckill.config;

import com.cqupt.art.seckill.entity.to.NftDetailRedisTo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class LocalCacheConfig {
    @Bean
    @Qualifier(value = "seckillLocalCache")
    public Map<String, NftDetailRedisTo> localCache(){
        return new HashMap<String,NftDetailRedisTo>();
    }
}
