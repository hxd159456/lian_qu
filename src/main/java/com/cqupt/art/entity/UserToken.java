package com.cqupt.art.entity;

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
@TableName("pm_user_token")
public class UserToken implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户所拥有的nft映射表
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private Integer id;

    private String userId;

    private String artId;

    private Integer count;


}
