package com.cqupt.art.order.entity.to;

import lombok.Data;

import java.util.Date;

@Data
public class NftDetailRedisTo {
    private Long id; //v
    private String authorName;
    private String avatarUrl; //作者头像
    private Integer totalSupply;
    private String contractAddress;
    private String name;
    private String imageUrl;
    private String description;
    private String txHash;
    private Float price;
    private String authorDesc;
    private Date startTime;
    private Date endTime;
    private String token;
}
