package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/6/19 on 19:33
 * 描述：
 * 作者：zhangyunhe
 */
public class ChannelBean {

    /**
     * 主键
     */
    public String id;

    /**
     * 父渠道id
     */
    public String pid;

    /**
     * 渠道种类，1-系统根渠道，2-未分配，3-用户
     */
    public String category;

    /**
     * 账户ID
     */
    public String manager_uuid;

    /**
     * 渠道名称
     */
    public String name;

    /**
     * 简称
     */
    public String display_name;

    /**
     * 负责人ID组
     */
    public String leader_id;

    /**
     * 经办人
     */
    public String office_id;

    /**
     * 纳税人身份，1-一般纳税人
     */
    public String taxpayer_identity;

    /**
     * 发票类型，1-普通发票
     */
    public String invoice_type;

    /**
     * 纳税人名称
     */
    public String taxpayer_name;

    /**
     * 纳税人电话
     */
    public String taxpayer_phone;

    /**
     * 纳税人地址
     */
    public String taxpayer_address;

    /***/
    public String tax_office_registration_bank;

    /**
     * 银行账号
     */
    public String bank_account;

    /**
     * 协议／合同有效日期，开始时间
     */
    public String validity_period_start_time;

    /**
     * 协议／合同有效日期，结束时间
     */
    public String validity_period_end_time;

    /**
     * 协议／合同图片
     */
    public String validity_period_image;

    /**
     * 渠道分类 1:内部机构 2:内部团队 3:外部渠道 4:外部中介
     */
    public String type;

    /**
     * 详细地址
     */
    public String address;

    /**
     * poi address
     */
    public String poi_address;

    /**
     * 经度
     */
    public String longitude;

    /**
     * 纬度
     */
    public String latitude;

    /**
     * 统一社会信用代码
     */
    public String unified_social_credit_code;

    /**
     * 开户行名称
     */
    public String bank_name;

    /**
     * 银行卡
     */
    public String bank_card_number;

    /**
     * 代理资格证号
     */
    public String agency_qualification_number;

    /**
     * 资格证有效期，开始
     */
    public String qualification_period_start_time;

    /**
     * 资格证有效期，开始
     */
    public String qualification_period_end_time;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 更新时间
     */
    public String updated_at;

}
