package com.cqupt.art.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.notice.constant.CacheConst;
import com.cqupt.art.exception.RRException;
import com.cqupt.art.notice.entity.PmArticles;
import com.cqupt.art.notice.mapper.PmArticlesMapper;
import com.cqupt.art.notice.service.PmArticlesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 文章记录表 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-07
 */
@Service
public class PmArticlesServiceImpl extends ServiceImpl<PmArticlesMapper, PmArticles> implements PmArticlesService {
    
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;
    public void publishNotice(PmArticles article){
        if (article.getPublishTime() == null) {
            throw new RRException("发布时间不能为空！",500);
        }
        // 更新数据库
        this.save(article);
        // 更新缓存
        redisTemplate.opsForValue().set(CacheConst.NOTICE_CACHE_PREFIX+article.getArticleId(),
                JSON.toJSONString(article),7, TimeUnit.DAYS);
    }

    @Override
    public PmArticles getNoticeDetail(String articleId) {
        PmArticles pmArticles = null;
        String articleCache = redisTemplate.opsForValue().get(CacheConst.NOTICE_CACHE_PREFIX + articleId);
        if(!StringUtils.isEmpty(articleCache)){
            pmArticles = JSON.parseObject(articleCache,PmArticles.class);
        }else{
            pmArticles = this.getById(articleId);
        }
        if(pmArticles.getPublishTime().getTime()<=System.currentTimeMillis()){
            return pmArticles;
        }else{
            return null;
        }
    }

    // 只有管理员能修改，使用分布式锁保证一致性
    // 公告会直接影响玩家，是核心，一致性要求高一些
    @Override
    public void updateArticle(PmArticles article) {
        RLock lock = redissonClient.getLock("admin-update-notice-lock");
        try {
            boolean locked = lock.tryLock();
            if(locked){
                this.updateById(article);
                redisTemplate.opsForValue()
                        .set(CacheConst.NOTICE_CACHE_PREFIX+article.getArticleId(),
                        JSON.toJSONString(article));
            }
        }finally {
            lock.unlock();
        }
    }
}
