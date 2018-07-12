package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.annotation.CheckParams;

/**
 * author   meiming_mm@163.com
 * date     2018/7/11
 * version  v1.0.0
 */
public class BankBean {

    public static class  UpdateRequest extends BaseRequest{


        @CheckParams(hintName = "ID",stringType = CheckParams.StringType.NUMBER,maxLen = 20)
        public String id;

        public String bankName;

        public String bankCity;
        @CheckParams(hintName = "银行卡号",stringType = CheckParams.StringType.STRING,maxLen = 20)
        public String bankCode;


        @CheckParams(hintName = "银行预留手机号",stringType = CheckParams.StringType.STRING,maxLen = 13)
        public String bankPhone;

    }

    public static class  DetailRequest extends BaseRequest{
        @CheckParams(hintName = "ID",stringType = CheckParams.StringType.NUMBER,maxLen = 20)
        public String id;
    }

    public static class  ListRequest extends BaseRequest{

    }


    public static class ApplyAuthRequest extends BaseRequest{

        @CheckParams(hintName = "姓名",stringType = CheckParams.StringType.STRING,maxLen = 20)
        public String name;

        @CheckParams(hintName = "银行预留手机号",stringType = CheckParams.StringType.STRING,maxLen = 13)
        public String bankPhone;

        @CheckParams(hintName = "银行卡号",stringType = CheckParams.StringType.STRING,maxLen = 20)
        public String bankCode;

        @CheckParams(hintName = "身份证号",stringType = CheckParams.StringType.STRING,maxLen = 18)
        public String idCard;

    }


    public static class ConfirmAuthRequest extends BaseRequest{

        @CheckParams(hintName = "验证序号",stringType = CheckParams.StringType.STRING,maxLen = 50)
        public String requestId;

        @CheckParams(hintName = "验证码",stringType = CheckParams.StringType.STRING,maxLen = 10)
        public String vdCode;
    }

    public static class ApplyAuthResponse extends BaseResponse{
        public ApplyData data;
    }


    public static class ApplyData{
        public String requestId;
    }



    public static class BankData{

        public long id;

        /**
         * 银行名称：工商银行,建设银行等
         */
        public String bankName;

        /**
         * 开户行城市(地址)
         */
        public String bankCity;

        /**
         * 银行卡号
         */
        public String bankCode;

        /**
         * 银行卡类型：储蓄卡，借记卡等
         */
        public String bankType;

        /**
         * 银行卡绑定手机
         */
        public String bankPhone;

        /**
         * 审核状态(授权状态):是否通过审核检验，默认1未审核,2已审核,3审核失败
         */
        public int status;

    }
}
