package com.cqupt.art.seckill.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class MyInterceptorAop {

    @Pointcut("execution(* com.cqupt.art.seckill.app.*.*(..))")
    public void pointcut(){}


    @Before("pointcut()")
    public void preHandle(JoinPoint joinPoint){
        log.info("前置拦截");
    }

    @After("pointcut()")
    public void afterHandle(){
        log.info("后置拦截");
    }
}
