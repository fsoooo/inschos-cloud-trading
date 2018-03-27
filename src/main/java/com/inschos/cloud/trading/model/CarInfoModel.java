package com.inschos.cloud.trading.model;

/**
 * 创建日期：2018/3/26 on 11:59
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInfoModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 车牌号
     */
    public String car_code;

    /**
     * 车主姓名
     */
    public String name;

    /**
     * 车主身份证号
     */
    public String code;

    /**
     * 车主手机号
     */
    public String phone;

    /**
     * 车架号
     */
    public String frame_no;

    /**
     * 发动机号
     */
    public String engine_no;

    /**
     * 发改委编码
     */
    public String vehicle_fgw_code;

    /**
     * 发改委名称
     */
    public String vehicle_fgw_name;

    /**
     * 年份款式
     */
    public String parent_veh_name;

    /**
     * 品牌型号编码
     */
    public String brand_code;

    /**
     * 品牌型号名称
     */
    public String brand_name;

    /**
     * 排量
     */
    public String engine_desc;

    /**
     * 新车购置价
     */
    public String new_car_price;

    /**
     * 含税价格
     */
    public String purchase_price_tax;

    /**
     * 进口标识（0:国产，1:合资，2:进口）
     */
    public String import_flag;

    /**
     * 座位数
     */
    public String seat;

    /**
     * 款型名称
     */
    public String standard_name;

    /**
     * 是否过户车（0:否 1:是）
     */
    public String is_trans;

    /**
     * 备注
     */
    public String remark;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;


}
