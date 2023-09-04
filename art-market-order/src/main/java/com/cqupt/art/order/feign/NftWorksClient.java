package com.cqupt.art.order.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "art-dev-author")
public interface NftWorksClient {

    @GetMapping("/author/nftBatchInfo/getNftName/{id}")
    R getNftInfo(@PathVariable("id") String id);

    @PostMapping("/author/nftInfo/localId")
    R getLocalId(@RequestParam String artId, @RequestParam String userId);

    @GetMapping("/author/nftBatchInfo/updateInventory/{artId}/{localId}/{userId}")
    public R updateInventory(@PathVariable("artId") Long artId,@PathVariable("localId") Long localId,@PathVariable("userId") String userId);
}
