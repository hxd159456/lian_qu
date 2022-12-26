package com.cqupt.art.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cqupt.art.entity.UserTokenItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-25
 */
@Mapper
public interface UserTokenItemMapper extends BaseMapper<UserTokenItem> {

    void updateStatus(@Param("artId") String artId,
                      @Param("userId") String userId,
                      @Param("localId") Integer localId,
                      @Param("txHash") String txHash);
}
