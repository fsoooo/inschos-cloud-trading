package com.inschos.cloud.trading.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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
     * 天眼佣金
     */
    public String ins_money;

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
     * 天眼佣金比例
     */
    public String ins_rate;

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

    public Page page;


    public CustWarrantyBrokerageModel() {

    }

    public CustWarrantyBrokerageModel(String warranty_uuid, String manager_uuid, String channel_id, String agent_id, String time) {
        this.warranty_uuid = warranty_uuid;
        this.manager_uuid = manager_uuid;
        this.channel_id = channel_id;
        this.agent_id = agent_id;
        this.created_at = time;
        this.updated_at = time;
    }

    public void setBrokerage(BigDecimal warranty_money, BigDecimal ins_money, BigDecimal manager_money, BigDecimal channel_money, BigDecimal agent_money) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        this.warranty_money = decimalFormat.format(warranty_money);
        this.ins_money = decimalFormat.format(ins_money);
        this.manager_money = decimalFormat.format(manager_money);
        this.channel_money = decimalFormat.format(channel_money);
        this.agent_money = decimalFormat.format(agent_money);
    }

    public void setBrokerageRate(BigDecimal warranty_rate, BigDecimal ins_rate, BigDecimal manager_rate, BigDecimal channel_rate, BigDecimal agent_rate) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        this.warranty_rate = decimalFormat.format(warranty_rate);
        this.ins_rate = decimalFormat.format(ins_rate);
        this.manager_rate = decimalFormat.format(manager_rate);
        this.channel_rate = decimalFormat.format(channel_rate);
        this.agent_rate = decimalFormat.format(agent_rate);
    }

}
