//package com.cqupt.artdev.gateway.filter;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.cqupt.art.utils.R;
//import com.cqupt.artdev.gateway.auth.pojo.to.UserLoginTo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Collection;
//import java.util.concurrent.atomic.AtomicReference;
//
//@Slf4j
////@Component
//public class AuthGlobalFilter implements GlobalFilter, Ordered {
//
//    @Autowired
//    RedisTemplate<String,String> redisTemplate;
//    private AntPathMatcher antPathMatcher = new AntPathMatcher();
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//        log.info("鉴权拦截器，当前访问路径：{}",path);
//        //用户登录和管理员登录
//        if(antPathMatcher.match("/api/user/auth/login",path)
//                ||antPathMatcher.match("/api/user/auth/admin/login",path)
//                || antPathMatcher.match("/api/user/auth/register",path)
//        ){
//            return chain.filter(exchange);
//        }
//        if(antPathMatcher.match("/api/**",path)){
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            System.out.println(authentication);
//            AtomicReference<UserLoginTo> userLoginTo = null;
//            exchange.getSession().subscribe(webSession -> {
//                Object loginUser =  webSession.getAttribute("loginUser");
//                System.out.println(loginUser.toString());
//                userLoginTo.set(null);
//            });
//            if(userLoginTo.get()!=null){
//                String userType = userLoginTo.get().getUserType();
//                Collection<? extends GrantedAuthority> authorities = userLoginTo.get().getAuthorities();
//                if(authorities.contains(new SimpleGrantedAuthority(path))){
//                    return chain.filter(exchange);
//                }else{
//                    return hasNopermission(exchange.getResponse());
//                }
//            }else{
//                return notLogin(exchange.getResponse());
//            }
//        }
//        return chain.filter(exchange);
//    }
//
//    private Mono<Void> notLogin(ServerHttpResponse response){
//        byte[] bytes = JSON.toJSONString(R.userNotLogin()).getBytes();
//        DataBuffer buffer = response.bufferFactory().wrap(bytes);
//        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//        return response.writeWith(Mono.just(buffer));
//    }
//    private Mono<Void> hasNopermission(ServerHttpResponse response) {
//        byte[] bits = JSONObject.toJSONString(R.hasNoPermission()).getBytes(StandardCharsets.UTF_8);
//        DataBuffer buffer = response.bufferFactory().wrap(bits);
//        //指定编码，否则在浏览器中会中文乱码
//        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//        return response.writeWith(Mono.just(buffer));
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
