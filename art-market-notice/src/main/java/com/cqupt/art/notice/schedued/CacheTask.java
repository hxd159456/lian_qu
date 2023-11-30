package com.cqupt.art.notice.schedued;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.notice.constant.CacheConst;
import com.cqupt.art.notice.entity.PmArticles;
import com.cqupt.art.notice.service.PmArticlesService;
import com.cqupt.art.notice.service.impl.PmArticlesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CacheTask {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    PmArticlesService articlesService;

    @Scheduled(cron = "0 0 12 * * ?")
    public void updateCache(){
        List<PmArticles> list = articlesService.shouldCached();
        List<String> idList = list.stream().map(item -> item.getArticleId().toString()).collect(Collectors.toList());
        redisTemplate.opsForList().leftPushAll(CacheConst.NOTICE_CACHE_PREFIX+"list",idList);
        list.forEach(item->{
            redisTemplate.opsForValue().set(CacheConst.NOTICE_CACHE_PREFIX+"detail:"+item.getArticleId(), JSON.toJSONString(item));
        });
    }


}
