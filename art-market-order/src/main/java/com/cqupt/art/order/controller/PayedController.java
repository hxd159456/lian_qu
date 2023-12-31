package com.cqupt.art.order.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.cqupt.art.order.config.AlipayTemplate;
import com.cqupt.art.order.entity.vo.AlipayAsyncVo;
import com.cqupt.art.order.service.OrderService;
import com.cqupt.art.utils.R;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay/payed")
@Slf4j
public class PayedController {
    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private OrderService orderService;

    @GetMapping("test")
    public R test(){
        return R.ok();
    }

    // 支付宝异步回调，获取支付状态
    // TODO 分布式事务的入口
    @GlobalTransactional
    @PostMapping("/alipay/notify")
    public String handlerAlipay(HttpServletRequest request, AlipayAsyncVo alipayAsyncVo) throws AlipayApiException, UnsupportedEncodingException {
        log.info("收到支付宝异步通知，订单号为：{}，支付宝订单号为：{}",alipayAsyncVo.getOut_trade_no(),alipayAsyncVo.getTrade_no());
        //验签
        Map<String,String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String key:requestParams.keySet()){
            String[] values = requestParams.get(key);
            String valStr = "";
            for (int i = 0; i < values.length; i++) {
                valStr = (i== values.length-1)?valStr+values[i] :valStr+values[i]+",";
                params.put(key,valStr);
            }
            valStr =new String(valStr.getBytes("ISO-8859-1"),"gbk");
        }
        boolean signVerified  = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(), alipayTemplate.getSign_type());
        if(signVerified){
            log.info("支付宝异步通知验签成功！！！");
            boolean handled  = orderService.handlerPayResult(alipayAsyncVo);
            if(handled){
                return "success";
            }else {
                return "error";
            }
        }else{
            log.info("支付宝异步通知验签失败！！！");
            return "error";
        }
    }
}
