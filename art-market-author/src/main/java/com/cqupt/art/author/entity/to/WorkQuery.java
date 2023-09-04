package com.cqupt.art.author.entity.to;

import lombok.Data;

@Data
public class WorkQuery {
    private String keyword;
    private Long authorId;
    private Integer curPage;
    private Integer limit;
}
