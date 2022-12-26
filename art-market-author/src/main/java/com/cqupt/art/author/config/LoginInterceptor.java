package com.cqupt.art.author.config;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.author.entity.User;
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
        Object o = session.getAttribute("loginUser");
        if (o != null) {
            User loginUser = JSON.parseObject(o.toString(), User.class);
            log.info("拦截器中的数据：{}", JSON.toJSONString(loginUser));
            if (loginUser != null) {
                threadLocal.set(loginUser);
                return true;
            }
        }
//        User loginUser = null;
//        if(StringUtils.isNotBlank(loginUserJsonString)){
//             loginUser = JSON.parseObject(loginUserJsonString, User.class);
//        }
        log.info("跳转登录！");
//        response.addHeader("Access-Control-Allow-Origin","*");
//        response.sendRedirect("http://art-meta.top:9090/#/login");
        response.sendError(401, "用户未登录！");
        return false;


    }
}
