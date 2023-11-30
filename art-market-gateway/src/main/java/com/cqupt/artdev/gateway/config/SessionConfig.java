package com.cqupt.artdev.gateway.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//@Configuration
//
@Slf4j
public class SessionConfig {
    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CustomWebSessionIdResolver customWebSessionIdResolver = new CustomWebSessionIdResolver();
        //以下四项配置主要用于跨域调用让客户端处理cookie信息；若同域调用，下面四行可删除
        return customWebSessionIdResolver;
    }

    private static class CustomWebSessionIdResolver extends CookieWebSessionIdResolver {
        // 重写resolve方法 对SESSION进行base64解码
        @Override
        public List<String> resolveSessionIds(ServerWebExchange exchange) {
            MultiValueMap<String, HttpCookie> cookieMap = exchange.getRequest().getCookies();
            // 获取SESSION
            List<HttpCookie> cookies = cookieMap.get(getCookieName());
            if (cookies == null) {
                return Collections.emptyList();
            }
            return cookies.stream().map(HttpCookie::getValue).map(this::base64Decode).collect(Collectors.toList());
        }

        private String base64Decode(String base64Value) {
            try {
                byte[] decodedCookieBytes = Base64.getDecoder().decode(base64Value);
                String decodedCookieString = new String(decodedCookieBytes);
                log.debug("base64Value:{}, decodedCookieString:{} ", base64Value, decodedCookieString);
                return decodedCookieString;
            } catch (Exception ex) {
                //如果转不了base64，就认为原始值就是返回的
                log.debug("Unable to Base64 decode value:{} ", base64Value);
                return base64Value;
            }
        }
    }
}
