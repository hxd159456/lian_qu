package com.cqupt.art.author.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class NftItem {
    private Integer localId;
    private BigDecimal price;
    private boolean sail;
}
