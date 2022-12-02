package com.cqupt.art.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.entity.UserToken;
import com.cqupt.art.entity.to.GainTokenTo;
import com.cqupt.art.entity.vo.TokenBasicVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-25
 */
public interface UserTokenService extends IService<UserToken> {

    List<TokenBasicVo> userToken(String userId, Integer curPage, Integer limit);

    void transferIn(GainTokenTo to);
}
