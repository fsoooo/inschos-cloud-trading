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
     * 保单价格
     */
    public String premium;
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
     * 支付状态  1-核保中 2-核保失败 3-待支付 4-支付中 5-支付取消 6-支付成功
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

    public CustWarrantyCostModel () {

    }

    // 核保成功
    public static final String APPLY_UNDERWRITING_SUCCESS = "0";
    // 核保中
    public static final String APPLY_UNDERWRITING_PROCESSING = "1";
    // 核保失败
    public static final String APPLY_UNDERWRITING_FAILURE = "2";

    // 待支付
    public static final String PAY_STATUS_WAIT = "3";
    // 支付中
    public static final String PAY_STATUS_PROCESSING = "4";
    // 支付取消
    public static final String PAY_STATUS_CANCEL = "5";
    // 支付成功
    public static final String PAY_STATUS_SUCCESS = "6";

    public boolean setPayStatus(String payStatus) {
        if (!StringKit.isInteger(payStatus) || Integer.valueOf(payStatus) > 6 || Integer.valueOf(payStatus) < 1) {
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
        // 支付状态 1-核保中 2-核保失败 3-待支付 4-支付中 5-支付取消 6-支付成功
        switch (payStatus) {
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
