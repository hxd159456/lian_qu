package com.cqupt.art.order.app;

import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.entity.to.TransferOrderTo;
import com.cqupt.art.order.service.OrderService;
import com.cqupt.art.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/transfer")
public class TransferOrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("saveTransferOrder")
    public R savaTransferOrder(@RequestBody TransferOrderTo to){
        Order order = new Order();
        BeanUtils.copyProperties(to,order);
        orderService.save(order);
        return R.ok();
    }
}
