package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/5/19 on 14:33
 * 描述：
 * 作者：zhangyunhe
 */
public class ProductBrokerageInfoBean {

    //佣金比比值是百分比
    //基础佣金比
    public float basicBrokerage;
    //天眼佣金比
    public float insBrokerage;
    //平台(业管)佣金比
    public float platformBrokerage;
    //渠道佣金比
    public float channelBrokerage;
    //代理人佣金比
    public float agentBrokerage;
}
