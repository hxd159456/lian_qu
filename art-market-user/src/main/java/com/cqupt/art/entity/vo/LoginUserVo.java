package com.cqupt.art.entity.vo;

import lombok.Data;

import javax.ws.rs.DefaultValue;

@Data
public class LoginUserVo {
    private String id;
    private String phone;
    private String userName;
    private String chainAddress;
    @DefaultValue("USER")
    private String userType;
}
