package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.StringKit;

/**
 * 创建日期：2018/4/19 on 10:49
 * 描述：
 * 作者：zhangyunhe
 */
public class CustWarrantyCostModel {

    /**
     * 主键
     */
    public String id;
    /**
     * 内部保单唯一标识
     */
    public String warranty_uuid;
    /**
     * 应支付时间
     */
    public String pay_time;

    /**
     * 第几期
     */
    public String phase;

    /**
     * 保单价格
     */
    public String premium;

    /**
     * 税费
     */
    public String tax_money;
    /**
     * 实际支付时间
     */
    public String actual_pay_time;
    /**
     * 支付方式 1 银联 2 支付宝 3 微信 4现金
     */
    public String pay_way;
    /**
     * 付款金额
     */
    public String pay_money;
    /**
     * 支付状态  201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功
     */
    public String pay_status;
    /**
     * 创建时间
     */
    public String created_at;
    /**
     * 结束时间
     */
    public String updated_at;
    //======================search====================
    public String account_uuid;
    public String channel_id;
    public String agent_id;
    public String manager_uuid;
    public String start_time;
    public String end_time;
    public String time_range_type;
    public String search;
    public Page page;

    public CustWarrantyCostModel() {

    }
    //预投保
    public static final String APPLY_UNDERWRITING_PRE = "199";
    //待核保
    public static final String APPLY_UNDERWRITING_WAIT = "200";
    // 核保中
    public static final String APPLY_UNDERWRITING_PROCESSING = "201";
    // 核保失败
    public static final String APPLY_UNDERWRITING_FAILURE = "202";
    // 待支付
    public static final String PAY_STATUS_WAIT = "203";
    // 支付中
    public static final String PAY_STATUS_PROCESSING = "204";
    // 支付取消
    public static final String PAY_STATUS_CANCEL = "205";
    // 支付成功
    public static final String PAY_STATUS_SUCCESS = "206";
    //支付失败
    public static final String PAY_STATUS_FAILED = "207";



    public boolean setPayStatus(String payStatus) {
        if (!StringKit.isInteger(payStatus) || Integer.valueOf(payStatus) > 207 || Integer.valueOf(payStatus) < 199) {
            return false;
        }

        this.pay_status = payStatus;
        return true;
    }

    // 银联
    public static final String PAY_WAY_CHINA_UNION = "1";
    // 支付宝
    public static final String PAY_WAY_ALI = "2";
    // 微信
    public static final String PAY_WAY_WE_CHAT = "3";
    // 现金
    public static final String PAY_WAY_CASH = "4";

    public boolean setPayWay(String payWay) {
        if (!StringKit.isInteger(payWay) || Integer.valueOf(payWay) > 4 || Integer.valueOf(payWay) < 1) {
            return false;
        }

        this.pay_way = payWay;
        return true;
    }

    public String payStatusText(String payStatus) {
        String str = "";
        if (payStatus == null) {
            return str;
        }
        // 支付状态 201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功
        switch (payStatus) {
            case APPLY_UNDERWRITING_PRE:
                str = "预投保";
                break;
            case APPLY_UNDERWRITING_WAIT:
                str = "待核保";
                break;
            case APPLY_UNDERWRITING_PROCESSING:
                str = "核保中";
                break;
            case APPLY_UNDERWRITING_FAILURE:
                str = "核保失败";
                break;
            case PAY_STATUS_WAIT:
                str = "待支付";
                break;
            case PAY_STATUS_PROCESSING:
                str = "支付中";
                break;
            case PAY_STATUS_CANCEL:
                str = "支付取消";
                break;
            case PAY_STATUS_SUCCESS:
                str = "支付成功";
                break;
            case PAY_STATUS_FAILED:
                str = "支付失败";
                break;
        }
        return str;
    }

    public String payWayText(String payWay) {
        String str = "";
        if (payWay == null) {
            return str;
        }
        // 1 银联 2 支付宝 3 微信 4 现金
        switch (payWay) {
            case PAY_WAY_CHINA_UNION:
                str = "银联";
                break;
            case PAY_WAY_ALI:
                str = "支付宝";
                break;
            case PAY_WAY_WE_CHAT:
                str = "微信";
                break;
            case PAY_WAY_CASH:
                str = "现金";
                break;
        }
        return str;
    }

}
