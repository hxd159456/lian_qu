package com.cqupt.art.order.service.impl;

import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.order.dao.OrderMapper;
import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.entity.UserToken;
import com.cqupt.art.order.entity.UserTokenItem;
import com.cqupt.art.order.entity.to.ChainTransferTo;
import com.cqupt.art.order.entity.to.NftBatchInfoTo;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.order.entity.vo.AlipayAsyncVo;
import com.cqupt.art.order.entity.vo.PayVo;
import com.cqupt.art.order.feign.NftWorksClient;
import com.cqupt.art.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.order.service.UserTokenItemService;
import com.cqupt.art.order.service.UserTokenService;
import com.cqupt.art.utils.R;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private NftWorksClient worksClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    UserTokenService userTokenService;
    @Autowired
    UserTokenItemService itemService;

    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        Order order = new Order();
        BeanUtils.copyProperties(orderTo, order);

        //卖方id为0为首发订单
        order.setSellUserId("0");
//        order.setGoodsId(orderTo.getGoodsId());
        order.setNum(1);
        //每次只能买一个
        order.setSumPrice(orderTo.getPrice());
        //不发优惠卷
        order.setPayMoney(orderTo.getPrice());
        order.setStatus(1);
        this.save(order);
        //秒杀限制了总量，所以库存是不会出问题的，所以不用先锁库存，支付成功了锁库存就行了
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        BigDecimal amount = order.getSumPrice();
        amount.setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(amount.toString());
        R r = worksClient.getNftInfo(order.getGoodsId().toString());
        if (r.getCode() == 200) {
            NftBatchInfoTo info = r.getData("data", new TypeReference<NftBatchInfoTo>() {
            });
            payVo.setSubject(info.getName());
            if ("0".equals(order.getSellUserId())) {
                payVo.setBody("首发订单-" + info.getName());
            } else {
                payVo.setBody("二级订单-" + order.getSellUserId() + "-" + info.getName());
            }
        }
        return payVo;
    }


    @Override
    public boolean handlerPayResult(AlipayAsyncVo alipayAsyncVo) {
        //支付成功
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", alipayAsyncVo.getOut_trade_no()));
        if("TRADE_SUCCESS".equals(alipayAsyncVo.getTrade_status())){
            order.setStatus(2);
            order.setPayTime(alipayAsyncVo.getGmt_payment());
            order.setEndTime(alipayAsyncVo.getGmt_close());
            this.updateById(order);
            //支付成功，应当给用户转入藏品
            transferToUser(order);
            return true;
        }else if("TRADE_CLOSED".equals(alipayAsyncVo.getTrade_status())){
            //超时关闭
            order.setStatus(3);
            order.setEndTime(alipayAsyncVo.getGmt_close());
            this.updateById(order);
            return true;
        }
        return false;
    }

    @Transactional
    void transferToUser(Order order) {
        ChainTransferTo to = new ChainTransferTo();
        to.setFromUserId(order.getSellUserId());
        to.setToUserId(order.getBuyUserId());
        to.setArtId(order.getGoodsId());
        to.setLocalId(order.getLocalId());

        UserTokenItem item = new UserTokenItem();
        item.setPrice(order.getPrice());
        item.setLocalId(order.getLocalId());
        item.setStatus(2);
        int gainType = order.getSellUserId().equals("0")?1:2;
        item.setGainType(gainType);
        R r = worksClient.getNftInfo(order.getGoodsId());
        item.setStatus(2);
        NftBatchInfoTo nftBatchInfoTo = null;
        if(r.getCode() == 200){
            nftBatchInfoTo = r.getData("data", new TypeReference<NftBatchInfoTo>() {
            });
            item.setTokenType(nftBatchInfoTo.getType());
        }
        if(order.getSellUserId().equals("0")){
            //首发订单
            UserToken userToken = new UserToken();
            userToken.setUserId(order.getBuyUserId());
            userToken.setArtId(order.getGoodsId());
            userToken.setCount(order.getNum());
            userToken.setSail(0);
            userTokenService.getBaseMapper().insert(userToken);
            item.setMapId(userToken.getId());
            itemService.save(item);

            // 链上转帐，远程调用的话会阻塞在这里，影响效率
            rabbitTemplate.convertAndSend("nft-order-event","nft.order.chain.transfer",to);
        }else{
            UserToken userToken = userTokenService.getOne(new QueryWrapper<UserToken>()
                    .eq("user_id", order.getBuyUserId())
                    .eq("art_id", order.getGoodsId()));
            userToken.setCount(userToken.getCount()+order.getNum());
            userTokenService.updateById(userToken);
            item.setMapId(userToken.getId());
            itemService.save(item);
            rabbitTemplate.convertAndSend("nft-order-event","nft.order.chain.transfer",to);
        }
    }
}
