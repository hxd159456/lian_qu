package com.cqupt.art.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.art.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
@Mapper
@Qualifier("userMapper")
public interface PmUserMapper extends BaseMapper<User> {

}
