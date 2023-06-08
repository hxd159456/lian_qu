package com.cqupt.art.aop;

import com.cqupt.art.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.sql.Time;
import java.time.LocalDateTime;

@Component
@Aspect
public class SysAspect {

    private static final Logger log = LoggerFactory.getLogger(SysAspect.class);

    @Pointcut("@annotation(com.cqupt.art.annotation.Log)")
    private void pointcut(){
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取增强类和方法信息
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if(method!=null){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String uri = request.getRequestURI();
            Log logAnnotation = method.getAnnotation(Log.class);
            Object loginUser = request.getSession().getAttribute("loginUser");
            Object proceed = joinPoint.proceed();
            log.info("name:{}\t,type:{}\t,uri:{}\t,user:{}\t,time:{}\t,result:{}",
                    logAnnotation.name(),logAnnotation.type(),uri,loginUser, LocalDateTime.now(),proceed);
            return proceed;
        }
        return null;
    }

}
