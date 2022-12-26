package com.cqupt.art.chain.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "art-user-service")
public interface UserClient {
    @PostMapping("/api/user/chainAddress")
    R getchainAddress(@RequestParam String userId);
}
