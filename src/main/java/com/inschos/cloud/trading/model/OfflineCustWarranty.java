package com.inschos.cloud.trading.model;


import com.inschos.cloud.trading.assist.kit.Time2Kit;
import com.inschos.common.assist.kit.StringKit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018/5/29 on 18:11
 * 描述：
 * 作者：zhangyunhe
 */
public class OfflineCustWarranty {

    public final static List<String> OFFLINE_COLUMN_FIELD_LIST;

    static {
        OFFLINE_COLUMN_FIELD_LIST = new ArrayList<>();
        OFFLINE_COLUMN_FIELD_LIST.add("insured_name");
        OFFLINE_COLUMN_FIELD_LIST.add("policy_holder_name");
        OFFLINE_COLUMN_FIELD_LIST.add("insurance_company");
        OFFLINE_COLUMN_FIELD_LIST.add("insurance_type");
        OFFLINE_COLUMN_FIELD_LIST.add("insurance_product");
        OFFLINE_COLUMN_FIELD_LIST.add("warranty_code");
        OFFLINE_COLUMN_FIELD_LIST.add("payment_time");
        OFFLINE_COLUMN_FIELD_LIST.add("order_time");
        OFFLINE_COLUMN_FIELD_LIST.add("real_income_time");
        OFFLINE_COLUMN_FIELD_LIST.add("start_time");
        OFFLINE_COLUMN_FIELD_LIST.add("end_time");
        OFFLINE_COLUMN_FIELD_LIST.add("premium");
        OFFLINE_COLUMN_FIELD_LIST.add("pay_status");
        OFFLINE_COLUMN_FIELD_LIST.add("brokerage");
        OFFLINE_COLUMN_FIELD_LIST.add("channel_name");
        OFFLINE_COLUMN_FIELD_LIST.add("agent_name");
        OFFLINE_COLUMN_FIELD_LIST.add("reason");
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

    /**
     * 删除标识 0删除 1可用
     */
    public String state;

    public String reason;

    public List<ErrorReason> reasonList;

    public String search;
    public String searchType;

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
    public String search_warranty_code;
    public String time_type;

    public boolean isEnable() {
        if (reasonList == null) {
            reasonList = new ArrayList<>();
        }

        boolean flag = true;

        if (StringKit.isEmpty(warranty_code)) {
            flag = false;
            reasonList.add(new ErrorReason("保单号不能为空", "warrantyCode"));
        }

        if (!StringKit.isEmpty(insured_name) && insured_name.length() > 64) {
            flag = false;
            reasonList.add(new ErrorReason("投保人最多为64个字符", "insuredName"));
        }

        if (!StringKit.isEmpty(policy_holder_name) && policy_holder_name.length() > 64) {
            flag = false;
            reasonList.add(new ErrorReason("被保险人人最多为64个字符", "policyHolderName"));
        }

        if (!StringKit.isEmpty(channel_name) && channel_name.length() > 16) {
            flag = false;
            reasonList.add(new ErrorReason("归属机构最多为16个字符", "channelName"));
        }

        if (!StringKit.isEmpty(agent_name) && agent_name.length() > 16) {
            flag = false;
            reasonList.add(new ErrorReason("归属代理人最多为16个字符", "agentName"));
        }

        if (!StringKit.isInteger(payment_time) || Long.valueOf(payment_time) < 0) {
            flag = false;
            reasonList.add(new ErrorReason("缴费期必须是正整数", "paymentTime"));
        }

        if (StringKit.isEmpty(insurance_product) || insurance_product.length() > 64) {
            flag = false;
            reasonList.add(new ErrorReason("保险产品不能为空，且最多为64个字符", "insuranceProduct"));
        }

        List<SimpleDateFormat> sdfs = new ArrayList<>();
        sdfs.add(new SimpleDateFormat("yyyy/MM/dd"));
        sdfs.add(new SimpleDateFormat("yyyy-MM-dd"));
        sdfs.add(new SimpleDateFormat("yyyy年MM月dd日"));

        if (!StringKit.isEmpty(order_time)) {
            String ot = Time2Kit.parseMillisecondByShowDate(sdfs, order_time);
            if (ot == null) {
                flag = false;
                reasonList.add(new ErrorReason("签单日期格式错误（年/月/日 或者 年-月-日）", "orderTime"));
            } else {
                order_time = ot;
            }
        }

        if (!StringKit.isEmpty(real_income_time)) {
            String rit = Time2Kit.parseMillisecondByShowDate(sdfs, real_income_time);
            if (rit == null) {
                flag = false;
                reasonList.add(new ErrorReason("实收日期格式错误（年/月/日 或者 年-月-日）", "realIncomeTime"));
            } else {
                real_income_time = rit;
            }
        }

        if (!StringKit.isEmpty(start_time)) {
            String st = Time2Kit.parseMillisecondByShowDate(sdfs, start_time);
            if (st == null) {
                flag = false;
                reasonList.add(new ErrorReason("起保时间格式错误（年/月/日 或者 年-月-日）", "startTime"));
            } else {
                start_time = st;
            }
        }

        if (!StringKit.isEmpty(end_time)) {
            String et = Time2Kit.parseMillisecondByShowDate(sdfs, end_time);
            if (et == null) {
                flag = false;
                reasonList.add(new ErrorReason("终止时间格式错误（年/月/日 或者 年-月-日）", "endTime"));
            } else {
                end_time = et;
            }
        }

        if (premium.contains(",")){
            String warrantyPremium = "";
            for (int i = 0; i < premium.length(); i++) {
                if (premium.charAt(i) != ',') {
                    warrantyPremium += premium.charAt(i);
                }
            }
            premium = warrantyPremium;
        }
        if (!StringKit.isNumeric(premium)) {
            flag = false;
            reasonList.add(new ErrorReason("保费必须是数字", "premium"));
        }

//        if (brokerage.contains(",")){
//            String warrantyBrokerage = "";
//            for (int i = 0; i < brokerage.length(); i++) {
//                if (brokerage.charAt(i) != ',') {
//                    warrantyBrokerage += brokerage.charAt(i);
//                }
//            }
//            brokerage = warrantyBrokerage;
//        }
//        if (!StringKit.isNumeric(brokerage)) {
//            flag = false;
//            reasonList.add(new ErrorReason("应收佣金必须是数字", "brokerage"));
//        }

        if (pay_status == null) {
            flag = false;
            reasonList.add(new ErrorReason("支付状态不能为空", "payStatus"));
        } else {
            switch (pay_status) {
                case "未支付":
                    pay_status = CustWarrantyCost.PAY_STATUS_WAIT;
                    break;
                case "已支付":
                    pay_status = CustWarrantyCost.PAY_STATUS_SUCCESS;
                    break;
                default:
                    flag = false;
                    reasonList.add(new ErrorReason("支付状态只能为：未支付/已支付", "payStatus"));
                    break;
            }
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

    public static OfflineCustWarranty getTitleModel() {
        OfflineCustWarranty title = new OfflineCustWarranty();
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



}
