package com.cqupt.art.seckill.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    @Bean
    @Qualifier(value = "threadPoolExcutor")
    public ThreadPoolExecutor buildThreadPoolExcutor(){
        return  new ThreadPoolExecutor(50,100,10, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1000),new ThreadPoolExecutor.CallerRunsPolicy());
    }


}
