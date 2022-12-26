package com.cqupt.art.chain.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "art-market-trade")
public interface TradeClient {
    @GetMapping("updateStatus")
    R updateLogStatus(@RequestParam String nftId, @RequestParam String fromUid,
                          @RequestParam String toUid,
                          @RequestParam String txHash,
                          @RequestParam Integer localId);

    @GetMapping("/updateStatus")
    R updateUserNftStatus(@RequestParam String artId,
                          @RequestParam String userId,
                          @RequestParam Integer localId,
                          @RequestParam String txHash);
}
