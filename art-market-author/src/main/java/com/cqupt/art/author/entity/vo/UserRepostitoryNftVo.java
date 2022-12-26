package com.cqupt.art.author.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserRepostitoryNftVo {
    private String id;
    private String name;
    private String image;
    private Integer count;
    private Integer onSail;
    private Integer totalSupply;
//    private List<NftItem> details;
}
