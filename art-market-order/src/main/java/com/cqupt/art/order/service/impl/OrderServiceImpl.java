package com.cqupt.art.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.order.dao.OrderMapper;
import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.entity.UserToken;
import com.cqupt.art.order.entity.UserTokenItem;
import com.cqupt.art.order.entity.to.ChainTransferTo;
import com.cqupt.art.order.entity.to.NftBatchInfoTo;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.order.entity.to.TransferLog;
import com.cqupt.art.order.entity.vo.AlipayAsyncVo;
import com.cqupt.art.order.entity.vo.PayVo;
import com.cqupt.art.order.feign.NftWorksClient;
import com.cqupt.art.order.feign.TradeClient;
import com.cqupt.art.order.service.OrderService;
import com.cqupt.art.order.util.BloomFilterUtil;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    //    @Autowired
//    UserTokenService userTokenService;
//    @Autowired
//    UserTokenItemService itemService;
    @Resource
    BloomFilterUtil bloomFilterUtil;
    @Autowired
    TradeClient tradeClient;
    RBloomFilter<String> bloomFilter = null;
    @Autowired
    private NftWorksClient worksClient;

    @PostConstruct
    public void init() {
        bloomFilterUtil.create("orderList", 50000, 0.05);
    }


    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        Order order = new Order();
        BeanUtils.copyProperties(orderTo, order);
        //卖方id为0为首发订单
        order.setSellUserId("0");
        order.setGoodsId(orderTo.getGoodsId());
        order.setNum(1);
        //每次只能买一个
        order.setSumPrice(orderTo.getPrice());
        //不发优惠卷，价格不要额外做计算
        order.setPayMoney(orderTo.getPrice());
        order.setStatus(1);
        this.save(order);
        //秒杀限制了总量，所以库存是不会出问题的，所以不用先锁库存，支付成功了锁库存就行了
        //存入缓存，便于查询
        //使用hash存储订单信息：key为商品id，hash的key为订单号，value为用户id：

        // 布隆过滤器
        bloomFilter.add(orderTo.getGoodsId() + "-" + orderTo.getOrderSn());
        redisTemplate.opsForHash().put(orderTo.getGoodsId(), orderTo.getOrderSn(), order);
    }

    @Override
    public PayVo getOrderPay(String orderSn, String goodsId, String name) {
        PayVo payVo = null;
        if (bloomFilter.contains(goodsId + "-" + orderSn)) {
            payVo = getPayVoByCache(orderSn, goodsId, name);
            assert payVo != null;
            payVo.setGoodsId(goodsId);
        } else {
            payVo = getPayVoByDB(orderSn, name);
            payVo.setGoodsId(goodsId);
        }
        return payVo;
    }

    @NotNull
    private PayVo getPayVoByDB(String orderSn, String name) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", orderSn));
        log.info("order==={}", JSON.toJSONString(order));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        BigDecimal amount = order.getSumPrice();
        amount.setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(amount.toString());
        payVo.setSubject(name);
        if ("0".equals(order.getSellUserId())) {
            payVo.setBody("首发订单-" + name);
        } else {
            payVo.setBody("二级订单-" + order.getSellUserId() + "-" + name);
        }
        return payVo;
    }

    @Nullable
    private PayVo getPayVoByCache(String orderSn, String goodsId, String name) {
        Object orderCache = redisTemplate.opsForHash().get(goodsId, orderSn);
        if (orderCache != null) {
            Order order = (Order) orderCache;
            PayVo payVo = new PayVo();
            payVo.setOut_trade_no(orderSn);
            BigDecimal amount = order.getSumPrice();
            amount.setScale(2, BigDecimal.ROUND_UP);
            payVo.setTotal_amount(amount.toString());
            payVo.setSubject(name);
            if ("0".equals(order.getSellUserId())) {
                payVo.setBody("首发订单-" + name);
            } else {
                payVo.setBody("二级订单-" + order.getSellUserId() + "-" + name);
            }
            return payVo;
        }
        return null;
    }


    @Override
    public boolean handlerPayResult(AlipayAsyncVo alipayAsyncVo) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", alipayAsyncVo.getOut_trade_no()));
        if ("TRADE_SUCCESS".equals(alipayAsyncVo.getTrade_status())) {
            //支付成功
            order.setStatus(2);
            order.setPayTime(alipayAsyncVo.getGmt_payment());
            order.setEndTime(alipayAsyncVo.getGmt_close());
            this.updateById(order);
            //支付成功，应当给用户转入藏品
            transferToUser(order);
            return true;
        } else if ("TRADE_CLOSED".equals(alipayAsyncVo.getTrade_status())) {
            //超时关闭
            order.setStatus(3);
            order.setEndTime(alipayAsyncVo.getGmt_close());
            this.updateById(order);
            return true;
        }
        return false;
    }

    //TODO 分布式事务
    @Transactional
    void transferToUser(Order order) {
        ChainTransferTo chainTransferTo = new ChainTransferTo();
        chainTransferTo.setFromUserId(order.getSellUserId());
        chainTransferTo.setToUserId(order.getBuyUserId());
        chainTransferTo.setArtId(order.getGoodsId());
        Integer localId = order.getLocalId();
        if (localId == null) {
            log.info("订单支付完成，生成本地id");
            R r = worksClient.getLocalId(order.getGoodsId(), order.getBuyUserId());
            localId = r.getData("data", new TypeReference<Integer>() {
            });
            log.info("生成的本地id为：{}", localId);
        }
        chainTransferTo.setLocalId(localId);

        UserTokenItem item = new UserTokenItem();
        item.setPrice(order.getPrice());
        item.setLocalId(localId);
        item.setStatus(2);
        int gainType = order.getSellUserId().equals("0") ? 1 : 2;
        item.setGainType(gainType);
        R r = worksClient.getNftInfo(order.getGoodsId());
        item.setStatus(2);
        NftBatchInfoTo nftBatchInfoTo = null;
        //交易日志
        TransferLog logTo = new TransferLog();
        logTo.setFromUid(order.getSellUserId());
        logTo.setToUid(order.getBuyUserId());
        logTo.setNftId(order.getGoodsId());
        logTo.setLocalId(order.getLocalId());
        logTo.setPrice(order.getPrice());

        if (r.getCode() == 200) {
            nftBatchInfoTo = r.getData("data", new TypeReference<NftBatchInfoTo>() {
            });
            item.setTokenType(nftBatchInfoTo.getType());
        }

        //TODO 考虑到有不限购的藏品，此处也先查后更新
        R result = tradeClient.getUserToken(order.getBuyUserId(), order.getGoodsId());
        UserToken userToken = null;
        if (result.getCode() == 200) {
            userToken = result.getData("data", new TypeReference<UserToken>() {
            });
        }
        if (userToken == null) {
            userToken = new UserToken();
            userToken.setUserId(order.getBuyUserId());
            userToken.setArtId(order.getGoodsId());
            userToken.setCount(order.getNum());
            userToken.setSail(0);
            R saveUserTokenResult = tradeClient.saveUserToken(userToken);
            if (saveUserTokenResult.getCode() == 200) {
                //为了拿到id
                userToken = saveUserTokenResult.getData("data", new TypeReference<UserToken>() {
                });
            }
//            userTokenService.getBaseMapper().insert(userToken);
        } else {
            userToken.setCount(userToken.getCount() + order.getNum());
            tradeClient.updateUserToken(userToken);
//            userTokenService.updateById(userToken);
        }
        item.setMapId(userToken.getId());
//        itemService.save(item);
        tradeClient.saveUserTokenItem(item);
        logTo.setLocalId(localId);
        //保存交易日志
        if (order.getSellUserId().equals("0")) {
            logTo.setType(1);
        } else {
            logTo.setType(4);
        }
        tradeClient.saveTransferLog(logTo);
        // 链上转帐，远程调用的话会阻塞在这里，影响效率
        rabbitTemplate.convertAndSend("nft-order-event", "nft.order.chain.transfer", chainTransferTo);
    }
}
