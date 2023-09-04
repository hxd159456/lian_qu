package com.cqupt.art.seckill.service.impl;

import com.alibaba.fastjson2.JSON;
import com.cqupt.art.constant.SeckillConstant;
import com.cqupt.art.constant.SeckillOrderMqConstant;
import com.cqupt.art.exception.RRException;
import com.cqupt.art.seckill.config.LoginInterceptor;
import com.cqupt.art.seckill.entity.User;
import com.cqupt.art.seckill.entity.to.NftDetailRedisTo;
import com.cqupt.art.seckill.entity.to.SeckillOrderTo;
import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;
import com.cqupt.art.seckill.service.SeckillService;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Qualifier(value = "threadPoolExcutor")

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Qualifier(value = "seckillLocalCache")
    @Autowired
    Map<String, NftDetailRedisTo> seckillLocalCache;

    RateLimiter rateLimiter = RateLimiter.create(500);

    @Override
    public String kill(SeckillInfoVo info) throws InterruptedException {
//        //令牌桶限流
//        if(!rateLimiter.tryAcquire(1000,TimeUnit.MILLISECONDS)){
//            log.info("用户【{}】被限流！",LoginInterceptor.threadLocal.get().getUserId());
//            return null;
//        }
        String localCacheKey = info.getName() + "-" + info.getId();
        NftDetailRedisTo to = null;
//        if (seckillLocalCache.containsKey(localCacheKey)) {
//            to = seckillLocalCache.get(localCacheKey);
//        } else {
            String jsonString = redisTemplate.opsForValue().get("nft:seckill:info:detail:" + info.getName() + "-" + info.getId());
//            String jsonString = ops.get(info.getName() + "-" + info.getId());
            if (StringUtils.isNotBlank(jsonString)) {
                to = JSON.parseObject(jsonString, NftDetailRedisTo.class);
                seckillLocalCache.put(localCacheKey, to);
            }
//        }
        if (to != null) {
            String token = info.getToken();
            //验证时间
            long curTime = System.currentTimeMillis();
            if (curTime >= to.getStartTime().getTime() && curTime < to.getEndTime().getTime()) {
                //接口加密，不能简单的通过脚本抢购！配合诸如验证码等操作，可以保证较为公正的抢购体验
                if (to.getToken().equals(token)) {
                    //token对，是在秒杀时间进来的
                    //验证是否已购买过
//                    User user = LoginInterceptor.threadLocal.get();
                    long ttl = to.getEndTime().getTime() - System.currentTimeMillis();
                    //TODO：测试阶段先不限制购买次数
//                    Boolean occupy = redisTemplate.opsForValue()
//                            .setIfAbsent(SeckillConstant.USER_BOUGHT_FLAG + user.getUserId() + "-" + to.getId(),
//                                    "1", ttl, TimeUnit.MILLISECONDS);
                    if (true) {
                        //当前用户没买过
                        // semphore防止超卖
                        RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_SEMAPHORE + to.getToken());
                        boolean acquire = semaphore.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
                        if (acquire) {
                            //发送创建订单的消息
                            SeckillOrderTo orderTo = new SeckillOrderTo();
                            String orderSn = UUID.randomUUID().toString().replace("-", "");
//                        log.info("orderSn: {},userId: {}", orderSn, LoginInterceptor.threadLocal.get().getUserId());
                            orderTo.setOrderSn(orderSn);
                            orderTo.setBuyUserId("1593074422332678146");
                            orderTo.setBuyUserId("888");
                            orderTo.setBuyUserId("777");
                            orderTo.setGoodsId(to.getId().toString());
                            orderTo.setPrice(new BigDecimal(to.getPrice()));
                            orderTo.setToken(to.getToken());
                            // 流量削峰：让数据库按照他的处理能力，从消息队列中拿取消息进行处理。
                            // 避免大量请求占满数据库连接池，同时修改数据库，导致数据库读写性能降低
                            // 异步去执行:这里异步提升是不是不大额？
//                            threadPoolExecutor.execute(() -> {
//                                rabbitTemplate.convertAndSend(SeckillOrderMqConstant.EXCHANGE,
//                                        SeckillOrderMqConstant.ROUTING_KEY, orderTo);
//                            });
                            sendOrderMeassage(orderTo);
                            return orderSn;
                        }
                    }
                }
            }
        } else {
            throw new RRException("本地缓存和redis缓存都失效！", 4000);
        }
        return null;
    }

    // 确认消息发送成功，可以采用事务和确认机制，事务性能下降严重，基本不可用
    // 通常采用消息确认机制  confirmCallBack ——> 投递到exchange触发，ack或nack
    // returnCallback   ——> 投递到queue失败触发
    // 解决方案：业务重发！发送时保存消息（或者用数据库字段标识），失败回调进行重发。
    private void sendOrderMeassage(SeckillOrderTo orderTo){
        redisTemplate.opsForValue().set(SeckillOrderMqConstant.REDIS_SECKILL_ORDER_MESSAGE_PREFIX+orderTo.getOrderSn(),JSON.toJSONString(orderTo));
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(orderTo.getOrderSn());
        threadPoolExecutor.execute(() -> {
            rabbitTemplate.convertAndSend(SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE,
                    SeckillOrderMqConstant.CREATE_SECKILL_ORDER_ROUTING_KEY, orderTo,correlationData);
        });
    }
}
