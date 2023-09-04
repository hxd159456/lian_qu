//package com.cqupt.artdev.gateway.auth.handler;
//
//import com.alibaba.fastjson.JSON;
//import com.cqupt.art.utils.R;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
////处理匿名用户访问无权限资源时的异常
//@Component
//@Slf4j
//public class AnonymousAuthenticationEntryPoint  implements AuthenticationEntryPoint {
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//        log.warn("用户需要登录，访问[{}]失败，AuthenticationException={}", request.getRequestURI(), e);
//        response.getWriter().print(JSON.toJSONString(R.userNotLogin()));
//    }
//}
