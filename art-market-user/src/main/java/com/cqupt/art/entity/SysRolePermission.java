package com.cqupt.art.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRolePermission {
    @TableId(type = IdType.ID_WORKER)
    private Integer id;
    private Integer roleId;
    private Integer permissionId;
}
