package com.cqupt.art.notice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.exception.RRException;
import com.cqupt.art.notice.constant.CacheConst;
import com.cqupt.art.notice.entity.PmArticles;
import com.cqupt.art.notice.mapper.PmArticlesMapper;
import com.cqupt.art.notice.service.PmArticlesService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
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
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedisTemplate<String,PmArticles> redisTemplate;

    @Autowired
    RedissonClient redissonClient;


    @Autowired
    PmArticlesMapper articlesMapper;
    public void publishNotice(PmArticles article){
        if (article.getPublishTime() == null) {
            throw new RRException("发布时间不能为空！",500);
        }
        // 更新数据库
        this.save(article);
        stringRedisTemplate.opsForHash().put(CacheConst.NOTICE_CACHE_PREFIX+"list",article.getArticleId(),article.getArticleTitle());
        // 更新缓存
//        stringRedisTemplate.opsForValue().set(CacheConst.NOTICE_CACHE_PREFIX+"detail:"+article.getArticleId(),
//                JSON.toJSONString(article),7, TimeUnit.DAYS);
    }

    // 旁路写回的写回：使用分布式锁
    @Override
    public PmArticles getNoticeDetail(String articleId) {
        PmArticles pmArticles = null;
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(CacheConst.NOTICE_CACHE_PREFIX + "list");
        if(keys!=null && keys.contains(articleId))
//        List<String> range = stringRedisTemplate.opsForList().range(CacheConst.NOTICE_CACHE_PREFIX + "list", 0, -1);
//        if(range!=null&&range.contains(articleId))
        {
            pmArticles = redisTemplate.opsForValue().get(CacheConst.NOTICE_CACHE_PREFIX+"detail:"+articleId);
            if(pmArticles==null){
                RLock lock = redissonClient.getLock("nft-cache-notice-detail-rebuild");
                try {
                    boolean locked = lock.tryLock(1000, TimeUnit.MILLISECONDS);
                    if(locked){
                        pmArticles = this.getById(articleId);
                        redisTemplate.opsForValue().set(CacheConst.NOTICE_CACHE_PREFIX+"detail:"+articleId,pmArticles);
                    }else{
                        TimeUnit.MILLISECONDS.sleep(1000);
                        getNoticeDetail(articleId);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        }else{
            pmArticles = this.getById(articleId);
        }
        return pmArticles;
    }

    // 只有管理员能修改，使用分布式锁保证一致性
    // 公告会直接影响玩家，是核心，一致性要求高一些
    @Override
    public void updateArticle(PmArticles article) {
        this.updateById(article);
        redisTemplate.delete(CacheConst.NOTICE_CACHE_PREFIX+"detail:"+article.getArticleId());
    }

    @Override
    public List<PmArticles> shouldCached() {
        return articlesMapper.shouldCached();
    }
}
