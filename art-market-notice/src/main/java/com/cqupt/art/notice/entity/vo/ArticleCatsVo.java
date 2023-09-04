package com.cqupt.art.notice.entity.vo;

import com.cqupt.art.notice.entity.PmArticleCats;
import lombok.Data;

import java.util.List;
import java.util.Map;


public class ArticleCatsVo {
    List<PmArticleCats> items;
    Map<Integer, String> parentMap;

    public List<PmArticleCats> getItems() {
        return items;
    }

    public void setItems(List<PmArticleCats> items) {
        this.items = items;
    }

    public Map<Integer, String> getParentMap() {
        return parentMap;
    }

    public void setParentMap(Map<Integer, String> parentMap) {
        this.parentMap = parentMap;
    }
}
