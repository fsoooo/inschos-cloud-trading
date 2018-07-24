package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.model.CustWarrantyPerson;
import com.inschos.common.assist.kit.TimeKit;

import java.util.List;

/**
 * Created by IceAnt on 2018/6/14.
 */
public class TradeBean {


    public static class InsureRequest extends BaseRequest {

        @CheckParams(hintName = "产品ID", stringType = CheckParams.StringType.NUMBER, maxLen = 20)
        public String productId;

        @CheckParams(hintName = "起保时间", stringType = CheckParams.StringType.NUMBER, maxLen = 20)
        public String startTime;
        @CheckParams(hintName = "截止时间", stringType = CheckParams.StringType.NUMBER, maxLen = 20)
        public String endTime;

        @CheckParams(hintName = "分数", stringType = CheckParams.StringType.NUMBER, maxLen = 20)
        public String count;

        public String warrantyUuid;

        //业务识别号
        public String businessNo;
        //缴别ID
        public String payCategoryId;
        //投保人
        public InsurePersonData policyholder;
        //被保人
        public List<InsurePersonData> recognizees;
        //受益人
        public InsurePersonData beneficiary;

        public String businessParams;

        public Object selectQuote;
    }

    public static class InsureResponse extends BaseResponse {
        public InsureRspData data;
    }

    public static class InsurePayRequest extends BaseRequest{
        @CheckParams(hintName = "支付号", stringType = CheckParams.StringType.STRING)
        public String payNo;
        //
        @CheckParams(hintName = "支付方式", stringType = CheckParams.StringType.STRING)
        public String payWay;

        public BankData bankData;

    }

    public static class InsurePayResponse extends BaseResponse{
        public InsureRspData data;
    }
    public static class PreInsureResponse extends BaseResponse{
        public InsureRspData data;

    }


    public static class QuoteRequest extends BaseRequest {

        @CheckParams(hintName = "产品ID", stringType = CheckParams.StringType.NUMBER, maxLen = 20)
        public String productId;

        /**本次所有选中项的值*/
        public String new_val;
        /**被改变项的值*/
        public String old_val;
        /**旧的试算因子项*/
        public String old_option;
    }



    public static class InsureRspData{
        //保单状态
        public String status;
        public String statusTxt;
        public String payType;
        public String payUrl;
        public String payNo;
        public String warrantyUuid;
    }

    public static class InsurePersonData {
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


        public CustWarrantyPerson toParticipant(String warrantyUuid, String type, String operTime, String startTime, String endTime) {

            CustWarrantyPerson participantModel = new CustWarrantyPerson();

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
            participantModel.start_time = startTime;
            participantModel.end_time = endTime;
            participantModel.record_start_time = operTime;
            participantModel.record_end_time = TimeKit.curTimeMillis2Str();

            return participantModel;
        }
    }


    public static class BankData{
        public String bankCode;

        public String bankPhone;

        public String name;

        public String certCode;
    }

}
