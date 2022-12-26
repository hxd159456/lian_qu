package com.cqupt.art.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.config.LoginInterceptor;
import com.cqupt.art.entity.TransferLog;
import com.cqupt.art.entity.User;
import com.cqupt.art.entity.UserToken;
import com.cqupt.art.entity.UserTokenItem;
import com.cqupt.art.entity.to.TransferOrderTo;
import com.cqupt.art.entity.to.UserNftInfoTo;
import com.cqupt.art.entity.to.UserTransferTo;
import com.cqupt.art.entity.vo.UserTransferVo;
import com.cqupt.art.feign.AuthorFeignClient;
import com.cqupt.art.feign.ConfluxChainClient;
import com.cqupt.art.feign.OrderFeignClient;
import com.cqupt.art.service.TransferLogService;
import com.cqupt.art.service.UserTokenItemService;
import com.cqupt.art.service.UserTokenService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 用户的nft相关操作
 */
@RestController
@RequestMapping("/app/trade/user/nfts")
@Slf4j
public class UserNftController {
    @Autowired
    UserTokenService userTokenService;
    @Autowired
    UserTokenItemService userTokenItemService;

    @Autowired
    AuthorFeignClient authorFeignClient;

    @Autowired
    ConfluxChainClient confluxChainClient;

    @Autowired
    TransferLogService transferLogService;

    @Autowired
    OrderFeignClient orderFeignClient;


    @PostMapping("list")
    public R userNftList(){
        User user = LoginInterceptor.threadLocal.get();
        if(user!=null){
            List<UserToken> tokens = userTokenService.list(new QueryWrapper<UserToken>().eq("user_id",user.getUserId()));
            return R.ok().put("data",tokens);
        }else{
            log.info("fuck...没登陆！！！！！！！！！！！");
            return R.error().put("msg","未登录！");
        }
    }

    @PostMapping("items")
    public R items(@RequestParam("map_id") String mapId){
        List<UserTokenItem> list = userTokenItemService.list(new QueryWrapper<UserTokenItem>().eq("map_id", mapId));
        return R.ok().put("data",list);
    }

    @GetMapping("detail/{id}/{localId}")
    public R detail(@PathVariable("id") String id,@PathVariable("localId")String localId){
        UserToken userToken = userTokenService.getById(id);
        UserTokenItem item = userTokenItemService.getOne(new QueryWrapper<UserTokenItem>().eq("map_id", id).eq("local_id", localId));

        UserNftInfoTo to = new UserNftInfoTo();
        to.setPrice(item.getPrice());
        to.setArtId(userToken.getArtId());
        to.setStatus(item.getStatus());
        to.setTxHash(item.getTxHash());
        to.setLocalId(item.getLocalId());
        return R.ok().put("data",to);
    }

    @PostMapping("userTransfer")
    public R transfer(@RequestBody UserTransferVo transferVo){
        log.info("收到的参数：{}", JSON.toJSONString(transferVo));
        User user = LoginInterceptor.threadLocal.get();
        UserToken userToken = userTokenService.getById(transferVo.getUserTokenId());
        R r = authorFeignClient.getTokenId(userToken.getArtId(), transferVo.getLocalId());
        if(r.getCode()==200){
            Long tokenId = r.getData("tokenId", new TypeReference<Long>() {
            });
            log.info("tokenId==={}",tokenId);
            UserTransferTo to = new UserTransferTo();
            to.setToAddress(transferVo.getToAddress());
            to.setTokenId(tokenId);
            to.setFromAddress(user.getChainAddress());
            R result = confluxChainClient.userTransfer(to);
            if(result.getCode()==200){
                String txHash = result.getData("data", new TypeReference<String>() {
                });
                log.info("txHash==={}",txHash);
                //日志
                TransferLog transferLog = new TransferLog();
                transferLog.setNftId(userToken.getArtId());
                transferLog.setPrice(BigDecimal.ZERO);
                transferLog.setLocalId(transferVo.getLocalId());
                transferLog.setTxHash(txHash);
                transferLog.setFromUid(user.getUserId());
                transferLog.setToUid(transferVo.getToAddress());
                //1首发 2 转赠 3 二级 4 空投
                transferLog.setType(2);
                transferLogService.save(transferLog);
                TransferOrderTo orderTo = new TransferOrderTo();
                orderTo.setOrderSn(UUID.randomUUID().toString().replace("-",""));
                orderTo.setBuyUserId(transferVo.getToAddress());//用地址代替id了
                orderTo.setGoodsId(userToken.getArtId());
                orderTo.setLocalId(transferVo.getLocalId());
                orderTo.setNum(1);
                orderTo.setPrice(BigDecimal.ZERO);
                orderTo.setSellUserId(user.getUserId());
                orderTo.setPayMoney(BigDecimal.ZERO);
                orderTo.setSumPrice(BigDecimal.ZERO);
                orderFeignClient.savaTransferOrder(orderTo);
            }else{
                return R.error("系统异常");
            }
        }else{
            return R.error("系统异常！");
        }
        return R.ok();
    }

}
