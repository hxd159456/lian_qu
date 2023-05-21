package com.cqupt.art.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解加反射实现接口防刷
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    /**
     * 秒
     * @return 多少秒内
     */
    long second() default 60L;

    /**
     * 最大访问次数
     * @return 最大访问次数
     */
    long maxTime() default 3L;

    /**
     * 触发访问限制禁用时长，单位：秒
     * @return 禁用时长
     */
    long forbiddenTime() default  120L;
}
