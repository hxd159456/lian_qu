package com.cqupt.art.seckill.config;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.seckill.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("请求URI：{}", request.getRequestURI());
        HttpSession session = request.getSession();
        User loginUser = JSON.parseObject(session.getAttribute("loginUser").toString(), User.class);
        log.info("拦截器中的数据：{}", JSON.toJSONString(loginUser));
        if (loginUser != null) {
            threadLocal.set(loginUser);
            return true;
        }
        response.sendRedirect("http://10.17.156.253:8080/#/login");
        return false;
    }
}
