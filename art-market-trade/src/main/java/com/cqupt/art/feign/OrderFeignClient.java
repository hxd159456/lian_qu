package com.cqupt.art.feign;

import com.cqupt.art.entity.to.TransferOrderTo;
import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "art-order-server")
public interface OrderFeignClient {
    @PostMapping("/app/order/transfer/saveTransferOrder")
    public R savaTransferOrder(@RequestBody TransferOrderTo to);
}
