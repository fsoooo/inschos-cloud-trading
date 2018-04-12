package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.annotation.CheckParams;

/**
 * 创建日期：2018/3/22 on 14:03
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicy {

    public static class GetInsurancePolicyListForBusinessManagerRequest extends BaseRequest {
        // 保单状态
        public String warrantyStatus;
        // 起保时间
        @CheckParams
        public String startTime;
        // 结束时间
        @CheckParams
        public String endTime;
        // 保单类型 1-个人保单，2-团险保单，3-车险保单
        @CheckParams
        public String type;
        // 保单来源
        public String warrantyFrom;
        // 渠道
        public String ditchId;
        // 搜索关键字
        public String searchKey;
    }

    public static class GetInsurancePolicyListForBusinessManagerResponse extends BaseResponse {

    }

    public static class GetInsurancePolicy {

        //内部保单唯一标识
        public String warrantyUuid;

        //投保单号
        public String proPolicyNo;

        //保单号
        public String warrantyCode;

        //归属账号uuid
        public String accountUuid;

        //买家uuid
        public String buyerAuuid;

        //代理人ID为null则为用户自主购买
        public String agentAuuid;

        //渠道ID为0则为用户自主购买
        public String ditchId;

        //计划书ID为0则为用户自主购买
        public String planId;

        //产品ID
        public String productId;

        //保单价格
        public String premium;

        //保单价格（显示用）
        public String premiumText;

        //起保时间
        public String startTime;

        //起保时间（显示用）
        public String startTimeText;

        //结束时间
        public String endTime;

        //结束时间（显示用）
        public String endTimeText;

        //保险公司ID
        public String insCompanyId;

        //购买份数
        public String count;

        //支付时间
        public String payTime;

        //支付时间（显示用）
        public String payTimeText;

        //支付方式 1 银联 2 支付宝 3 微信 4现金
        public String payWay;

        //支付方式 1 银联 2 支付宝 3 微信 4现金（显示用）
        public String payWayText;

        //分期方式
        public String byStagesWay;

        //佣金 0表示未结算，1表示已结算
        public String isSettlement;

        //佣金 0表示未结算，1表示已结算（显示用）
        public String isSettlementText;

        //电子保单下载地址
        public String warrantyUrl;

        //保单来源 1 自购 2线上成交 3线下成交 4导入
        public String warrantyFrom;

        //保单来源 1 自购 2线上成交 3线下成交 4导入（显示用）
        public String warrantyFromText;

        //保单类型1表示个人保单，2表示团险保单，3表示车险保单
        public String type;

        //核保状态（01核保中 2核保失败，3核保成功
        public String checkStatus;

        //核保状态（01核保中 2核保失败，3核保成功（显示用）
        public String checkStatusText;

        //支付状态 0，1支付中2支付失败3支付成功，
        public String payStatus;

        //支付状态 0，1支付中2支付失败3支付成功，（显示用）
        public String payStatusText;

        //保单状态 1待处理 2待支付3待生效 4保障中5可续保，6已失效，7已退保  8已过保
        public String warrantyStatus;

        //保单状态 1待处理 2待支付3待生效 4保障中5可续保，6已失效，7已退保  8已过保（显示用）
        public String warrantyStatusText;

        //更新时间
        public String updatedAt;

        //更新时间（显示用）
        public String updatedAtText;


    }

    public static class GetInsurancePolicyDetailForCarInsuranceRequest extends BaseRequest {

    }

    public static class GetInsurancePolicyDetailForCarInsuranceResponse extends BaseResponse {

    }

    public static class GetInsurancePolicyDetailForOtherInsuranceRequest extends BaseRequest {

    }

    public static class GetInsurancePolicyDetailForOtherInsuranceResponse extends BaseResponse {

    }


//    // 获取保单详情
//    public static class InsurancePolicyDetailRequest extends BaseRequest {
//        public String privateCode;
//    }
//
//    public static class InsurancePolicyDetailResponse extends BaseResponse {
//        public InsurancePolicyBaseBean data;
//    }
//
//    // 获取保单列表(通过用户id与状态)
//    public static class InsurancePolicyListByUserIdAndStatusRequest extends BaseRequest {
//        public String userId;
//        public String status;
//    }
//
//    public static class InsurancePolicyListByUserIdAndStatusResponse extends BaseResponse {
//        public List<InsurancePolicyBaseBean> data;
//    }
//
//    // 获取保单列表(通过用户ID，保单类型，时间，状态，渠道，来源，关键字)
//    public static class InsurancePolicyListByOtherInfoRequest extends BaseRequest {
//        public String userId;
//        // 保单类型
//        public String type;
//        // 时间
//        public String startTime;
//        public String endTime;
//        public String status;
//        // 来源
//        public String warrantyFrom;
//        // 渠道id
//        public String ditchId;
//        // 关键字
//        public String searchKey;
//    }
//
//    public static class InsurancePolicyListByOtherInfoResponse extends BaseResponse {
//        public List<InsurancePolicyBaseBean> data;
//    }
//
//    public static class InsurancePolicyInsureForPersonRequest extends BaseRequest {
//
//        // '客户ID'
//        public String userId;
//
//        // '产品ID'
//        public String productId;
//
//        // '起保时间'
//        public String startTime;
//
//        // '结束时间'
//        public String endTime;
//
//        // '购买份数'
//        public String count;
//
//        // 投保人
//        public PersonInfo policyholder;
//
//        // 被保险人
//        public PersonInfo insured;
//
//        // 受益人
//        public PersonInfo beneficiary;
//
//    }
//
//    public static class InsurancePolicyInsureForPersonResponse extends BaseResponse {
//
//    }
//
//    public static class InsurancePolicyInsureForCompanyRequest extends BaseRequest {
//
//        // '客户ID'
//        public String userId;
//
//        // '产品ID'
//        public String productId;
//
//        // '起保时间'
//        public String startTime;
//
//        // '结束时间'
//        public String endTime;
//
//        // '购买份数'
//        public String count;
//
//        // 投保人
//        public PersonInfo policyholder;
//
//        // 被保险人
//        public PersonInfo insured;
//
//        // 受益人
//        public PersonInfo beneficiary;
//
//    }
//
//    public static class InsurancePolicyInsureForCompanyResponse extends BaseResponse {
//
//    }
//
//    public static class InsurancePolicySurrenderRequest extends BaseRequest {
//
//        // '客户ID'
//        public String userId;
//
//        // '产品ID'
//        public String productId;
//
//        public String privateCode;
//
//        public String surrenderReason;
//    }
//
//    public static class InsurancePolicySurrenderResponse extends BaseResponse {
//
//    }
//
//    public static class InsuranceClaimsListByUserIdRequest extends BaseRequest {
//
//        // '客户ID'
//        public String userId;
//
//        // 关键字
//        public String searchKey;
//
//        // 状态，0-全部
//        public String status;
//    }
//
//    public static class InsuranceClaimsListByUserIdResponse extends BaseResponse {
//
//    }
//
//    public static class InsurancePolicyBaseBean {
//        // id,分页用
//        public long id;
//
//        // '保单号'
//        public String warrantyCode;
//
//        // '内部保单唯一标识'
//        public String privateCode;
//
//        // '保单状态（ 1待核保，2核保失败，3未支付-核保成功，4支付中,5支付失败,6支付成功，7保障中,8待生效,9待续保，10已失效，11已退保）'
//        public int status;
//        public String statusText;
//
//        // '客户类型 1个人 2企业'
//        public int userType;
//
//        // '客户ID'
//        public String userId;
//
//        // '产品ID'
//        public String productId;
//
//        // '起保时间'
//        public String startTime;
//
//        // '结束时间'
//        public String endTime;
//
//        public String timeText;
//
//        // '创建时间'
//        public String createdTime;
//
//        // '更新时间'
//        public String updatedTime;
//
//        // '保单价格'
//        public String premium;
//
//        // '购买份数'
//        public String count;
//
//    }
//
//    public static class InsurancePolicyDetailForPersonBean extends InsurancePolicyBaseBean {
//
//        // 投保人
//        public List<PersonInfo> policyholders;
//
//        // 被保险人
//        public List<PersonInfo> insureds;
//
//    }
//
//    public static class InsurancePolicyDetailForCompanyBean extends InsurancePolicyBaseBean {
//        // TODO: 2018/3/22 通过RPC获取企业信息
//    }
//
//    public static class PersonInfo {
//
//        // '人员类型: 1投保人 2被保人 3受益人',
//        public int type;
//
//        // '证件类型（1为身份证，2为护照，3为军官证）'
//        public int cardType;
//        public String cardTypeText;
//
//        // '证件号'
//        public String cardCode;
//
//        // '被保人 投保人的（关系）'
//        public String relationName;
//
//        // '被保人姓名'
//        public String name;
//
//        // '手机号'
//        public String phone;
//
//        // '生日'
//        public String birthday;
//
//        // '性别 1 男 2 女 '
//        public String sex;
//        public String sexText;
//
//        // '邮箱'
//        public String email;
//
//        // '被保车牌号'
//        public String carCode;
//
//        // '职业'
//        public String occupation;
//
//        // '国籍'
//        public String nationality;
//
//        // '年收入'
//        public String annualIncome;
//
//        // '身高'
//        public String height;
//
//        // '体重'
//        public String weight;
//
//        // '地区'
//        public String area;
//
//        // '详细地址'
//        public String address;
//
//        // 是否添加联系人，0-不添加，1-添加
//        public String contact;
//
//    }

}
