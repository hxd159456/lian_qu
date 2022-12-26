package com.cqupt.art.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.dao.UserTokenItemMapper;
import com.cqupt.art.entity.UserTokenItem;
import com.cqupt.art.service.UserTokenItemService;
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
public class UserTokenItemServiceImpl extends ServiceImpl<UserTokenItemMapper, UserTokenItem> implements UserTokenItemService {

    @Override
    public void updateStatus(String artId, String userId, Integer localId,String txHash) {
        this.baseMapper.updateStatus(artId,userId,localId,txHash);
    }
}
