package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.StringKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建日期：2018/5/29 on 18:11
 * 描述：
 * 作者：zhangyunhe
 */
public class OfflineInsurancePolicyModel {

    public final static Map<String, String> COLUMN_FIELD_MAP;

    static {
        COLUMN_FIELD_MAP = new HashMap<>();
        COLUMN_FIELD_MAP.put("A", "insured_name");
        COLUMN_FIELD_MAP.put("B", "policy_holder_name");
        COLUMN_FIELD_MAP.put("C", "insurance_company");
        COLUMN_FIELD_MAP.put("D", "insurance_type");
        COLUMN_FIELD_MAP.put("E", "insurance_product");
        COLUMN_FIELD_MAP.put("F", "warranty_code");
        COLUMN_FIELD_MAP.put("G", "payment_time");
        COLUMN_FIELD_MAP.put("H", "order_time");
        COLUMN_FIELD_MAP.put("I", "real_income_time");
        COLUMN_FIELD_MAP.put("J", "start_time");
        COLUMN_FIELD_MAP.put("K", "end_time");
        COLUMN_FIELD_MAP.put("L", "premium");
        COLUMN_FIELD_MAP.put("M", "pay_status");
        COLUMN_FIELD_MAP.put("N", "brokerage");
        COLUMN_FIELD_MAP.put("O", "channel_name");
        COLUMN_FIELD_MAP.put("P", "agent_name");
        COLUMN_FIELD_MAP.put("Q", "reason");
//        COLUMN_FIELD_MAP.put("Q", "");
//        COLUMN_FIELD_MAP.put("R", "");
//        COLUMN_FIELD_MAP.put("S", "");
//        COLUMN_FIELD_MAP.put("T", "");
//        COLUMN_FIELD_MAP.put("U", "");
//        COLUMN_FIELD_MAP.put("V", "");
//        COLUMN_FIELD_MAP.put("W", "");
//        COLUMN_FIELD_MAP.put("X", "");
//        COLUMN_FIELD_MAP.put("Y", "");
//        COLUMN_FIELD_MAP.put("Z", "");
    }

    /**
     * 主键id
     */
    public String id;

    /**
     * 业管id
     */
    public String manager_uuid;

    /**
     * 保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 被保险人
     */
    public String insured_name;

    /**
     * 投保人
     */
    public String policy_holder_name;

    /**
     * 保险公司
     */
    public String insurance_company;

    /**
     * 险种
     */
    public String insurance_type;

    /**
     * 险种
     */
    public String insurance_product;

    /**
     * 保单号
     */
    public String warranty_code;

    /**
     * 缴费期
     */
    public String payment_time;

    /**
     * 签单日期
     */
    public String order_time;

    /**
     * 实收日期
     */
    public String real_income_time;

    /**
     * 起保时间
     */
    public String start_time;

    /**
     * 终止时间
     */
    public String end_time;

    /**
     * 保费
     */
    public String premium;

    /**
     * 保费支付状态
     */
    public String pay_status;

    /**
     * 应收佣金
     */
    public String brokerage;

    /**
     * 归属机构
     */
    public String channel_name;

    /**
     * 归属代理
     */
    public String agent_name;

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

    public String reason;

    public Page page;

    public String search_company;
    public String search_channel;
    public String search_product;
    public String time_type;

    public boolean isEnable() {
        return !StringKit.isEmpty(warranty_code);
    }

    public boolean isEmptyLine() {
        return StringKit.isEmpty(warranty_code);
    }

    public boolean isTitle() {
        return StringKit.equals(this.warranty_code, "保单号");
    }

    public static OfflineInsurancePolicyModel getTitleModel() {
        OfflineInsurancePolicyModel title = new OfflineInsurancePolicyModel();
        title.insured_name = "被保险人";
        title.policy_holder_name = "投保人";
        title.insurance_company = "保险公司";
        title.insurance_type = "险种";
        title.warranty_code = "保单号";
        title.payment_time = "缴费期";
        title.order_time = "签单日期";
        title.real_income_time = "实收日期";
        title.start_time = "起保时间";
        title.end_time = "终止时间";
        title.premium = "保费";
        title.pay_status = "保费支付状态";
        title.brokerage = "应收佣金";
        title.channel_name = "归属机构";
        title.agent_name = "归属代理";
        title.reason = "导入失败原因";
        return title;
    }

}
