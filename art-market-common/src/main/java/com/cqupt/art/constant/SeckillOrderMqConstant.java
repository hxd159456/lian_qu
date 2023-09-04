package com.cqupt.art.constant;

public interface SeckillOrderMqConstant {
    String SECKILL_ORDER_EXCHANGE = "nft-seckill-order-event";
    String CREATE_SECKILL_ORDER_ROUTING_KEY = "nft.order.seckill.create.order";
    String RELEASE_SECKILL_ORDER_ROUTING_KEY = "nft.order.seckill.release.order";
    String SECKILL_CREATE_ORDER_QUEUE = "nft.order.seckill.order.queue";
    String SECKILL_ORDER_DELAY_QUEUE = "nft.seckill.order.delay.queue";

    String SECKILL_ORDER_DELAY_ROUTING_KEY = "nft.seckill.order.delay.order";

    String SECKILL_RELEASE_ORDER_QUEUE = "nft.seckill.order.release.queue";

    String REDIS_SECKILL_ORDER_MESSAGE_PREFIX = "seckill:order:message:";
}
