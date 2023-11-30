package com.cqupt.art.notice.controller;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.art.notice.constant.CacheConst;
import com.cqupt.art.notice.entity.PmArticles;
import com.cqupt.art.notice.service.PmArticlesService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章记录表 前端控制器
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-07
 */
@RestController
@RequestMapping("/pm-articles")
@CrossOrigin
@Slf4j
public class PmArticlesController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private PmArticlesService articlesService;

    // 只是保存到了数据库
    @PostMapping("/publishNotice")
    public R publishNotice(@RequestBody PmArticles article) {
        articlesService.publishNotice(article);
        log.info("保存公告成功！");
        return R.ok("发布公告成功！");
    }

    @PostMapping("/updateNotice")
    public R updateNotice(@RequestBody PmArticles article){
        articlesService.updateArticle(article);
        log.info("修改公告成功！");
        return R.ok();
    }

    @GetMapping("/list/{curPage}/{limit}")
    public R list(@PathVariable("curPage") Integer curPage, @PathVariable("limit") Integer limit) {
        log.info("list curPage->{},limit->{}", curPage, limit);
        List<PmArticles> records = null;
        long total = 0;
        if(curPage==0){
            List<String> cacheNotice = redisTemplate.opsForList().range(CacheConst.NOTICE_CACHE_PREFIX + "list", curPage, limit);
            records= cacheNotice.stream().map(cachedNotice -> {
                PmArticles pmArticles = JSON.parseObject(cachedNotice, PmArticles.class);
                return pmArticles;
            }).collect(Collectors.toList());
            String count = redisTemplate.opsForValue().get(CacheConst.NOTICE_CACHE_PREFIX + "count");
            total = Long.parseLong(count);
        }else{
            Page<PmArticles> queryPage = new Page<>(curPage, limit);
            IPage<PmArticles> page = articlesService.page(queryPage);
            records = page.getRecords();
            total = page.getTotal();
        }
        return R.ok().put("items", records).put("total", total);
    }

    @GetMapping("/articleDetail/{id}")
    public R getArticle(@PathVariable("id") String articleId) {
        PmArticles article = articlesService.getNoticeDetail(articleId);
//        PmArticles article = articlesService.getById(articleId);
        if(!Objects.isNull(article)){
            return R.ok().put("data", article);
        }
        return R.error("非法请求！");
    }


}
