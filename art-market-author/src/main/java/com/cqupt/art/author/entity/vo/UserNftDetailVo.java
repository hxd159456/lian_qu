package com.cqupt.art.author.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserNftDetailVo {
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private Integer totalSupply;
    private Integer localId;
    private String contractAddress;
    private String txHash;
    private String description;
    private boolean onSail;
}
