package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/23 on 15:45
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePreservationModel {

    public static final int EVENT_TYPE_INSURE = 1;
    public static final int EVENT_TYPE_SURRENDER = 2;
    public static final int EVENT_TYPE_UNDERWRITING = 3;
    public static final int EVENT_TYPE_PRESERVATION_ADD = 4;
    public static final int EVENT_TYPE_PRESERVATION_DELETE = 5;
    public static final int EVENT_TYPE_CLAIMS = 6;

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String private_code;

    /**
     * 客户ID
     */
    public String cust_id;

    /**
     * 客户类型 1个人 2企业
     */
    public String cust_type;

    /**
     * 申请的时间
     */
    public String apply_time;

    /**
     * 事件类型（1投保，2退保，3核保，4加人，5减人，6理赔）
     */
    public String event;

    /**
     * IP地址
     */
    public String ip_address;

    /**
     * 要更改的旧数据
     */
    public String old_content;

    /**
     * 操作的内容
     */
    public String content;

    /**
     * 操作描述
     */
    public String describe;

    /**
     * 保单类型1表示个人保单，2表示团险保单，3表示车险保单
     */
    public String type;

    /**
     * 当前的状态（0为待审核，1为未通过，2为已完成）
     */
    public String status;

    /**
     * 备注
     */
    public String remark;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;


}
