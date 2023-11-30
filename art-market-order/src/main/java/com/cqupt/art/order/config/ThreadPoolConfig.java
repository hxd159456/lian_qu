package com.cqupt.art.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    static int coreSize = Runtime.getRuntime().availableProcessors()-1;
    static int blockingQueueSize = 1024;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(coreSize,2*coreSize,
                1000, TimeUnit.MICROSECONDS,
                new LinkedBlockingDeque<>(blockingQueueSize), new MyThreadNameFactory());
    }

    static class MyThreadNameFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"order-thread-pool-");
        }
    }

}
