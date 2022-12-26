package com.cqupt.art.entity.to;

import lombok.Data;

@Data
public class UserTransferTo {
    private String fromAddress;
    private String toAddress;
    private Long tokenId;
}
