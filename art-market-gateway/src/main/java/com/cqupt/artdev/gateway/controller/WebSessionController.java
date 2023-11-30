package com.cqupt.artdev.gateway.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WebSessionController {

    @Resource(name="sessionRepository1")
    RedisIndexedSessionRepository sessionRepository;

    @GetMapping("/websession/test")
    public Mono<Map<Object,Object>> testWebSessionByParam(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "note") String note,
            WebSession session, ServerWebExchange exchange) {
        System.out.println(session.getId());
        exchange.getResponse().addCookie(ResponseCookie.from("JSESSIONID",session.getId()).build());
        session.getAttributes().put("id", id);
        session.getAttributes().put("note", note);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code",200);
        return Mono.just(map);
    }

    @GetMapping("/websession")
    public Mono<Map<Object,Object>> getSession(WebSession session,ServerWebExchange exchange) {

//        session.getAttributes().putIfAbsent("id", 0);
//        session.getAttributes().putIfAbsent("note", "Howdy Cosmic Spheroid!");
        String jsessionid = exchange.getRequest().getCookies().getFirst("JSESSIONID").getValue();
        Session session1 = sessionRepository.findById(jsessionid);
        System.out.println(session.getId());
        Map<Object,Object> map = new HashMap<>();
        map.put("id",session1.getAttribute("id"));
        map.put("note",session1.getAttribute("note"));

        return Mono.just(map);
    }
}
