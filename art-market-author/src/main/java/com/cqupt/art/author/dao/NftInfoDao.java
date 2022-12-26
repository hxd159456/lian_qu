package com.cqupt.art.author.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.art.author.entity.NftInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NftInfoDao extends BaseMapper<NftInfoEntity> {
    int updateUseCas(@Param("one") NftInfoEntity one);
}
