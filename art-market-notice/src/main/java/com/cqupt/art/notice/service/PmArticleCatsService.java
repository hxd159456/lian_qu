package com.cqupt.art.notice.service;

import com.cqupt.art.notice.entity.PmArticleCats;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.notice.entity.vo.ArticleCatsVo;

/**
 * <p>
 * 文章分类表 服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-07
 */
public interface PmArticleCatsService extends IService<PmArticleCats> {

    ArticleCatsVo listVo(Integer curPage, Integer limit);
}
