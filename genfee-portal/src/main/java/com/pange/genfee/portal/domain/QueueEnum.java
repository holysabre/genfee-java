package com.pange.genfee.portal.domain;

import lombok.Getter;

/**
 * 消息队列枚举类
 * @auther Pange
 * @description
 * @date {2025/4/2}
 */
@Getter
public enum QueueEnum {
    /**
     * 通知消息队列
     */
    QUEUE_ORDER_CANCEL("genfee.order.direct","genfee.order.cancel","genfee.order.cancel"),
    /**
     * 通知消息ttl队列
     */
    QUEUE_TTL_ORDER_CANCEL("genfee.order.direct.ttl","genfee.order.cancel.ttl","genfee.order.cancel.ttl");

    //交换机名称
    private final String exchange;
    //队列名称
    private final String name;
    //路由键
    private final String routeKey;

    QueueEnum(String exchange,String name,String routeKey){
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
