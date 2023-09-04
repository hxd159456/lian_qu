//package com.cqupt.artdev.gateway.auth.handler;
//
//import com.alibaba.fastjson.JSON;
//import com.cqupt.art.utils.R;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
//        Object principal = authentication.getPrincipal();
//        UserDetails userDetails = null;
//        if(principal instanceof UserDetails){
//            userDetails = (UserDetails) principal;
//        }
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(JSON.toJSONString(R.ok("登录成功！").put("loginUser",userDetails)));
//    }
//}
