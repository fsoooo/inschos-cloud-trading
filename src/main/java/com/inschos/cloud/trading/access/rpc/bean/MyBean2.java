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
     * 保险公司id
     */
    public String insurance_co_id;

    /**
     * 保险产品类型id
     */
    public String category_id;

    /**
     * 最小投保人数
     */
    public String min_people;

    /**
     * 最大投保人数
     */
    public String max_people;

    /**
     * 可售状态 0停售 1可售
     */
    public String sell_status;

    /**
     * 天眼上下架状态，1上架，0下架
     */
    public String ins_status;

    /**
     * 基础保费
     */
    public String base_price;

    /**
     * 基础佣金
     */
    public String base_brokerage;

    /**
     * 支付类型
     */
    public String pay_type;

    /**
     * 观察期
     */
    public String observation_period;

    /**
     * 犹豫期
     */
    public String cooling_off_period;

    /**
     * 最晚起保日期
     */
    public String latest_date;

    /**
     * 最早起保日期
     */
    public String earliest_date;

    /**
     * 产品编码
     */
    public String code;

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


    public MyBean2(String name, String company_id, String code, String category_id, long time) {
        this.name = name;
        this.display_name = name;
        this.insurance_co_id = company_id;
        this.category_id = category_id;
        this.min_people = "1";
        this.max_people = "1";
        this.sell_status = "1";
        this.ins_status = "1";
        this.base_price = "0";
        this.base_price = "0";
        this.base_brokerage = "10";
        this.pay_type = "1";
        this.observation_period = "1";
        this.cooling_off_period = "1";
        this.latest_date = "0";
        this.earliest_date = "0";
        this.code = code;
        this.created_at = String.valueOf(time);
        this.updated_at = String.valueOf(time);
        this.state = "1";
    }

}
