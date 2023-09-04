package com.cqupt.art.seckill.intecpter;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.annotation.AccessLimit;
import com.cqupt.art.exception.RRException;
import com.cqupt.art.seckill.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AccessLimitIntercptor implements HandlerInterceptor {
    static final String ACCESSLIMIT_LOCK_PREFIX = "access:limit:";

    static final String ACCESS_COUNT_PREFIX = "access:limit:count:";

    @Qualifier(value = "originRedisTemplate")
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod targetMethord = (HandlerMethod) handler;
            AccessLimit accessLimit = targetMethord.getMethodAnnotation(AccessLimit.class);
            if(!Objects.isNull(accessLimit)){
                String ip = request.getRemoteAddr();
                //TODO:对ip进行限制
                Object o = request.getSession().getAttribute("loginUser");
                if(o!=null){
                    User loginUser = JSON.parseObject(o.toString(),User.class);

                    String userId = loginUser.getUserId();
                    //限制访问的前缀+用户id+请求路径
                    String lockKey = ACCESSLIMIT_LOCK_PREFIX+userId+request.getRequestURI();
                    // 查询是否被限制
                    Object isLock = redisTemplate.opsForValue().get(lockKey);
                    if(Objects.isNull(isLock)){
                        //未被限制
                        String countKey = ACCESS_COUNT_PREFIX+userId+request.getRequestURI();
                        Object count = redisTemplate.opsForValue().get(countKey);

                        long second = accessLimit.second();
                        long maxTime = accessLimit.maxTime();

                        if(Objects.isNull(count)){
                            //首次访问
                            log.info("首次访问");
                            redisTemplate.opsForValue().set(countKey,1,second, TimeUnit.SECONDS);
                        }else{
                            if((Integer)count<maxTime){
                                redisTemplate.opsForValue().increment(countKey);
                            }else{
                                log.info("{}禁用访问{}",ip,userId);
                                long forbiddenTime = accessLimit.forbiddenTime();
                                //禁用
                                redisTemplate.opsForValue().set(lockKey,1,forbiddenTime,TimeUnit.SECONDS);
                                redisTemplate.delete(countKey);
                                throw new RRException("访问频繁，请稍后重试！",7001);
                            }
                        }
                    }else {
                        throw new RRException("访问频繁，请稍后重试！",7001);
                    }
                }else{
                    throw new RRException("访问频繁，请稍后重试！",7001);
                }
//                String uri = request.getRequestURI();

            }
        }
        return true;
    }
}
