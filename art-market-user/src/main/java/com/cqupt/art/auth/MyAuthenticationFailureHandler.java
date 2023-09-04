//package com.cqupt.art.auth;
//
//import com.alibaba.fastjson.JSON;
//import com.cqupt.art.utils.R;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(JSON.toJSONString(R.userLoginFail()));
//    }
//}
