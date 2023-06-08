package com.cqupt.art.order.app;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.cqupt.art.order.config.AlipayTemplate;
import com.cqupt.art.order.entity.vo.AlipayAsyncVo;
import com.cqupt.art.order.entity.vo.PayVo;
import com.cqupt.art.order.service.OrderService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/app/pay")
@Slf4j
@CrossOrigin
public class PayAppController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @GetMapping("/alipay/payOrder/{name}/{orderSn}/{goodsId}")
    public R payOrder(@PathVariable("orderSn") String orderSn,
                      @PathVariable("goodsId") String goodsId,
                      @PathVariable("name") String name) {
        log.info("支付宝支付，订单号：{}",orderSn);
        PayVo payVo = orderService.getOrderPay(orderSn,goodsId,name);
        try {
            String pay = alipayTemplate.pay(payVo);
            return R.ok().put("result", pay);
        } catch (AlipayApiException e) {
            log.error(e.getErrMsg());
            return R.error("支付异常，请刷新重试！");
        }
    }
}
