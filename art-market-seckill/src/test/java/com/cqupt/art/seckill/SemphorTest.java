package com.cqupt.art.seckill;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class SemphorTest {
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void setSempoher(){
        log.info("================================启动成功================");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        RSemaphore semaphore = redissonClient.getSemaphore("nft:seckill:stock:");
        semaphore.trySetPermits(1);
        new Thread(()->{
            while (true) {
                RSemaphore semaphore1 = redissonClient.getSemaphore("nft:seckill:stock:");
                try {
                    boolean hasStock = semaphore1.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
                    if (hasStock) {
                        log.info(Thread.currentThread().getName() + "\t 获取库存成功！");
                    } else {
                        log.info(Thread.currentThread().getName() + "\t 获取库存失败！");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"t1").start();

        new Thread(()->{
            while (true){
                RSemaphore semaphore1 = redissonClient.getSemaphore("nft:seckill:stock:");
                try {
                    boolean hasStock = semaphore1.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
                    if(hasStock){
                        log.info(Thread.currentThread().getName()+"\t 获取库存成功！");
                    }else{
                        log.info(Thread.currentThread().getName()+"\t 获取库存失败！");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"t2").start();
    }
}
