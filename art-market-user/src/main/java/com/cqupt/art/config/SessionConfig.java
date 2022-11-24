package com.cqupt.art.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@Slf4j
public class SessionConfig {
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        log.info("自定义Session序列化");
        return new FastJsonRedisSerializer(Object.class);
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("ARTSESSIONID");
        serializer.setDomainName("10.17.156.253");
        return serializer;
    }
}
