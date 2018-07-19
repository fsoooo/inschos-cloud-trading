package com.inschos.cloud.trading.model;


import com.inschos.common.assist.kit.TimeKit;

import java.util.List;

/**
 * 创建日期：2018/6/25 on 16:01
 * 描述：
 * 作者：zhangyunhe
 */
public class BillDetailModel {

    /**
     * 主键id
     */
    public String id;

    /**
     * 支付id
     */
    public String cost_id;

    /**
     * 结算单uuid
     */
    public String bill_uuid;

    /**
     * 保单uuid
     */
    public String warranty_uuid;

    /**
     * 保单类型，1-网销，2-线下单
     */
    public String type;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 更新时间
     */
    public String updated_at;

    public Page page;

    public String search;
    public String searchType;
    public String max_time = String.valueOf(TimeKit.MAX_MILLIS);
    public String product_id_string;
    public List<String> warranty_list;


    // 网销
    public static final String TYPE_ON_LINE = "1";
    // 线下单
    public static final String TYPE_OFF_LINE = "2";

    public String typeText(String type) {
        String str = "";
        if (type == null) {
            return str;
        }
        // 车险类型 1-网销，2-线下单
        switch (type) {
            case TYPE_ON_LINE:
                str = "网销";
                break;
            case TYPE_OFF_LINE:
                str = "线下单";
                break;
        }
        return str;
    }

    // =========================================== BillModel ===========================================
    /**
     * 主键id
     */
    public String bill_id;

    /**
     * 业管uuid
     */
    public String manager_uuid;

    /**
     * 结算标识
     */
    public String bill_name;

    /**
     * 保险公司
     */
    public String insurance_company_id;

    /**
     * 负责人
     */
    public String principal;

    /**
     * 结算状态，0-未结算，1-结算
     */
    public String is_settlement;

    /**
     * 备注
     */
    public String remark;

    /**
     * 结算时间
     */
    public String bill_time;

    /**
     * 结算金额
     */
    public String bill_money;

    /**
     * 创建时间
     */
    public String bill_created_at;

    /**
     * 更新时间
     */
    public String bill_updated_at;

    // =========================================== OfflineModel ===========================================
    /**
     * 主键id
     */
    public String offline_id;

    /**
     * 被保险人
     */
    public String offline_insured_name;

    /**
     * 投保人
     */
    public String offline_policy_holder_name;

    /**
     * 保险公司
     */
    public String offline_insurance_company;

    /**
     * 险种
     */
    public String offline_insurance_type;

    /**
     * 险种
     */
    public String offline_insurance_product;

    /**
     * 保单号
     */
    public String offline_warranty_code;

    /**
     * 缴费期
     */
    public String offline_payment_time;

    /**
     * 签单日期
     */
    public String offline_order_time;

    /**
     * 实收日期
     */
    public String offline_real_income_time;

    /**
     * 起保时间
     */
    public String offline_start_time;

    /**
     * 终止时间
     */
    public String offline_end_time;

    /**
     * 保费
     */
    public String offline_premium;

    /**
     * 保费支付状态
     */
    public String offline_pay_status;

    /**
     * 应收佣金
     */
    public String offline_brokerage;

    /**
     * 归属机构
     */
    public String offline_channel_name;

    /**
     * 归属代理
     */
    public String offline_agent_name;

    /**
     * 创建时间
     */
    public String offline_created_at;

    /**
     * 结束时间
     */
    public String offline_updated_at;

    // =========================================== OnlineModel ===========================================
    /**
     * 主键
     */
    public String online_id;

    /**
     * 投保单号
     */
    public String online_pre_policy_no;

    /**
     * 保单号
     */
    public String online_warranty_code;

    /**
     * 买家uuid
     */
    public String online_account_uuid;

    /**
     * 代理人ID为null则为用户自主购买
     */
    public String online_agent_id;

    /**
     * 渠道ID为0则为用户自主购买
     */
    public String online_channel_id;

    /**
     * 计划书ID为0则为用户自主购买
     */
    public String online_plan_id;

    /**
     * 产品ID
     */
    public String online_product_id;

    /**
     * 起保时间
     */
    public String online_start_time;

    /**
     * 结束时间
     */
    public String online_end_time;

    /**
     * 保险公司ID
     */
    public String online_ins_company_id;

    /**
     * 购买份数
     */
    public String online_count;

    /**
     * 分期方式
     */
    public String online_pay_category_id;

    /**
     * 电子保单下载地址
     */
    public String online_warranty_url;

    /**
     * 保单来源 1 自购 2线上成交 3线下成交 4导入
     */
    public String online_warranty_from;

    /**
     * 保单类型1表示个人保单，2表示团险保单，3表示车险保单
     */
    public String online_type;

    /**
     * 保单状态 1投保中 2待生效 3保障中 4可续保 5已过保，6已退保
     */
    public String online_warranty_status;

    /**
     * 创建时间
     */
    public String online_created_at;

    /**
     * 结束时间
     */
    public String online_updated_at;

    // =========================================== OnlineCostModel ===========================================

    /**
     * 应支付时间
     */
    public String online_premium;

    /**
     * 应支付时间
     */
    public String online_pay_time;

    /**
     * 第几期
     */
    public String online_phase;

    /**
     * 税费
     */
    public String online_tax_money;
    /**
     * 实际支付时间
     */
    public String online_actual_pay_time;

    /**
     * 支付方式 1 银联 2 支付宝 3 微信 4现金
     */
    public String online_pay_way;

    /**
     * 付款金额
     */
    public String online_pay_money;

    /**
     * 支付状态  201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功
     */
    public String online_pay_status;

    /**
     * 创建时间
     */
    public String online_cost_created_at;

    /**
     * 结束时间
     */
    public String online_cost_updated_at;


    // =========================================== OnlineBrokerageModel ===========================================
    /**
     * 主键
     */
    public String online_brokerage_id;

    /**
     * 保单佣金
     */
    public String online_brokerage_warranty_money;

    /**
     * 天眼佣金
     */
    public String online_brokerage_ins_money;

    /**
     * 业管佣金
     */
    public String online_brokerage_manager_money;

    /**
     * 渠道佣金
     */
    public String online_brokerage_channel_money;

    /**
     * 代理人佣金
     */
    public String online_brokerage_agent_money;

    /**
     * 保单佣金比例
     */
    public String online_brokerage_warranty_rate;

    /**
     * 天眼佣金比例
     */
    public String online_brokerage_ins_rate;

    /**
     * 业管佣金比例
     */
    public String online_brokerage_manager_rate;

    /**
     * 渠道佣金比例
     */
    public String online_brokerage_channel_rate;

    /**
     * 代理人佣金比例
     */
    public String online_brokerage_agent_rate;

    /**
     * 车险核算佣金
     */
    public String online_brokerage_car_integral;

    /**
     * 创建时间
     */
    public String online_brokerage_created_at;

    /**
     * 结束时间
     */
    public String online_brokerage_updated_at;


    // =========================================== OnlinePersonModel ===========================================
    /**
     * 主键
     */
    public String online_person_id;

    /**
     * 人员类型: 1投保人 2被保人 3受益人
     */
    public String online_person_type;

    /**
     * 被保人 投保人的（关系）
     */
    public String online_person_relation_name;

    /**
     * 被保人单号
     */
    public String online_person_out_order_no;

    /**
     * 姓名
     */
    public String online_person_name;

    /**
     * 证件类型（1为身份证，2为护照，3为军官证）
     */
    public String online_person_card_type;

    /**
     * 证件号
     */
    public String online_person_card_code;

    /**
     * 手机号
     */
    public String online_person_phone;

    /**
     * 职业
     */
    public String online_person_occupation;

    /**
     * 生日
     */
    public String online_person_birthday;

    /**
     * 性别 1 男 2 女
     */
    public String online_person_sex;

    /**
     * 年龄
     */
    public String online_person_age;

    /**
     * 邮箱
     */
    public String online_person_email;

    /**
     * 国籍
     */
    public String online_person_nationality;

    /**
     * 年收入
     */
    public String online_person_annual_income;

    /**
     * 身高
     */
    public String online_person_height;

    /**
     * 体重
     */
    public String online_person_weight;

    /**
     * 地区
     */
    public String online_person_area;

    /**
     * 详细地址
     */
    public String online_person_address;

    /**
     * 开始时间
     */
    public String online_person_start_time;

    /**
     * 结束时间
     */
    public String online_person_end_time;

    /**
     * 记录开始时间
     */
    public String online_person_record_start_time;

    /**
     * 记录结束时间
     */
    public String online_person_record_end_time;
}
