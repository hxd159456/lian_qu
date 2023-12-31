package com.cqupt.art.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.art.notice.entity.PmArticleCats;
import com.cqupt.art.notice.entity.vo.ArticleCatsVo;
import com.cqupt.art.notice.mapper.PmArticleCatsMapper;
import com.cqupt.art.notice.service.PmArticleCatsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章分类表 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-07
 */
@Service
public class PmArticleCatsServiceImpl extends ServiceImpl<PmArticleCatsMapper, PmArticleCats> implements PmArticleCatsService {

    @Override
    public ArticleCatsVo listVo(Integer curPage, Integer limit) {
        IPage<PmArticleCats> page = this.page(new Page<PmArticleCats>());
        List<PmArticleCats> parentId = baseMapper.selectList(new QueryWrapper<PmArticleCats>().eq("parent_id", 0));
        Map<Integer, String> idNameMap = parentId.stream().collect(Collectors.toMap(PmArticleCats::getCatId, PmArticleCats::getCatName, (k1, k2) -> k1));
        idNameMap.put(0, "根分类");
        ArticleCatsVo vo = new ArticleCatsVo();
        vo.setItems(page.getRecords());
        vo.setParentMap(idNameMap);
        return vo;
    }
}
