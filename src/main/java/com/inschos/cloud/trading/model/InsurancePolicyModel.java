package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.StringKit;

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
     * 内部保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 投保单号
     */
    public String pro_policy_no;

    /**
     * 保单号
     */
    public String warranty_code;

    /**
     * 公司ID
     */
    public String company_id;

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
     * 保险公司ID
     */
    public String ins_company_id;

    /**
     * 购买份数
     */
    public String count;

    /**
     * 支付时间
     */
    public String pay_time;

    /**
     * 支付方式 1 银联 2 支付宝 3 微信 4现金
     */
    public String pay_way;

    /**
     * 分期方式
     */
    public String by_stages_way;

    /**
     * 佣金 0表示未结算，1表示已结算
     */
    public String is_settlement;

    /**
     * 电子保单下载地址
     */
    public String warranty_url;

    /**
     * 保单来源 1 自购 2线上成交 3线下成交 4导入
     */
    public String warranty_from;

    /**
     * 保单类型1表示个人保单，2表示团险保单，3表示车险保单
     */
    public String type;

    /**
     * 核保状态（01核保中 2核保失败，3核保成功
     */
    public String check_status;

    /**
     * 支付状态 0，1支付中2支付失败3支付成功，
     */
    public String pay_status;

    /**
     * 保单状态 1待处理 2待支付3待生效 4保障中5可续保，6已失效，7已退保  8已过保
     */
    public String warranty_status;

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


    // 待核保
    public static final int ORDER_STATUS_WAITING_UNDERWRITING = 1;
    // 核保失败
    public static final int ORDER_STATUS_UNDERWRITING_FAIL = 2;


    // 未支付-核保成功
    public static final int ORDER_STATUS_UNDERWRITING_UNPAID = 0;
    // 支付中
    public static final int ORDER_STATUS_PAYING = 1;
    // 支付失败
    public static final int ORDER_STATUS_PAYMENT_FAIL = 2;
    // 支付成功
    public static final int ORDER_STATUS_PAYMENT_SUCCESS = 3;


    // 保障中
    public static final int ORDER_STATUS_PROTECTING = 1;
    // 待生效
    public static final int ORDER_STATUS_WAITING_EFFECTIVE = 2;
    // 待续保
    public static final int ORDER_STATUS_WAITING_RENEWAL = 3;
    // 已失效
    public static final int ORDER_STATUS_FAILURE = 4;
    // 已退保
    public static final int ORDER_STATUS_SURRENDER = 5;

    public String createPrivateCode() {
        return "";
    }

    public boolean setCheckStatus(String checkStatus) {
        if (!StringKit.isInteger(checkStatus) || Integer.valueOf(checkStatus) > 2 || Integer.valueOf(checkStatus) < 1) {
            return false;
        }

        this.check_status = checkStatus;
        return true;
    }

    public boolean setPayStatus(String payStatus) {
        if (!StringKit.isInteger(payStatus) || Integer.valueOf(payStatus) > 3 || Integer.valueOf(payStatus) < 0) {
            return false;
        }

        this.pay_status = payStatus;
        return true;
    }

    public boolean setWarrantyStatus(String warrantyStatus) {
        if (!StringKit.isInteger(warrantyStatus) || Integer.valueOf(warrantyStatus) > 5 || Integer.valueOf(warrantyStatus) < 1) {
            return false;
        }

        this.warranty_status = warrantyStatus;
        return true;
    }

    public String getCheckStatusText() {
        String statusText = null;
        int s = Integer.valueOf(check_status);
        switch (s) {
            case ORDER_STATUS_WAITING_UNDERWRITING:
                statusText = "待核保";
                break;
            case ORDER_STATUS_UNDERWRITING_FAIL:
                statusText = "核保失败";
                break;
        }
        return statusText;
    }

    public String getPayStatusText() {
        String statusText = null;
        int s = Integer.valueOf(pay_status);
        switch (s) {
            case ORDER_STATUS_UNDERWRITING_UNPAID:
                statusText = "未支付-核保成功";
                break;
            case ORDER_STATUS_PAYING:
                statusText = "支付中";
                break;
            case ORDER_STATUS_PAYMENT_FAIL:
                statusText = "支付失败";
                break;
            case ORDER_STATUS_PAYMENT_SUCCESS:
                statusText = "支付成功";
                break;
        }
        return statusText;
    }

    public String getWarrantyStatusText() {
        String statusText = null;
        int s = Integer.valueOf(warranty_status);
        switch (s) {
            case ORDER_STATUS_PROTECTING:
                statusText = "保障中";
                break;
            case ORDER_STATUS_WAITING_EFFECTIVE:
                statusText = "待生效";
                break;
            case ORDER_STATUS_WAITING_RENEWAL:
                statusText = "待续保";
                break;
            case ORDER_STATUS_FAILURE:
                statusText = "已失效";
                break;
            case ORDER_STATUS_SURRENDER:
                statusText = "已退保";
                break;
        }
        return statusText;
    }

}
