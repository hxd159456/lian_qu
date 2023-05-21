package com.cqupt.art.chain.controller;


import com.cqupt.art.chain.channel.ChainChannelContextFactory;
import com.cqupt.art.chain.channel.ChainOperation;
import com.cqupt.art.chain.entity.AccountInfo;
import com.cqupt.art.exception.ChainOperationException;
import com.cqupt.art.utils.R;
import com.cqupt.art.chain.service.ConfluxAccountService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;

/**
 * 使用桥接模式，策略模式，工厂模式（策略的调用类）来实现对抽象的依赖，可以自由选择以及快速的实现链的切换
 * 链上的操作与业务逻辑完全解耦
 */

@RestController
@RequestMapping("/chain/account")
@Slf4j
public class AccountController {
//    @Autowired
//    private ConfluxAccountService accountService;

    @Autowired
    private ChainChannelContextFactory chainChannelContextFactory;

    //    @ApiOperation("创建账户，返回账户相关信息，包括密码，私钥，地址，由调用方确定是否放到数据库")
    @GetMapping("/createAccount/{pwd}")
    public R createAccount(@PathVariable("pwd") String pwd, @RequestParam(value = "type",required = true,defaultValue = "conflux") String type) {

        ChainOperation chainOperation = chainChannelContextFactory.getChainOperation(type);

        log.info("createAccount---pwd  {}", pwd);
        long start = new Date().getTime();
        try {
//            AccountInfo ai = accountService.createAccount(pwd);
            AccountInfo ai = chainOperation.createAccount(pwd);
            log.info("账户信息 {}", ai.toString());
            long end = new Date().getTime();
            log.info("创建链上账户所用时间：{}毫秒", end - start);
            return R.ok().put("data", ai);
        } catch (Exception e) {
            throw new ChainOperationException(e);
        }
    }
}
