package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.CardCodeKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.extend.car.ExtendCarInsurancePolicy;

/**
 * 创建日期：2018/3/23 on 11:52
 * 描述：
 * 作者：zhangyunhe
 */
public class InsuranceParticipantModel {

    public final static String TYPE_POLICYHOLDER = "1";
    public final static String TYPE_INSURED = "2";
    public final static String TYPE_BENEFICIARY = "3";

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 人员类型: 1投保人 2被保人 3受益人
     */
    public String type;

    /**
     * 被保人 投保人的（关系）
     */
    public String relation_name;

    /**
     * 被保人单号
     */
    public String out_order_no;

    /**
     * 姓名
     */
    public String name;

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

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 修改时间
     */
    public String updated_at;

    public InsuranceParticipantModel() {

    }

    /**
     * 该构造只限车险使用
     *
     * @param warrantyUuid 内部保单标识
     * @param type         人员类型
     * @param relationName 默认为"1"
     * @param time         时间戳
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param personInfo   人员信息
     */
    public InsuranceParticipantModel(String warrantyUuid, String type, String relationName, String time, String startTime, String endTime, ExtendCarInsurancePolicy.InsuranceParticipant personInfo) {
        this.warranty_uuid = warrantyUuid;
        this.type = type;
        this.relation_name = relationName;
        switch (type) {
            case TYPE_POLICYHOLDER:
                this.name = personInfo.insuredName;
                this.card_type = personInfo.insuredIdType;
                this.card_code = personInfo.insuredID;
                this.phone = personInfo.insuredMobile;
                this.birthday = personInfo.insuredBirthday;
                this.sex = personInfo.insuredSex;
                this.age = personInfo.getAge(personInfo.insuredBirthday);
                break;
            case TYPE_INSURED:
                this.name = personInfo.applicantName;
                this.card_type = personInfo.applicantIdType;
                this.card_code = personInfo.applicantID;
                this.phone = personInfo.applicantMobile;
                this.birthday = personInfo.applicantBirthday;
                this.sex = personInfo.applicantSex;
                this.age = personInfo.getAge(personInfo.applicantBirthday);
                break;
        }
        this.start_time = startTime;
        this.end_time = endTime;
        this.created_at = time;
        this.updated_at = time;
    }


    public boolean setCardType(String cardType) {
        if (!StringKit.isInteger(cardType)) {
            return false;
        }

        this.card_type = cardType;

        return true;
    }

    public String getCardTypeText(int cardType) {
        String cardTypeText = null;
        switch (cardType) {
            case CardCodeKit.CARD_TYPE_ID_CARD:
                cardTypeText = "身份证";
                break;
            case CardCodeKit.CARD_TYPE_PASSPORT:
                cardTypeText = "护照";
                break;
            case CardCodeKit.CARD_TYPE_MILITARY_CERTIFICATE:
                cardTypeText = "军官证";
                break;
        }
        return cardTypeText;
    }

    // 先设置证件类型
    public boolean setCardCode(String cardCode) {
        if (!StringKit.isInteger(cardCode)) {
            return false;
        }

        if (!CardCodeKit.isLegal(Integer.valueOf(card_type), cardCode)) {
            return false;
        }

        this.card_code = cardCode;

        return true;
    }

}
