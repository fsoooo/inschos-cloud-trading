package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：
 * 作者：zhangyunhe
 */
public class BrokerageCommissionModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String private_code;

    /**
     * 系统佣金比
     */
    public String product_rate;

    /**
     * 渠道佣金比
     */
    public String ditch_rate;

    /**
     * 代理人佣金比
     */
    public String agent_rate;

    /**
     * 系统佣金
     */
    public String product_money;

    /**
     * 渠道佣金
     */
    public String ditch_money;

    /**
     * 代理人佣金
     */
    public String agent_money;


}
