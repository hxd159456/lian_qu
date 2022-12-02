package com.cqupt.art.order.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(value = "artdev-author",url = "http://10.17.156.253:8881")
public interface NftWorksClient {

    @GetMapping("/author/nftBatchInfo/getNftName/{id}")
    R getNftInfo(@PathVariable("id") String id);


}
