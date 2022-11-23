package com.cqupt.art.order.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pm_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.ID_WORKER)
    private Integer id;

    /**
     * 交易单号
     */
    @TableField("orderSn")
    private String orderSn;

    /**
     * 卖方用户ID
     */
    @TableField("sellUserId")
    private String sellUserId;

    /**
     * 买方用户ID
     */
    @TableField("buyUserId")
    private String buyUserId;

    /**
     * 商品ID
     */
    @TableField("goodsId")
    private Integer goodsId;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 交易数量
     */
    private Integer num;

    /**
     * 总价
     */
    @TableField("sumPrice")
    private BigDecimal sumPrice;

    /**
     * 实际支付金额
     */
    @TableField("payMoney")
    private BigDecimal payMoney;

    /**
     * 付款截图
     */
    @TableField("payPic")
    private String payPic;

    /**
     * 订单状态 1:待付款 2:已付款 3:已取消 4:已完成 5:转售
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private Date createTime;

    /**
     * 付款时间
     */
    @TableField("payTime")
    private Date payTime;

    /**
     * 收款时间
     */
    @TableField("endTime")
    private Date endTime;


}
