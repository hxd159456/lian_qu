package com.cqupt.art.chain.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "art-market-author")
public interface WorkClient {

    @PostMapping("/author/nftInfo/getTokenId")
    R getTokenId(@RequestParam String artId, @RequestParam Integer localId);

}
