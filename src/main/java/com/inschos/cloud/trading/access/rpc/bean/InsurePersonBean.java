package com.inschos.cloud.trading.access.rpc.bean;

import com.inschos.cloud.trading.model.InsuranceParticipantModel;

/**
 * Created by IceAnt on 2018/6/13.
 */
public class InsurePersonBean {

    /**
     * 被保人 投保人的（关系）
     */
    public String relationName;

    /**
     * 姓名
     */
    public String name;

    /**
     * 证件类型（1为身份证，2为护照，3为军官证）
     */
    public String cardType;

    /**
     * 证件号
     */
    public String cardCode;

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
     * 年龄
     */
    public String age;

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
    public String annualIncome;

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



    public InsuranceParticipantModel toParticipant(String warrantyUuid,String type){

        InsuranceParticipantModel participantModel = new InsuranceParticipantModel();

        participantModel.type = type;
        participantModel.relation_name = this.relationName;
        participantModel.name = this.name;
        participantModel.card_type = this.cardType;
        participantModel.card_code = this.cardCode;
        participantModel.phone = this.phone;
        participantModel.occupation = this.occupation;
        participantModel.birthday = this.birthday;
        participantModel.sex = this.sex;
        participantModel.age = this.age;
        participantModel.email = this.email;
        participantModel.nationality = this.nationality;
        participantModel.annual_income = this.annualIncome;
        participantModel.height = this.height;
        participantModel.weight = this.weight;
        participantModel.area = this.area;
        participantModel.address = this.address;
        return participantModel;
    }
}
