package com.cqupt.art.chain.entity.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChainTransferTo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String transferLogId;
    private String fromUserId;
    private String toUserId;
    private String artId;
    private Integer localId;
}
