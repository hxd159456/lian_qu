package com.cqupt.art.controller;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.config.LoginInterceptor;
import com.cqupt.art.entity.User;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@Slf4j
public class TestController {
    int serverId = 2;

    @GetMapping("/user/test")
    public R test() {
        User user = LoginInterceptor.threadLocal.get();
        log.info("controller中的数据：{}", JSON.toJSONString(user));
        return R.ok().put("loginUser", user);
    }
    @GetMapping("/test/v6")
    public R testV6(){
        return R.ok().put("msg","Hello Ipv6");
    }

    @GetMapping("/test/lb")
    public R testLb(){
        return R.ok().put("serverId",2);
    }

    @GetMapping("/test/setCookie")
    public R setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("test","same");
        cookie.setPath("/");
        response.addCookie(cookie);
        return R.ok().put("cookie",JSON.toJSONString(cookie));
    }
    @GetMapping("getCookie")
    public String getCookie(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length >0) {
            for (Cookie cookie : cookies) {
                System.out.println("name:" + cookie.getName() + "-----value:" + cookie.getValue());
            }
        }
        return "success";
    }
}
