package com.cqupt.art.seckill.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
//@ApiModel(value="PmUser对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;


    private String userId;


    private String userName;


    private String realName;


    private Integer payPassword;
    //    @ApiModelProperty(value = "身份证号")

    @Pattern(regexp = " /^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$/", message = "身份证不合法！")
    private String cardID;


    @Pattern(regexp = "/^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$/", message = "手机号不合法")
    private String userPhone;


    private Integer inviteId;


    private String inviteCode;


    private BigDecimal friendMoney;


    private BigDecimal orderMoney;


    private BigDecimal totalMoney;


    private BigDecimal drawMoney;


    private Integer star;


    private Integer invitedCount;


    private Integer userStatus;


    private Date createTime;

    @Pattern(regexp = "/^(?![0-9]+$)[a-z0-9]{1,50}$/", message = "密码必须同时包含字母和数字！")
    private String password;


    private String chainAddress;


    private String privateKey;

    private String accountPassword;


}
