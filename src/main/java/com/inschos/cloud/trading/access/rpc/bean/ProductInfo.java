package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/4/19 on 14:47
 * 描述：
 * 作者：zhangyunhe
 */
public class ProductInfo {

    public String id;

    /**
     * 保险产品名称
     */
    public String name;
    /**
     * 保险产品简称
     */
    public String display_name;
    /**
     * 保险产品说明
     */
    public String content;
    /**
     * 公司ID
     */
    public String product_company_id;
    /**
     * 分类ID
     */
    public String product_category_id;
    /**
     * 佣金比ID
     */
    public String product_brokerage_id;
    /**
     * 最小投保人数
     */
    public String min_math;
    /**
     * 最大投保人数
     */
    public String max_math;
    /**
     * 可售状态 0停售 1可售
     */
    public String sell_status;
    /**
     * 基础保费
     */
    public String base_price;
    /**
     * 系统佣金比
     */
    public String system_commission_ratio;
    /**
     * 基础佣金比缴别
     */
    public String base_stages_way;
    /**
     * 基础佣金比
     */
    public String base_ratio;
    /**
     * 保险产品类型(险种)
     */
    public String type;
    /**
     * 支付方式
     */
    public String pay_mode;
    /**
     * 业务支持
     */
    public String business_support;
    /**
     * 观察期
     */
    public String observation_period;
    /**
     * 犹豫期
     */
    public String period_hesitation;
    /**
     * 最大起保日期
     */
    public String latest_date;
    /**
     * 最小起保日期
     */
    public String first_date;

    /**
     * 保险公司code
     */
    public String code;

}
