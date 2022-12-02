package com.cqupt.art.chain.service.listener;

import com.cqupt.art.chain.entity.to.ChainTransferTo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RabbitListener
@Slf4j
@Service
public class OrderListener {

    @RabbitHandler
    public void handleTransfer(ChainTransferTo to, Channel channel, Message message){

    }
}
