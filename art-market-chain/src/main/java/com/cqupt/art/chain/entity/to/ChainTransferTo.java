package com.cqupt.art.chain.entity.to;

import lombok.Data;

@Data
public class ChainTransferTo {
    private String fromUserId;
    private String toUserId;
    private String artId;
    private Integer localId;
}
