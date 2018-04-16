package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/26 on 10:37
 * 描述：
 * 作者：zhangyunhe
 */
public class CustWarrantyClaims {

    /** 主键*/
    public long id;

    /** 内部保单唯一标识*/
    public String warranty_uuid;

    /** 归属账号uuid*/
    public String account_uuid;

    /** 买家uuid*/
    public String buyer_auuid;

    /** 出险人ID*/
    public long warranty_person_id;

    /** 理赔车辆车牌号*/
    public String car_code;

    /** 理赔类型（online／offline）*/
    public String claim_type;

    /** 理赔时提供的材料*/
    public String material;

    /** 保险产品名称*/
    public String product_name;

    /** 赔偿金额*/
    public int premium;

    /** 银行卡号*/
    public String bank_card_number;

    /** 联系人姓名*/
    public String link_name;

    /** 联系人手机号*/
    public String link_phone;

    /** 联系人邮箱*/
    public String link_email;

    /** 理赔的状态，0待审核，1审核中，2审核通过，3审核不通过，4理赔完成*/
    public String status;

    /** 创建时间*/
    public long created_at;

    /** 结束时间*/
    public long updated_at;


}
