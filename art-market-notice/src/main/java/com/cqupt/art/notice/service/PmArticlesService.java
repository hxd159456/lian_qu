package com.cqupt.art.notice.service;

import com.cqupt.art.notice.entity.PmArticles;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 文章记录表 服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-07
 */
public interface PmArticlesService extends IService<PmArticles> {
    void publishNotice(PmArticles article);

    PmArticles getNoticeDetail(String articleId);

    void updateArticle(PmArticles article);

    List<PmArticles> shouldCached();

}
