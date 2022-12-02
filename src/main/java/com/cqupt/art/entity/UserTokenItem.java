package com.cqupt.art.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pm_user_token_item")
public class UserTokenItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private Integer id;

    /**
     * 藏品类型：0、藏品，1、盲盒
     */
    private Boolean tokenType;

    /**
     * 获取类型：1、首发，2、二级购买，3、转增，4、盲盒开出
     */
    private Integer gainType;

    /**
     * 链上交易hash
     */
    private String txHash;

    /**
     * 获取的价格
     */
    private BigDecimal price;

    /**
     * 藏品的本地id
     */
    private Integer localId;

    /**
     * 藏品状态：1、正常，2、待链上确认，3、寄售中（链上转出确认中）
     */
    private Boolean status;

    private Integer mapId;


}
