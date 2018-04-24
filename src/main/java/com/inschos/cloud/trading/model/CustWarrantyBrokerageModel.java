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
     * 渠道ID|代理人ID
     */
    public String data_id;

    /**
     * 佣金
     */
    public String money;

    /**
     * 保单佣金比例
     */
    public String rate;

    /**
     * 1保单佣金 2天眼佣金 3业管佣金  4渠道佣金 5代理佣金
     */
    public String type;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

    public String channel_id;
    public String start_time;
    public String end_time;


}
