package com.cqupt.art.entity.vo;

import lombok.Data;

@Data
public class UserTransferVo {
    //usertoken表的id
    private String userTokenId;
    private Integer localId;
    private String toAddress;
}
