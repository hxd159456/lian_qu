package com.cqupt.art.order.entity.to;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jnr.ffi.annotations.In;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransferLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String nftId;

    private String fromUid;

    private String toUid;

    private Integer type;

    private Integer localId;

    private BigDecimal price;

    private String txHash;

    private Date createTime;

    private Date updateTime;

}
