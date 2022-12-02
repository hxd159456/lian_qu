package com.cqupt.art.order.controller;


import com.cqupt.art.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/hello")
    public R helloFrp(){
        return R.ok().put("msg","内网穿透成功");
    }
}
