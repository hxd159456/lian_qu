package com.cqupt.art.seckill.app;

import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;
import com.cqupt.art.seckill.service.SeckillService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/seckill")
@CrossOrigin
public class SeckillController {
    @Autowired
    SeckillService seckillService;


    @PostMapping("/nft")
    public R seckill(@RequestBody SeckillInfoVo info) throws InterruptedException {
        String orderSn = seckillService.kill(info);

        if(orderSn!=null){
            return R.ok().put("orderSn", orderSn);
        }else{
            return R.error("清稍后重试！");
        }
    }
}
