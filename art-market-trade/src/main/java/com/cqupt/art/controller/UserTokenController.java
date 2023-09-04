package com.cqupt.art.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.entity.UserToken;
import com.cqupt.art.entity.UserTokenItem;
import com.cqupt.art.service.UserTokenItemService;
import com.cqupt.art.service.UserTokenService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userToken")
public class UserTokenController {
    @Autowired
    private UserTokenItemService userTokenItemService;
    @Autowired
    private UserTokenService userTokenService;

    @GetMapping("/updateStatus")
    public R updateUserNftStatus(@RequestParam String artId,@RequestParam String userId,@RequestParam Integer localId,@RequestParam String txHash){
        userTokenItemService.updateStatus(artId,userId,localId,txHash);
        return R.ok();
    }


    @GetMapping("getUserToken")
    public R getUserToken(@RequestParam("userId") String userId,@RequestParam("artId") String artId){
        UserToken userToken = userTokenService.getOne(new QueryWrapper<UserToken>()
                .eq("user_id", userId)
                .eq("art_id", artId));
        return R.ok().put("data",userToken);
    }

    @PostMapping("saveUserToken")
    public R saveUserToken(@RequestBody UserToken userToken){
        userTokenService.save(userToken);
        return R.ok().put("data",userToken);
    }

    @PostMapping("updateUserToken")
    public R updateUserToken(@RequestBody UserToken userToken){
        userTokenService.updateById(userToken);
        return R.ok();
    }

    @PostMapping("saveUserTokenItem")
    public R saveUserTokenItem(@RequestBody UserTokenItem item){
        userTokenItemService.save(item);
        return R.ok();
    }
}
