package com.cqupt.art.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.constant.SeckillConstant;
import com.cqupt.art.constant.SeckillOrderMqConstant;
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
import org.redisson.api.RBloomFilter;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

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
    ThreadPoolExecutor threadPoolExecutor;

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
//        CorrelationData correlationData = new CorrelationData();
//        correlationData.setId(orderTo.getToken());
        rabbitTemplate.convertAndSend(SeckillOrderMqConstant.SECKILL_ORDER_EXCHANGE,
                SeckillOrderMqConstant.SECKILL_ORDER_DELAY_ROUTING_KEY,
                order.getOrderSn() + "-" + orderTo.getToken());
        //秒杀限制了总量，所以库存是不会出问题的，所以不用先锁库存，支付成功了锁库存就行了
        //存入缓存，便于查询
        //使用hash存储订单信息：key为商品id，hash的key为订单号，value为用户id：
        // 布隆过滤器
//        bloomFilter.add(orderTo.getGoodsId() + "-" + orderTo.getOrderSn());
//        redisTemplate.opsForHash().put(orderTo.getGoodsId(), orderTo.getOrderSn(), order);
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


    @Transactional
    @Override
    public boolean handlerPayResult(AlipayAsyncVo alipayAsyncVo) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", alipayAsyncVo.getOut_trade_no()));
        if ("TRADE_SUCCESS".equals(alipayAsyncVo.getTrade_status())) {
            // 支付成功
            order.setStatus(2);
            order.setPayTime(alipayAsyncVo.getGmt_payment());
            order.setEndTime(alipayAsyncVo.getGmt_close());
            this.updateById(order);
            // 支付成功，应当给用户转入藏品
            transferToUser(order);
            return true;
        } else if ("TRADE_CLOSED".equals(alipayAsyncVo.getTrade_status())) {
            // 超时关闭,这里有可能支付回调还没来，系统超时把订单关闭了
            if (order.getStatus() != 3) {
                order.setStatus(3);
                order.setEndTime(alipayAsyncVo.getGmt_close());
                this.updateById(order);
            }
            return true;
        }
        return false;
    }

    @Override
    public void releaseOrder(String msg) {
        String[] split = msg.split("-");
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", split[0]));
        //订单没有创建成功或者没用支付成功
        if (order == null || order.getStatus() != 2) {
            RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_SEMAPHORE + split[1]);
            semaphore.release();
            if (order != null) {
                // 关闭订单
                order.setStatus(3);
                this.updateById(order);
            }
        }
        // TODO: 订单没有创建成功，是否需要处理？
//        throw new RuntimeException("订单不存在!");
    }

    @Transactional
    @Override
    public void transferByMQ(String orderSn) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", orderSn));
        if (Objects.isNull(order)) {
            throw new RuntimeException("订单【" + orderSn + "】不存在");
        }
        transferToUser(order);
    }

    //TODO 分布式事务 线程编排
    // TODO @Transactional 自调用失效: 因为this调用不会走增强逻辑

    public void transferByMultiTask(Order order) throws ExecutionException, InterruptedException {
        R res = worksClient.getLocalId(order.getGoodsId(), order.getBuyUserId());
        final Integer localId = res.getData("data", new TypeReference<Integer>() {});
        CompletableFuture<UserToken> userTokenTask = CompletableFuture.supplyAsync(() -> {
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
                    // 为了拿到id
                    userToken = saveUserTokenResult.getData("data", new TypeReference<UserToken>() {
                    });
                }
//            userTokenService.getBaseMapper().insert(userToken);
            } else {
                userToken.setCount(userToken.getCount() + order.getNum());
                tradeClient.updateUserToken(userToken);
//            userTokenService.updateById(userToken);
            }
            return userToken;
        },threadPoolExecutor);

        CompletableFuture<NftBatchInfoTo> nftInfoTask = CompletableFuture.supplyAsync(() -> {
            R r = worksClient.getNftInfo(order.getGoodsId());
            NftBatchInfoTo nftBatchInfoTo = null;
            if (r.getCode() == 200) {
                nftBatchInfoTo = r.getData("data", new TypeReference<NftBatchInfoTo>() {
                });
            }
            return nftBatchInfoTo;
        },threadPoolExecutor);

        userTokenTask.thenCombine(nftInfoTask,((userToken, nftBatchInfoTo) -> {
            UserTokenItem item = new UserTokenItem();
            item.setPrice(order.getPrice());
            item.setLocalId(localId);
            item.setStatus(2);
            int gainType = order.getSellUserId().equals("0") ? 1 : 2;
            item.setGainType(gainType);
            item.setStatus(2);
            item.setTokenType(nftBatchInfoTo.getType());
            item.setMapId(userToken.getId());
            tradeClient.saveUserTokenItem(item);
            return nftBatchInfoTo;
        })).thenAccept(nftBatchInfoTo -> {
            worksClient.updateInventory(nftBatchInfoTo.getId(),
                    Long.valueOf(localId),order.getBuyUserId());
        });

        CompletableFuture<Void> logTask = CompletableFuture.runAsync(() -> {
            TransferLog logTo = new TransferLog();
            logTo.setFromUid(order.getSellUserId());
            logTo.setToUid(order.getBuyUserId());
            logTo.setNftId(order.getGoodsId());
            logTo.setLocalId(order.getLocalId());
            logTo.setPrice(order.getPrice());
            logTo.setLocalId(localId);
            // 保存交易日志
            if (order.getSellUserId().equals("0")) {
                logTo.setType(1);
            } else {
                logTo.setType(4);
            }
            tradeClient.saveTransferLog(logTo);
        },threadPoolExecutor);

        CompletableFuture.allOf(userTokenTask,nftInfoTask,logTask).get();

        CompletableFuture<Void> chainTransferTask = CompletableFuture.runAsync(() -> {
            ChainTransferTo chainTransferTo = new ChainTransferTo();
            chainTransferTo.setFromUserId(order.getSellUserId());
            chainTransferTo.setToUserId(order.getBuyUserId());
            chainTransferTo.setArtId(order.getGoodsId());
            chainTransferTo.setLocalId(localId);
            rabbitTemplate.convertAndSend("nft-order-event",
                    "nft.order.chain.transfer",
                    chainTransferTo);
        });
        chainTransferTask.get();
    }

    /**
     * 1、获取（生成）必要信息
     * 2、推进交易流程 ——> 用户资产，资产详情修改和插入
     * 3、交易日志
     * 4、扣减库存
     * 5、链上交易
     */

    /**
     * 1、生成本地 id 远程服务，通过锁定数据库行实现
     * 2、获取 nft 详情
     * 3、获取用户资产
     * 4、增加用户资产，增加用户 nft 项目详情
     * 5、更新库存
     * @param order
     */
    public void transferToUser(Order order) {
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
        // 交易日志
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

        // TODO 考虑到有不限购的藏品，此处也先查后更新
        R result = tradeClient.getUserToken(order.getBuyUserId(), order.getGoodsId());
        UserToken userToken = null;
        if (result.getCode() == 200) {
            userToken = result.getData("data", new TypeReference<UserToken>() {
            });
        }
        // 用户增加资产
        if (userToken == null) {
            userToken = new UserToken();
            userToken.setUserId(order.getBuyUserId());
            userToken.setArtId(order.getGoodsId());
            userToken.setCount(order.getNum());
            userToken.setSail(0);
            R saveUserTokenResult = tradeClient.saveUserToken(userToken);
            if (saveUserTokenResult.getCode() == 200) {
                // 为了拿到id
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
        // user_token_item  增加详情
        tradeClient.saveUserTokenItem(item);
        logTo.setLocalId(localId);
        // 保存交易日志
        if (order.getSellUserId().equals("0")) {
            logTo.setType(1);
        } else {
            logTo.setType(4);
        }
        tradeClient.saveTransferLog(logTo);

        // 更新库存：此处使用自旋锁+乐观锁更新库存,是因为考虑到创建订单和支付已经削峰了，此处竞争应该不大——>错误
        // 这里没用使用锁，因为是在
        worksClient.updateInventory(nftBatchInfoTo.getId(),
                Long.valueOf(localId),order.getBuyUserId());

        // 链上转帐，远程调用的话会阻塞在这里，影响效率
        rabbitTemplate.convertAndSend("nft-order-event",
                "nft.order.chain.transfer",
                chainTransferTo);
        log.info("发货成功！");
    }
}
