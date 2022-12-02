package com.cqupt.art.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.art.order.entity.UserToken;
import com.cqupt.art.order.entity.vo.TokenBasicVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-25
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {

//    List<TokenBasicVo> getTokenBasicInfo(@Param("userId") String userId, @Param("start") int start, @Param("limit") Integer limit);
}
