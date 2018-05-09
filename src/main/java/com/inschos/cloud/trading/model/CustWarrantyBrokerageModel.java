package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：
 * 作者：zhangyunhe
 */
public class CustWarrantyBrokerageModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 归属账号uuid
     */
    public String manager_uuid;

    /**
     * 缴费ID
     */
    public String cost_id;

    /**
     * 渠道ID
     */
    public String channel_id;

    /**
     * 代理人ID
     */
    public String agent_id;

    /**
     * 保单佣金
     */
    public String warranty_money;

    /**
     * 业管佣金
     */
    public String manager_money;

    /**
     * 渠道佣金
     */
    public String channel_money;

    /**
     * 代理人佣金
     */
    public String agent_money;

    /**
     * 保单佣金比例
     */
    public String warranty_rate;

    /**
     * 业管佣金比例
     */
    public String manager_rate;

    /**
     * 渠道佣金比例
     */
    public String channel_rate;

    /**
     * 代理人佣金比例
     */
    public String agent_rate;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

    public String account_uuid;
    public String start_time;
    public String end_time;
    public String time_range_type;


}
