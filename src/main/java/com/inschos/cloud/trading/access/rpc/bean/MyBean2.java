package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/4/26 on 15:23
 * 描述：
 * 作者：zhangyunhe
 */
public class MyBean2 {

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
     * 创建时间
     */
    public String created_at;

    /**
     * 修改时间
     */
    public String updated_at;

    /**
     * 删除标识 0删除 1可用
     */
    public String state;

    public MyBean2(String name, String company_id, String type, String category_id, long time) {
        this.name = name;
        this.display_name = name;
        this.product_company_id = company_id;
        this.product_category_id = category_id;
        this.min_math = "1";
        this.max_math = "1";
        this.sell_status = "1";
        this.base_price = "0";
        this.type = type;
        this.pay_mode = "外链";
        this.business_support = "线下理赔";
        this.observation_period = "0";
        this.period_hesitation = "0";
        this.latest_date = "0";
        this.first_date = "0";
        this.created_at = String.valueOf(time);
        this.updated_at = String.valueOf(time);
        this.state = "1";
        this.product_brokerage_id = "0.3";
        this.system_commission_ratio = "0.3";
        this.base_stages_way = "0.3";
        this.base_ratio = "0.3";
    }

}
