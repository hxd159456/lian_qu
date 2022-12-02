package com.cqupt.art.app;

import com.cqupt.art.config.LoginInterceptor;
import com.cqupt.art.entity.User;
import com.cqupt.art.entity.to.GainTokenTo;
import com.cqupt.art.entity.vo.TokenBasicVo;
import com.cqupt.art.service.UserTokenService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户的藏品相关
 */
@RestController
@RequestMapping("/app/trade/token")
public class UserTokenAppController {

    @Autowired
    private UserTokenService userTokenService;

    @GetMapping("/userTokenList/{curPage}/{limit}")
    public R userTokenList(@PathVariable("curPage") Integer curPage, @PathVariable("limit")Integer limit){
        User user = LoginInterceptor.threadLocal.get();
        List<TokenBasicVo> list =  userTokenService.userToken(user.getUserId(),curPage,limit);
        return R.ok().put("list",list);
    }

    /**
     * 用户获取到新的藏品，转入
     * @return
     */
    @PostMapping("transferToUser")
    public R transferToUser(@RequestBody GainTokenTo to){
        userTokenService.transferIn(to);
        return R.ok();
    }
}
