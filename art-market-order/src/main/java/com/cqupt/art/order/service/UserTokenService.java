package com.cqupt.art.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.order.entity.UserToken;
import com.cqupt.art.order.entity.vo.TokenBasicVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-25
 */
@Service
public interface UserTokenService extends IService<UserToken> {

//    List<TokenBasicVo> userToken(String userId, Integer curPage, Integer limit);

}
