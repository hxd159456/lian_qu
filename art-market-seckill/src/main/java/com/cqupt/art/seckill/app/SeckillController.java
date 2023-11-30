package com.cqupt.art.seckill.app;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.cqupt.art.annotation.AccessLimit;
import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;
import com.cqupt.art.seckill.service.SeckillService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@CrossOrigin
public class SeckillController {
    @Autowired
    SeckillService seckillService;


    /**
     * token 可以防止连接泄露，提前写好脚本来秒杀
     *
     * @param info
     * @return
     * @throws InterruptedException
     */
    @SentinelResource(value = "seckill", blockHandler = "handleSecBlock")
    @PostMapping("/nft")
    //单用户访问频率控制
    @AccessLimit(maxTime = 2L)
    public R seckill(@RequestBody SeckillInfoVo info) throws InterruptedException {
        String orderSn = seckillService.kill(info);
        if (orderSn != null) {
            return R.ok().put("orderSn", orderSn);
        } else {
            return R.error("清稍后重试！");
        }
    }

    public R handleSecBlock(@RequestBody SeckillInfoVo info, BlockException e){
        return R.error("太火爆了！");
    }

}
