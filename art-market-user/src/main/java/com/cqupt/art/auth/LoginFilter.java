//package com.cqupt.art.auth;
//
//import com.alibaba.fastjson2.JSON;
//import com.cqupt.art.entity.to.LoginTo;
//import lombok.SneakyThrows;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//public class LoginFilter extends UsernamePasswordAuthenticationFilter {
//    private static final String APPLICATION_JSON_UTF8_VALUE_WITH_SPACE = "application/json; charset=UTF-8";
//    @Override
//    @SneakyThrows
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        // 需要是 POST 请求
//        if (!request.getMethod().equals("POST")) {
//            throw new AuthenticationServiceException(
//                    "Authentication method not supported: " + request.getMethod());
//        }
//        String contentType = request.getContentType();
//        // 判断请求格式是否是 JSON
//        if (MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType) ||
//                APPLICATION_JSON_UTF8_VALUE_WITH_SPACE.equalsIgnoreCase(contentType) ||
//                MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)) {
//            LoginTo loginTo = null;
//            BufferedReader br = null;
//            try {
//                br = new BufferedReader(new InputStreamReader(request.getInputStream()));
//                StringBuilder sb = new StringBuilder();
//                String line;
//                while (((line = br.readLine())!=null)){
//                    sb.append(line);
//                }
//                loginTo = JSON.parseObject(sb.toString(),LoginTo.class);
//            } finally {
//                if(br!=null){
//                    br.close();
//                }
//            }
//            if(StringUtils.isEmpty(loginTo.getPhone())){
//                throw new AuthenticationServiceException("用户名不能为空");
//            }
//            if(StringUtils.isEmpty(loginTo.getPassword())){
//                throw new AuthenticationServiceException("密码不能为空");
//            }
//            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
//                    loginTo.getPhone(), loginTo.getPassword());
//            setDetails(request, authRequest);
//            return this.getAuthenticationManager().authenticate(authRequest);
//        }else {
//            return super.attemptAuthentication(request, response);
//        }
//    }
//
//
//}
