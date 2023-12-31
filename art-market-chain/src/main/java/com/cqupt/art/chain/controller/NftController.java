package com.cqupt.art.chain.controller;

import com.cqupt.art.chain.entity.NftMetadata;
import com.cqupt.art.chain.entity.to.CreateNftBatchInfoTo;
import com.cqupt.art.chain.entity.to.CreateNftBatchResultTo;
import com.cqupt.art.chain.entity.to.UserTransferTo;
import com.cqupt.art.chain.service.ConfluxNftService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/chain/conflux")
//@Api(tags = "对外暴露接口")
public class NftController {
    @Autowired
    private ConfluxNftService confluxNftService;

    //    @ApiOperation("获取发行总量，返回到现在为止总共发行过的nft数量")
    @GetMapping("/totalSupply")
    public R totalSupply() {
        BigInteger totalSupply = confluxNftService.totalSupply();
        return R.ok().put("data", totalSupply);
    }

    //    @Operation(summary = "链上资产转账，将tokenId从from转移到to")
    @GetMapping("/transfer/{from}/{to}/{tokenId}")
    public R transfer(@PathVariable("from")
                              String from,
                      @PathVariable("to")
                              String to,
                      @PathVariable("tokenId")
                              BigInteger tokenId) {
        String txHash = null;
        try {
            txHash = confluxNftService.transfer(from, to, tokenId);
            return R.ok().put("data", txHash);
        } catch (Exception e) {
            return R.error("链上操作异常，详见链上操作模块日志！");
        }
    }

    @GetMapping("/transfer/{to}/{tokenId}")
    public R adminTransfer(@PathVariable("to") String toAddress, @PathVariable("tokenId") BigInteger tokenId) {
        String txHash = confluxNftService.adminTransfer(toAddress, tokenId);
        if (txHash != null) {
            return R.ok().put("data", txHash);
        } else {
            return R.error("链上操作异常，详见链上操作模块日志！");
        }
    }

    @PostMapping("/adminTransferBatch")
    public R adminTransferBatch(@RequestParam List<String> addressList, @RequestParam List<BigInteger> tokenIds) {
        String txHash = confluxNftService.adminTransferBatch(addressList, tokenIds);
        if (txHash != null) {
            return R.ok().put("data", txHash);
        } else {
            return R.error("链上操作异常，详见链上操作模块日志！");
        }
    }

    @GetMapping("/approve/{to}/{tokenId}")
    public R approve(@PathVariable("to") String to, @PathVariable("tokenId") BigInteger tokenId) {
        try {
            String approve = confluxNftService.approve(to, tokenId);
            return R.ok().put("data", approve);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("链上操作异常，详见链上操作模块日志！");
        }
    }

    @PostMapping("/createNft/batch/once")
    public R createNftBatchOnce(@RequestBody CreateNftBatchInfoTo to) {
        try {
            CreateNftBatchResultTo resultTo = confluxNftService.createNftBatch(to.getNum(), to.getMetadata(), to.getAuthorName());
            return R.ok().put("data", resultTo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("链上操作异常，详见链上操作模块日志！");
        }
    }

    @PostMapping("/createNft/batch")
    public R createNftBatch(@RequestParam("num") int num, @RequestParam("metadata") NftMetadata metadata) {
        try {
            List<String> txHashList = confluxNftService.creatNftBatch(num, metadata);
            return R.ok().put("data", txHashList);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("链上操作异常，详见链上操作模块日志！");
        }
    }


    @PostMapping("/userTransfer")
    public R userTransfer(@RequestBody UserTransferTo to){
        String txHash = null;
        try {
            txHash = confluxNftService.userTransfer(to);
            log.info("转赠成功，交易Hash为：{}",txHash);
            return R.ok().put("data",txHash);
        } catch (Exception e) {
            log.info(e.getMessage());
            return R.error().put("msg","链上转移错误！");
        }
    }
}
