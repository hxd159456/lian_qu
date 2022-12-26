package com.cqupt.art.feign;

import com.cqupt.art.entity.to.UserTransferTo;
import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigInteger;

@FeignClient("artmarket-conflux-chain")
public interface ConfluxChainClient {

    @GetMapping("/api/chain/conflux/transfer/{to}/{tokenId}")
    R adminTransfer(@PathVariable("to") String toAddress, @PathVariable("tokenId") BigInteger tokenId);

    @PostMapping("/api/chain/conflux/userTransfer")
    public R userTransfer(@RequestBody UserTransferTo to);
}
