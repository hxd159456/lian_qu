package com.cqupt.art.entity.to;

import com.cqupt.art.entity.UserTokenItem;
import lombok.Data;

import java.util.List;

@Data
public class GainTokenTo {
    private String fromUserId;
    private String userId;
    private String artId;
    private Integer count;
    private List<UserTokenItem> tokenItem;
}
