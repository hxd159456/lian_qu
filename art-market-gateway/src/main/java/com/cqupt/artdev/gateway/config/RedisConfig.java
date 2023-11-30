package com.cqupt.artdev.gateway.config;

import io.lettuce.core.ClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

import static org.bouncycastle.cms.RecipientId.password;

@Configuration
//@EnableRedisWebSession(maxInactiveIntervalInSeconds = 10*60*60, redisNamespace = "my:spring:session")
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("10.16.19.243", 6379);
//        configuration.setPassword(RedisPassword.of(password));
        configuration.setDatabase(1);
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(ClientOptions.builder().build())
                .build();
        return new LettuceConnectionFactory(configuration,clientConfiguration);
    }


    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // 配置Redis序列化器
        return new GenericJackson2JsonRedisSerializer();
    }
    //应为映入的jar包已经有这个sessionRepository实例注入了，所以这里我随便写了一个，
    @Bean(name="sessionRepository1")
    public RedisIndexedSessionRepository sessionRepository(RedisSerializer<Object> springSessionDefaultRedisSerializer) {
        // 配置RedisSessionRepository

        RedisIndexedSessionRepository sessionRepository = new RedisIndexedSessionRepository(redisOperations());
        //sessionRepository.setRedisKeyNamespace("constellation:session:id");
        sessionRepository.setDefaultSerializer(springSessionDefaultRedisSerializer);
        return sessionRepository;
    }
    @Bean
    public RedisOperations<Object, Object> redisOperations() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        redisTemplate.setKeySerializer(springSessionDefaultRedisKeySerializer());
        redisTemplate.setValueSerializer(springSessionDefaultRedisSerializer());
        redisTemplate.setHashKeySerializer(springSessionDefaultRedisKeySerializer());
        redisTemplate.setHashValueSerializer(springSessionDefaultRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    @Bean
    public RedisSerializer<String> springSessionDefaultRedisKeySerializer() {
        return new StringRedisSerializer();
    }
}
