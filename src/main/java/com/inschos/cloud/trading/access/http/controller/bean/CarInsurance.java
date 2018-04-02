package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.extend.car.ExtendCarInsurancePolicy;

import java.util.List;

/**
 * 创建日期：2018/3/30 on 11:34
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInsurance {

    // 获取省级信息
    public static class GetProvinceCodeRequest extends BaseRequest {
        // 指定保险公司，不传代表全部
        public String insurerCode;
    }

    public static class GetProvinceCodeResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.ProvinceCodeDetail> data;
    }

    // 获取市级信息
    public static class GetCityCodeRequest extends BaseRequest {
        // 省级代码
        public String provinceCode;
    }

    public static class GetCityCodeResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.ProvinceCodeDetail data;
    }

    /**
     * 获取车辆部分信息（车型，车架号，发动机号，注册日期）
     * <p>1、通过车牌号获取，licenseNo</p>
     * <p>2、通过车牌号获取，frameNo</p>
     */
    public static class GetCarInfoRequest extends BaseRequest {
        public String licenseNo;
        public String frameNo;
    }

    public static class GetCarInfoResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.CarInfo data;
        public String signToken;
    }

    public static class GetCarModelRequest extends BaseRequest {
        public ExtendCarInsurancePolicy.CarInfo carInfo;
        public String signToken;
        // 0-不是，1-是
        public String isNew;

        public String brandName;
    }

    public static class GetCarModelResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.CarModel> data;
    }

    public static class GetCarModelInfoRequest extends BaseRequest {
        public String licenseNo;
    }

    public static class GetCarModelInfoResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.CarModelInfo data;
        public String signToken;
    }

    public static class GetInsuranceCompanyRequest extends BaseRequest {
        public String provinceCode;
    }

    public static class GetInsuranceCompanyResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.InsuranceCompany> data;
    }

    public static class GetInsuranceStartTimeRequest extends BaseRequest {
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;
        public String cityCode;
        public String signToken;

        // 非必传
        public ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo;
    }

    public static class GetInsuranceStartTimeResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.InsuranceStartTime data;
    }

    public static class GetPremiumRequest extends BaseRequest {
        public String cityCode;
        public String insurerCode;
        public String signToken;
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;

        // 非必传
        public ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo;
        public List<ExtendCarInsurancePolicy.InsuranceInfoDetail> coverageList;
    }

    public static class GetPremiumResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.InsurancePolicy> data;
    }

    public static class GetInsuranceInfoRequest extends BaseRequest {
    }

    public static class GetInsuranceInfoResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.InsuranceInfo> data;
    }

    public static class GetInsuranceCompanyAndInsuranceStartTimeAndPremiumRequest extends BaseRequest {
        public String cityCode;
        public String provinceCode;
        public String signToken;
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;

        // 非必传
        public ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo;

        // 接口自动赋值
        public String insurerCode;
        public List<ExtendCarInsurancePolicy.InsuranceInfoDetail> coverageList;
    }

    public static class GetInsuranceCompanyAndInsuranceStartTimeAndPremiumResponse extends BaseResponse {
        public InsuranceCompanyAndInsuranceStartTimeAndPremiumResponse data;
    }

    public static class InsuranceCompanyAndInsuranceStartTimeAndPremiumResponse {
        // 保险公司信息
        public List<ExtendCarInsurancePolicy.InsuranceCompany> insuranceCompanies;
        // 起保时间信息
        public ExtendCarInsurancePolicy.InsuranceStartTime startTimeInfo;
        // 参考报价信息
        public List<ExtendCarInsurancePolicy.InsurancePolicy> premiumInfo;
    }


    public static class GetPremiumCalibrateRequest extends BaseRequest {
        // 非必传
        public String refId;
        // 非必传，代理人手机号（如果在平台申请的代理人模式，这个必传）
        public String agentMobile;
        // 非必传，1-微信（移动端） 2-非微信（移动端） 3-PC端 4-POS机刷卡支付（只支持安盛天平） （人太平只支持微信、POS刷卡）
        public String payType;
        // 非必传，0或null纸质发票，1-电子发票，2-无需打印发票
        public String invoiceType;
        // 非必传，是否代缴车船税 1-不代缴，null-代缴，目前只支持人保、太保、平安
        public String remittingTax;

        public String signToken;
        public String thpBizID;
        public String cityCode;
        public String biBeginDate;
        public String ciBeginDate;
        public String insurerCode;
        public String responseNo;
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;
        public ExtendCarInsurancePolicy.InsuranceParticipant personInfo;
        public List<ExtendCarInsurancePolicy.InsurancePolicyInfo> coverageList;
    }

    public static class GetPremiumCalibrateResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail> data;
    }

    public static class GetPremiumFactorRequest extends BaseRequest {
        public String bizID;
    }

    public static class GetPremiumFactorResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.PremiumFactor data;
    }


    public static class ApplyUnderwritingRequest extends BaseRequest {
        public String channelCode;
        public String insurerCode;
        public String bizID;
        public String addresseeName;
        public String addresseeMobile;
        public String addresseeDetails;
        public String policyEmail;
        public String addresseeCounty;
        public String addresseeCity;
        public String addresseeProvince;

        // 非必传，支付成功后跳转地址
        public String applicantUrl;
        // 非必传，支付方式
        public String payType;
        // 非必传，推荐人手机号
        public String refereeMobile;
        // 北京验证码
        public String verificationCode;
        // 必传
        public String bjCodeFlag;

        public boolean isNeedVerificationCode() {
            return StringKit.equals("1", bjCodeFlag);
        }
    }

    public static class ApplyUnderwritingResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.ApplyUnderwriting data;
    }

    public static class PremiumCalibrateAndApplyUnderwritingRequest extends BaseRequest {

        public GetPremiumCalibrateRequest premiumCalibrate;
        public ApplyUnderwritingRequest applyUnderwriting;


//        // 非必传
//        public String refId;
//        // 非必传，代理人手机号（如果在平台申请的代理人模式，这个必传）
//        public String agentMobile;
//        // 非必传，1-微信（移动端） 2-非微信（移动端） 3-PC端 4-POS机刷卡支付（只支持安盛天平） （人太平只支持微信、POS刷卡）
//        public String payType;
//        // 非必传，0或null纸质发票，1-电子发票，2-无需打印发票
//        public String invoiceType;
//        // 非必传，是否代缴车船税 1-不代缴，null-代缴，目前只支持人保、太保、平安
//        public String remittingTax;
//
//        public String signToken;
//        public String thpBizID;
//        public String cityCode;
//        public String biBeginDate;
//        public String ciBeginDate;
//        public String insurerCode;
//        public String responseNo;
//        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;
//        public ExtendCarInsurancePolicy.InsuranceParticipant personInfo;
//        public List<ExtendCarInsurancePolicy.InsurancePolicyInfo> coverageList;
//
//        public String channelCode;
//        public String bizID;
//        public String addresseeName;
//        public String addresseeMobile;
//        public String addresseeDetails;
//        public String policyEmail;
//        public String addresseeCounty;
//        public String addresseeCity;
//        public String addresseeProvince;
//
//        // 非必传，支付成功后跳转地址
//        public String applicantUrl;
//        // 非必传，推荐人手机号
//        public String refereeMobile;
//        // 北京验证码
//        public String verificationCode;
//        // 必传
//        public String bjCodeFlag;
//
//        public boolean isNeedVerificationCode() {
//            return StringKit.equals("1", bjCodeFlag);
//        }
    }

    public static class PremiumCalibrateAndApplyUnderwritingResponse extends BaseResponse {
        public PremiumCalibrateAndApplyUnderwriting data;
    }

    public static class PremiumCalibrateAndApplyUnderwriting {
        public ExtendCarInsurancePolicy.ApplyUnderwriting applyUnderwriting;
        public List<ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail> insurancePolicyPremiumDetails;
    }

    public static class GetPayLinkRequest extends BaseRequest {
        public String bizID;
    }

    public static class GetPayLinkResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.PayLink data;
    }

    public static class VerifyPhoneCodeRequest extends BaseRequest {
        public String verificationCode;
        public String bizID;
    }

    public static class VerifyPhoneCodeResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.PhoneCode data;
    }

    public static class ReGetPhoneVerifyCodeRequest extends BaseRequest {
        public String bizID;
    }

    public static class ReGetPhoneVerifyCodeResponse extends BaseResponse {
    }
}
