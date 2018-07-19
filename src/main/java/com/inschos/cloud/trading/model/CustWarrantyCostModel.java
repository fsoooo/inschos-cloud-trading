package com.inschos.cloud.trading.model;


import com.inschos.common.assist.kit.StringKit;
import com.inschos.common.assist.kit.TimeKit;

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
     * 结算状态，0-未结算，1-已结算
     */
    public String is_settlement;

    /**
     * 结算单uuid
     */
    public String bill_uuid;

    /**
     * 创建时间
     */
    public String created_at;
    /**
     * 结束时间
     */
    public String updated_at;
    //======================search====================

    public String manager_uuid;
    public String time_type;
    public String start_time;
    public String end_time;
    public String time_range_type;
    public String search;
    public String searchType;
    public String max_time = String.valueOf(TimeKit.MAX_MILLIS);
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

    // =========================================== OnlineModel ===========================================
    /**
     * 主键
     */
    public String cust_warranty_id;

    /**
     * 投保单号
     */
    public String pre_policy_no;

    /**
     * 保单号
     */
    public String warranty_code;

    /**
     * 买家uuid
     */
    public String account_uuid;

    /**
     * 代理人ID为null则为用户自主购买
     */
    public String agent_id;

    /**
     * 渠道ID为0则为用户自主购买
     */
    public String channel_id;

    /**
     * 计划书ID为0则为用户自主购买
     */
    public String plan_id;

    /**
     * 产品ID
     */
    public String product_id;

    /**
     * 起保时间
     */
    public String warranty_start_time;

    /**
     * 结束时间
     */
    public String warranty_end_time;

    /**
     * 保险公司ID
     */
    public String ins_company_id;

    /**
     * 购买份数
     */
    public String warranty_count;

    /**
     * 分期方式
     */
    public String by_stages_way;

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
    public String warranty_type;

    /**
     * 保单状态 1投保中 2待生效 3保障中 4可续保 5已过保，6已退保
     */
    public String warranty_status;

    /**
     * 创建时间
     */
    public String warranty_created_at;

    /**
     * 结束时间
     */
    public String warranty_updated_at;

    // =========================================== OnlineBrokerageModel ===========================================
    /**
     * 主键
     */
    public String brokerage_id;

    /**
     * 保单佣金
     */
    public String brokerage_warranty_money;

    /**
     * 天眼佣金
     */
    public String brokerage_ins_money;

    /**
     * 业管佣金
     */
    public String brokerage_manager_money;

    /**
     * 渠道佣金
     */
    public String brokerage_channel_money;

    /**
     * 代理人佣金
     */
    public String brokerage_agent_money;

    /**
     * 保单佣金比例
     */
    public String brokerage_warranty_rate;

    /**
     * 天眼佣金比例
     */
    public String brokerage_ins_rate;

    /**
     * 业管佣金比例
     */
    public String brokerage_manager_rate;

    /**
     * 渠道佣金比例
     */
    public String brokerage_channel_rate;

    /**
     * 代理人佣金比例
     */
    public String brokerage_agent_rate;

    /**
     * 车险核算佣金
     */
    public String brokerage_car_integral;

    /**
     * 创建时间
     */
    public String brokerage_created_at;

    /**
     * 结束时间
     */
    public String brokerage_updated_at;


    // =========================================== OnlinePersonModel ===========================================
    /**
     * 主键
     */
    public String person_id;

    /**
     * 人员类型: 1投保人 2被保人 3受益人
     */
    public String person_type;

    /**
     * 被保人 投保人的（关系）
     */
    public String person_relation_name;

    /**
     * 被保人单号
     */
    public String person_out_order_no;

    /**
     * 姓名
     */
    public String person_name;

    /**
     * 证件类型（1为身份证，2为护照，3为军官证）
     */
    public String person_card_type;

    /**
     * 证件号
     */
    public String person_card_code;

    /**
     * 手机号
     */
    public String person_phone;

    /**
     * 开始时间
     */
    public String person_start_time;

    /**
     * 结束时间
     */
    public String person_end_time;

    /**
     * 记录开始时间
     */
    public String person_record_start_time;

    /**
     * 记录结束时间
     */
    public String person_record_end_time;

}
