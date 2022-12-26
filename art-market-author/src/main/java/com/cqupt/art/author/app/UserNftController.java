package com.cqupt.art.author.app;

import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.cqupt.art.author.config.LoginInterceptor;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.User;
import com.cqupt.art.author.entity.UserToken;
import com.cqupt.art.author.entity.UserTokenItem;
import com.cqupt.art.author.entity.to.UserNftInfoTo;
import com.cqupt.art.author.entity.vo.NftItem;
import com.cqupt.art.author.entity.vo.UserNftDetailVo;
import com.cqupt.art.author.entity.vo.UserRepostitoryNftVo;
import com.cqupt.art.author.feign.OrderFeignClient;
import com.cqupt.art.author.feign.TradeFeignService;
import com.cqupt.art.author.service.NftBatchInfoService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/app/author/user/nfts")
@Slf4j
public class UserNftController {

    @Autowired
    TradeFeignService tradeFeignService;
    @Autowired
    NftBatchInfoService batchInfoService;

    @PostMapping("list")
    public R userNftList(){
        R res = tradeFeignService.userNftList();
        if(res.getCode()==200){
            List<UserToken> list = res.getData("data", new TypeReference<List<UserToken>>() {
            });
            List<UserRepostitoryNftVo> vos = new ArrayList<>();
            for (UserToken userToken : list) {
                UserRepostitoryNftVo vo = new UserRepostitoryNftVo();
                vo.setCount(userToken.getCount());
                vo.setId(userToken.getId());
                vo.setOnSail(userToken.getSail());
                log.info("artId--->{}",userToken.getArtId());
//                NftBatchInfoEntity nftMetaInfo = batchInfoService.getById(userToken.getUserId());
                NftBatchInfoEntity nftMetaInfo = batchInfoService.getOne(new QueryWrapper<NftBatchInfoEntity>().eq("id", userToken.getArtId()));

                vo.setImage(nftMetaInfo.getImageUrl());
                vo.setName(nftMetaInfo.getName());
                vo.setTotalSupply(nftMetaInfo.getTotalSupply());
                vos.add(vo);
            }
            return R.ok().put("list",vos);
        }
        return R.error("系统错误！");
    }

    @PostMapping("popupData")
    public R pupup(@RequestParam("id") String map_id){
        log.info("id----{}",map_id);
        R r = tradeFeignService.items(map_id);
        if(r.getCode()==200){
            List<UserTokenItem> userTokenItems = r.getData("data", new TypeReference<List<UserTokenItem>>() {
            });
            List<NftItem> collect = userTokenItems.stream().map(item -> {
                NftItem nftItem = new NftItem();
                nftItem.setPrice(item.getPrice().setScale(2, RoundingMode.DOWN));
                nftItem.setLocalId(item.getLocalId());
                nftItem.setSail(item.getStatus() == 3);
                return nftItem;
            }).collect(Collectors.toList());
            return R.ok().put("items",collect);
        }
        return R.error("系统异常！");
    }


    @GetMapping("nftDetail/{id}/{localId}")
    public R nftDetail(@PathVariable("id") String id,@PathVariable("localId") String localId){
        R r = tradeFeignService.detail(id,localId);
        if(r.getCode()==200){
            UserNftInfoTo info = r.getData("data", new TypeReference<UserNftInfoTo>() {
            });
            NftBatchInfoEntity metaInfo = batchInfoService.getById(info.getArtId());
            UserNftDetailVo vo = new UserNftDetailVo();
            vo.setDescription(metaInfo.getDescription());
            vo.setImageUrl(metaInfo.getImageUrl());
            vo.setContractAddress(metaInfo.getContractAddress());
            vo.setTxHash(info.getTxHash());
            vo.setLocalId(info.getLocalId());
            vo.setPrice(info.getPrice());
            vo.setOnSail(info.getStatus()==3);
            vo.setName(metaInfo.getName());
            vo.setTotalSupply(metaInfo.getTotalSupply());
            return R.ok().put("data",vo);
        }
        return R.error("系统异常！");
    }
}
