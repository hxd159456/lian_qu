package com.cqupt.art.author.entity.vo;

import lombok.Data;

/**
 * 用户藏品视图
 */
@Data
public class TokenVo {
    private String id;
    private String name;
    private String image;
    private Integer count;
    private Integer onSail;
    private Integer totalSupply;

}
