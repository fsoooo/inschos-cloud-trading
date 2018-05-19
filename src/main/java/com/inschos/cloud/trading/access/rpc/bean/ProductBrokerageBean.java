package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/5/19 on 14:33
 * 描述：
 * 作者：zhangyunhe
 */
public class ProductBrokerageBean {

    //产品ID 必传
    public long productId;
    //业管UUID 必传
    public String managerUuid;
    //缴费期数（趸交值为1）  必传
    public int payTimes;
    //时间    必传
    public long queryTime;
    //缴别ID 必传
    public long payCategoryId;
    //渠道ID
    public long channelId;
    //代理人ID
    public long agentId;

}
