package com.cqupt.art.author.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "art-market-trade")
public interface TradeFeignService {
    @GetMapping("/api/trade/transfer/getTransforLog")
    R getTransforLog(@RequestParam("nftId") Long nftId);


    @PostMapping("/app/trade/user/nfts/list")
    R userNftList();

    @PostMapping("/app/trade/user/nfts/items")
    public R items(@RequestParam("map_id") String mapId);

    @GetMapping("/app/trade/user/nfts/detail/{id}/{localId}")
    public R detail(@PathVariable("id") String id, @PathVariable("localId")String localId);
}
