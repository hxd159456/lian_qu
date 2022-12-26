package com.cqupt.art.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.dao.UserTokenMapper;
import com.cqupt.art.entity.UserToken;
import com.cqupt.art.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-25
 */
@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenMapper, UserToken> implements UserTokenService {
    @Autowired
    UserTokenMapper userTokenMapper;
//    @Autowired
//    UserTokenItemService tokenItemService;
//    @Override
//    public List<TokenBasicVo> userToken(String userId, Integer curPage, Integer limit) {
//        int start = (curPage-1)*limit;
//        List<TokenBasicVo> list =  userTokenMapper.getTokenBasicInfo(userId,start,limit);
//        return list;
//    }

//    @Transactional
//    @Override
//    public void transferIn(GainTokenTo to) {
//        UserToken userToken = this.getOne(new QueryWrapper<UserToken>().eq("user_id", to.getUserId()).eq("art_id", to.getArtId()));
//        if(userToken==null){
//            userToken = new UserToken();
//            BeanUtils.copyProperties(to,userToken);
//            userToken.setSail(0);
//            List<UserTokenItem> tokenItems = to.getTokenItem();
//            tokenItemService.saveBatch(tokenItems);
//            this.save(userToken);
//        }else{
//            userToken.setCount(userToken.getCount()+to.getCount());
//            this.updateById(userToken);
//            tokenItemService.saveBatch(to.getTokenItem());
//        }
//        //TODO :链上真实转账
//    }
}
