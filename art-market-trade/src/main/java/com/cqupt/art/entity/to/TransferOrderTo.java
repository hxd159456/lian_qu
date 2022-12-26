package com.cqupt.art.entity.to;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransferOrderTo {
    private String orderSn;

    private String sellUserId;

    private String buyUserId;


    private String goodsId;


    private BigDecimal price;


    private Integer num;


    private BigDecimal sumPrice;


    private BigDecimal payMoney;


    private Integer status;


    private Date createTime;


    private Date payTime;


    private Date endTime;

    private Integer localId;
}
