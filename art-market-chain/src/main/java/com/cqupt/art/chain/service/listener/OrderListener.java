package com.cqupt.art.chain.service.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.cqupt.art.chain.entity.to.ChainTransferTo;
import com.cqupt.art.chain.feign.TradeClient;
import com.cqupt.art.chain.feign.UserClient;
import com.cqupt.art.chain.feign.WorkClient;
import com.cqupt.art.chain.service.ConfluxNftService;
import com.cqupt.art.utils.R;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;

@RabbitListener(queues = "nft.seckill.queue.transfer")
@Slf4j
@Service
public class OrderListener {
    @Resource
    UserClient userClient;
    @Autowired
    WorkClient workClient;
    @Autowired
    ConfluxNftService nftService;
    @Autowired
    TradeClient tradeClient;

    @SneakyThrows
    @RabbitHandler
    public void handleAdminTransfer(ChainTransferTo to, Channel channel, Message message){
        log.info("处理链上转帐，传入数据：{}", JSON.toJSONString(to));
        log.info("本地id为：{}",to.getLocalId());
        R r = workClient.getTokenId(to.getArtId(), to.getLocalId());
        //TODO 多线程，线程编排提高效率
        if(r.getCode() == 200){
            String tokenId = r.getData("tokenId", new TypeReference<String>() {
            });
            R r1 = userClient.getchainAddress(to.getToUserId());
            if(r1.getCode()==200){
                String chainAddress = r1.getData("chainAddress", new TypeReference<String>() {
                });
                //TODO 只用交易hash验证交易是否成功是不完全可靠的！
                String txHash = nftService.adminTransfer(chainAddress, new BigInteger(tokenId));
                if(StringUtils.isNotBlank(txHash)){
                    //链上交易成功，更新用户藏品装态并更新交易记录，接收消息
                    tradeClient.updateLogStatus(to.getArtId(),to.getFromUserId(),to.getToUserId(),txHash,to.getLocalId());
                    //更新用户藏品的状态
                    tradeClient.updateUserNftStatus(to.getArtId(), to.getToUserId(), to.getLocalId(),txHash);

                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }else{
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
            }else{
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }else{
            log.info(r.getData("msg", new TypeReference<String>() {
            }));
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
