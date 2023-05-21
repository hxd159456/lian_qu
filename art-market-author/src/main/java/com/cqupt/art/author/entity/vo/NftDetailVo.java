package com.cqupt.art.author.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class NftDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String authorName;
    private String avatarUrl; //作者头像
    private String contractAddress;
    private Integer totalSupply;
    private String name;
    private String imageUrl;
    private String description;
    private String txHash;
    private Float price;
    private Integer lanuchStatus;
    private String authorDesc;
    private Date startTime;
    private Date endTime;
}
