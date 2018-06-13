package com.inschos.cloud.trading.access.rpc.bean;

import java.util.List;

/**
 * Created by IceAnt on 2018/6/13.
 */
public class WarrantyInsureBean {


    public String managerUuid;

    public String accountUuid;

    /**
     * 产品ID
     */
    public String productId;

    /**
     * 代理人ID为null则为用户自主购买
     */
    public String agentId;
    /**
     * 渠道ID为0则为用户自主购买
     */
    public String channelId;
    /**
     * 计划书ID为0则为用户自主购买
     */
    public String planId;

    /**
     * 起保时间
     */
    public String startTime;
    /**
     * 结束时间
     */
    public String endTime;

    /**
     * 购买份数
     */
    public int count;

    /**
     * 缴别ID
     */
    public long payCategoryId;
    /**
     * 缴别名称
     */
    public String payCategoryName;

    public InsurePersonBean policyholder;

    public List<InsurePersonBean> recognizees;

    public InsurePersonBean beneficiary;

}
