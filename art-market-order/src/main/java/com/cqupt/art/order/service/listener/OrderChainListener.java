package com.cqupt.art.order.service.listener;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.order.entity.to.ChainTransferTo;
import com.cqupt.art.order.feign.ChainClient;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@RabbitListener(queues = "nft.order.queue.chain")
@Service
@Slf4j
public class OrderChainListener {

    @Autowired
    ChainClient chainClient;

    @RabbitHandler
    public void orderChainHandler(ChainTransferTo to, Channel channel, Message message){
        log.info("开始处理链上交易，传入参数为：{}", JSON.toJSONString(to));
        if(to.getFromUserId().equals("0")){

        }
    }
}
