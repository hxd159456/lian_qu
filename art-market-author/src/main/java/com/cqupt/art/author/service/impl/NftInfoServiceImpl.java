package com.cqupt.art.author.service.impl;


import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.author.dao.NftInfoDao;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.NftInfoEntity;
import com.cqupt.art.author.entity.to.TransferLogTo;
import com.cqupt.art.author.entity.to.UserTo;
import com.cqupt.art.author.entity.vo.NftAndUserVo;
import com.cqupt.art.author.entity.vo.TokenVo;
import com.cqupt.art.author.feign.TradeFeignService;
import com.cqupt.art.author.feign.UserFeignService;
import com.cqupt.art.author.service.NftBatchInfoService;
import com.cqupt.art.author.service.NftInfoService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("nftInfoService")
@Slf4j
public class NftInfoServiceImpl extends ServiceImpl<NftInfoDao, NftInfoEntity> implements NftInfoService {

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    TradeFeignService tradeFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    NftBatchInfoService batchInfoService;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public List<NftAndUserVo> queryPage(Map<String, Object> params) {
        QueryWrapper<NftInfoEntity> queryWrapper = new QueryWrapper<>();
        String artName = (String) params.get("artName");
        if (artName != null && artName.length() != 0) {
            queryWrapper.like("token_name", artName);
        }
        String state = (String) params.get("state");
        if (state != null && state.length() != 0) {
            int state_int = Integer.parseInt(state);
            queryWrapper.and(q -> {
                q.eq("state", state_int);
            });
        }
        String getWay = (String) params.get("getWay");
        if (getWay != null && getWay.length() != 0) {
            int getWay_int = Integer.parseInt(getWay);
            queryWrapper.and(q -> {
                q.eq("getWay", getWay_int);
            });
        }

        List<NftInfoEntity> nftInfoEntityList = this.list(queryWrapper);
        List<NftAndUserVo> nftAndUserVoList = new ArrayList<>();
        for (NftInfoEntity nftInfoEntity : nftInfoEntityList) {
            R r = userFeignService.getPhoneAndAddById(nftInfoEntity.getUserId());
            String phone = (String) params.get("phone");
            if (r.getCode() != 0) {
                log.error("远程查询用户信息失败");
            } else {
                UserTo userTo = r.getData("data", new TypeReference<UserTo>() {
                });
                if (phone != null && phone.length() > 0) {
                    if (userTo.getUserPhone().equals(phone)) {
                        NftAndUserVo nftAndUserVo = new NftAndUserVo();
                        nftAndUserVo.setUserInfo(userTo);
                        nftAndUserVo.setNftInfoEntity(nftInfoEntity);
                        nftAndUserVoList.add(nftAndUserVo);
                    }
                } else {
                    NftAndUserVo nftAndUserVo = new NftAndUserVo();
                    nftAndUserVo.setUserInfo(userTo);
                    nftAndUserVo.setNftInfoEntity(nftInfoEntity);
                    nftAndUserVoList.add(nftAndUserVo);
                }
            }
        }
        int curPage = (int) params.get("curPage");
        int capacity = (int) params.get("capacity");

        return nftAndUserVoList.subList((curPage - 1) * capacity, (curPage - 1) * capacity + capacity);
    }

    @Override
    public List<TransferLogTo> getTransforLog(Long id) {
        R r = tradeFeignService.getTransforLog(id);
        if (r.getCode() != 0) {
            log.error("远程查询流转记录失败");
            return null;
        }
        List<TransferLogTo> data = r.getData("data", new TypeReference<List<TransferLogTo>>() {
        });
        return data;
    }

    @Override
    public Integer localId(String artId,String userId) {
        String key = "nft:token:bit:local:" + artId;
        Boolean hasKey = redisTemplate.hasKey(key);;
        if(Boolean.FALSE.equals(hasKey)){
            NftBatchInfoEntity byId = batchInfoService.getById(artId);
            redisTemplate.opsForValue().setBit("nft:token:bit:local"+artId,byId.getInventory(),false);
        }
        RLock lock = redissonClient.getLock(key + ":lock");
        try {
            boolean locked = false;
            Long index = -1L;
            do{
                locked = lock.tryLock(1000, TimeUnit.MILLISECONDS);
                index = redisTemplate.execute(new DefaultRedisScript<>("return redis.call('bitpos', KEYS[1], ARGV[1])", Long.class),
                        Arrays.asList(key), "0");
                redisTemplate.opsForValue().setBit(key,index,true);
            }while (!locked);
            return index.intValue();
        }catch (Exception e){
            log.info("分配LocalId失败！{}",e.getMessage());
            return null;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void updateUser(Long artId, String userId, Long localId) {
//        NftInfoEntity one = this.getOne(new QueryWrapper<NftInfoEntity>()
//                .eq("local_id", localId)
//                .eq("art_id", artId));
//        one.setUserId(userId);
//        this.updateById(one);
    }

}
