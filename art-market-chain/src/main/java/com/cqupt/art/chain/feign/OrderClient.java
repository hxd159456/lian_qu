package com.cqupt.art.chain.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "art-order-server")
public interface OrderClient {
    @GetMapping("/userToken/updateStatus")
    public R updateStatus(@RequestParam String artId,
                          @RequestParam String userId,
                          @RequestParam Integer localId,
                          @RequestParam String txHash);
}
