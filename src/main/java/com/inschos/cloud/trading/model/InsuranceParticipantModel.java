package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/23 on 11:52
 * 描述：
 * 作者：zhangyunhe
 */
public class InsuranceParticipantModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String private_code;

    /**
     * 人员类型: 1投保人 2被保人 3受益人
     */
    public String type;

    /**
     * 被保人 投保人的（关系）
     */
    public String relation_name;

    /**
     * 被保人姓名
     */
    public String name;

    /**
     * 被保车牌号
     */
    public String car_code;

    /**
     * 证件类型（1为身份证，2为护照，3为军官证）
     */
    public String card_type;

    /**
     * 证件号
     */
    public String card_code;

    /**
     * 手机号
     */
    public String phone;

    /**
     * 职业
     */
    public String occupation;

    /**
     * 生日
     */
    public String birthday;

    /**
     * 性别 1 男 2 女
     */
    public String sex;

    /**
     * 邮箱
     */
    public String email;

    /**
     * 国籍
     */
    public String nationality;

    /**
     * 年收入
     */
    public String annual_income;

    /**
     * 身高
     */
    public String height;

    /**
     * 体重
     */
    public String weight;

    /**
     * 地区
     */
    public String area;

    /**
     * 详细地址
     */
    public String address;

    /**
     * 开始时间
     */
    public String start_time;

    /**
     * 结束时间
     */
    public String end_time;


}
