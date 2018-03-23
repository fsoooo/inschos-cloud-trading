package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/22 on 16:43
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicyModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 预投保编号，由保险公司返回
     */
    public String union_order_code;

    /**
     * 保单号
     */
    public String warranty_code;

    /**
     * 内部保单唯一标识
     */
    public String private_code;

    /**
     * 客户ID
     */
    public String user_id;

    /**
     * 客户类型 1个人 2企业
     */
    public String user_type;

    /**
     * 代理人ID为0则为用户自主购买
     */
    public String agent_id;

    /**
     * 渠道ID为0则为用户自主购买
     */
    public String ditch_id;

    /**
     * 计划书ID为0则为用户自主购买
     */
    public String plan_id;

    /**
     * 产品ID
     */
    public String product_id;

    /**
     * 保单价格
     */
    public String premium;

    /**
     * 起保时间
     */
    public String start_time;

    /**
     * 结束时间
     */
    public String end_time;

    /**
     * 保险公司名称
     */
    public String company_name;

    /**
     * 购买份数
     */
    public String count;

    /**
     * 支付时间
     */
    public String pay_time;

    /**
     * 支付方式
     */
    public String pay_way;

    /**
     * 分期方式
     */
    public String by_stages_way;

    /**
     * 投保参数
     */
    public String parameter;

    /**
     * 佣金 0表示未结算，1表示已结算
     */
    public String is_settlement;

    /**
     * 电子保单下载地址
     */
    public String warranty_url;

    /**
     * 保单照片url集合(json)
     */
    public String warranty_image;

    /**
     * 保单来源 1 自购 2线上成交 3线下成交 4导入
     */
    public String warranty_from;

    /**
     * 车牌号
     */
    public String car_code;

    /**
     * 保单类型1表示个人保单，2表示团险保单，3表示车险保单
     */
    public String type;

    /**
     * 保单状态（ 1待核保，2核保失败，3未支付-核保成功，4支付中5支付失败6支付成功，7保障中8待生效9待续保，10已失效，11已退保）
     */
    public String status;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

    /**
     * 删除标识 0删除 1可用
     */
    public String state;

    public Page page;

}
