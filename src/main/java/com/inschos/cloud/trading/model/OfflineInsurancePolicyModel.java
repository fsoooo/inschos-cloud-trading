package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.StringKit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public List<ErrorReason> reasonList;

    public static class ErrorReason {
        public String reason;
        public String reasonField;

        public ErrorReason() {

        }

        public ErrorReason(String reason, String reasonField) {
            this.reason = reason;
            this.reasonField = reasonField;
        }
    }

    public Page page;

    public String search_company;
    public String search_channel;
    public String search_product;
    public String time_type;

    public boolean isEnable() {
        if (reasonList == null) {
            reasonList = new ArrayList<>();
        }

        boolean flag = true;

        if (StringKit.isEmpty(warranty_code)) {
            flag = false;
            reasonList.add(new ErrorReason("保单号不能为空", "warranty_code"));
        }

        if (!StringKit.isEmpty(order_time)) {
            String ot = parseMillisecond(order_time);
            if (ot == null) {
                flag = false;
                reasonList.add(new ErrorReason("签单日期格式错误（年/月/日）", "order_time"));
            } else {
                order_time = ot;
            }
        }

        if (!StringKit.isEmpty(real_income_time)) {
            String rit = parseMillisecond(real_income_time);
            if (rit == null) {
                flag = false;
                reasonList.add(new ErrorReason("实收日期格式错误（年/月/日）", "real_income_time"));
            } else {
                real_income_time = rit;
            }
        }

        if (!StringKit.isEmpty(start_time)) {
            String st = parseMillisecond(start_time);
            if (st == null) {
                flag = false;
                reasonList.add(new ErrorReason("起保时间格式错误（年/月/日）", "start_time"));
            } else {
                start_time = st;
            }
        }

        if (!StringKit.isEmpty(end_time)) {
            String et = parseMillisecond(end_time);
            if (et == null) {
                flag = false;
                reasonList.add(new ErrorReason("终止时间格式错误（年/月/日）", "end_time"));
            } else {
                end_time = et;
            }
        }

        if (!StringKit.isEmpty(premium) && !StringKit.isNumeric(premium)) {
            flag = false;
            reasonList.add(new ErrorReason("保费必须是数字", "premium"));
        }

        if (!StringKit.isEmpty(brokerage) && !StringKit.isNumeric(brokerage)) {
            flag = false;
            reasonList.add(new ErrorReason("应收佣金必须是数字", "brokerage"));
        }

        return flag;
    }

    public void addErrorReason(String reason, String reasonField) {
        if (StringKit.isEmpty(reason) || StringKit.isEmpty(reasonField)) {
            return;
        }

        if (reasonList == null) {
            reasonList = new ArrayList<>();
        }

        boolean flag = false;
        ErrorReason old = null;
        for (ErrorReason errorReason : reasonList) {
            if (StringKit.equals(errorReason.reasonField, reasonField)) {
                flag = true;
                old = errorReason;
                break;
            }
        }

        if (flag) {
            old.reason = old.reason + reason;
        } else {
            reasonList.add(new ErrorReason(reason, reasonField));
        }
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
        title.insurance_product = "保险产品";
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

    /**
     * 格式化时间戳用
     *
     * @param time 时间
     * @return showDate指定sdf的格式
     */
    private String parseMillisecond(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (!StringKit.isEmpty(time)) {
            try {
                Date parse = sdf.parse(time);
                return String.valueOf(parse.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

}
