package com.inschos.cloud.trading.model;

/**
 * author   meiming_mm@163.com
 * date     2018/7/25
 * version  v1.0.0
 */
public class BankAuthorize {
    /** 主键*/
    public long id;

    /** */
    public String account_uuid;

    /** 默认使用银行卡id*/
    public long bank_id;

    /** 请求序列号*/
    public String request_serial;

    /** 协议过期时间*/
    public String contract_expired_time;

    /** 委托代扣协议id*/
    public String contract_id;

    /** 变更类型*/
    public String change_type;

    /** 签约协议号*/
    public String contract_code;

    /** 微信openID*/
    public String openid;

    /** */
    public int state;

    /** */
    public long created_at;

    /** */
    public long updated_at;


}
