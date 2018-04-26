package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/4/21 on 16:47
 * 描述：
 * 作者：zhangyunhe
 */
public class MyBean {

    public String id;

    /**
     * 保险公司全称
     */
    public String name;

    /**
     * 保险公司简称
     */
    public String display_name;

    /**
     * 分类ID
     */
    public String category_id;

    /**
     * 保险公司代码
     */
    public String code;

    /**
     * 保险公司logo
     */
    public String logo;

    /**
     * 账号银行卡类型
     */
    public String bank_type;

    /**
     * 银行卡账号
     */
    public String bank_num;

    /**
     * 保险公司邮箱
     */
    public String email;

    /**
     * 保险公司链接
     */
    public String url;

    /**
     * 保险公司电话
     */
    public String phone;

    /**
     * 保险公司公众号二维码
     */
    public String code_img;

    /**
     * 状态，0删除，1未删除
     */
    public String status;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 修改时间
     */
    public String updated_at;

    /**
     * 删除标识 0:删除 1:未删除
     */
    public String state = "1";


    public MyBean(String code, String name, String display_name, long time) {
        this.code = code;
        this.name = name;
        this.display_name = display_name;
        this.category_id = "200000000";
        this.logo = code + "_key";
        this.created_at = String.valueOf(time);
        this.updated_at = String.valueOf(time);
    }


    @Override
    public String toString() {
        return "MyBean{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
