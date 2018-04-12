package com.inschos.cloud.trading.access.http.controller.bean;

/**
 * 创建日期：2018/3/22 on 14:03
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicy {

    public static class GetInsurancePolicyListRequest extends BaseRequest {

    }

    public static class GetInsurancePolicyListResponse extends BaseResponse {

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
