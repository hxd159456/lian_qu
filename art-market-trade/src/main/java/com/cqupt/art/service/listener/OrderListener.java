package com.cqupt.art.service.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.entity.Order;
import com.cqupt.art.entity.UserToken;
import com.cqupt.art.service.UserTokenService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@RabbitListener(queues = "nft.order.handle.queue")
public class OrderListener {
    @Autowired
    UserTokenService userTokenService;

    @RabbitHandler
    public void handleTransfer(Order order, Message message, Channel channel){
        if(order.getSellUserId().equals("0")){
            //首发订单
            UserToken userToken = new UserToken();
            userToken.setUserId(order.getBuyUserId());
            userToken.setArtId(order.getGoodsId());
            userToken.setCount(order.getNum());
            userToken.setSail(0);
            userTokenService.save(userToken);
        }else{
            UserToken userToken = userTokenService.getOne(new QueryWrapper<UserToken>()
                    .eq("user_id", order.getBuyUserId())
                    .eq("art_id", order.getGoodsId()));
            userToken.setCount(userToken.getCount()+order.getNum());
            userTokenService.updateById(userToken);
        }
    }
}
