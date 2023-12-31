package com.cqupt.art.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 交易单号
     */
    @TableField("order_sn")
    private String orderSn;

    /**
     * 卖方用户ID
     */
    @TableField("sell_user_id")
    private String sellUserId;

    /**
     * 买方用户ID
     */
    @TableField("buy_user_id")
    private String buyUserId;

    /**
     * 商品ID
     */
    @TableField("goods_id")
    private String goodsId;

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
    @TableField("sum_price")
    private BigDecimal sumPrice;

    /**
     * 实际支付金额
     */
    @TableField("pay_money")
    private BigDecimal payMoney;

    /**
     * 付款截图
     */
    @TableField("pay_pic")
    private String payPic;

    /**
     * 订单状态 1:待付款 2:已付款 3:已取消 4:已完成 5:转售
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 付款时间
     */
    @TableField("pay_time")
    private Date payTime;

    /**
     * 收款时间
     */
    @TableField("end_time")
    private Date endTime;


}
