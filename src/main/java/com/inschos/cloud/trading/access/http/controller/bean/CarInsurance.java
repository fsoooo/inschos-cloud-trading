package com.inschos.cloud.trading.access.http.controller.bean;

import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.extend.car.CarInsuranceRequest;
import com.inschos.cloud.trading.extend.car.CarInsuranceResponse;
import com.inschos.cloud.trading.extend.car.ExtendCarInsurancePolicy;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
        // 自己用，0-不处理，1-处理一下字段名
        public String type = "1";
    }

    public static class GetProvinceCodeResponse extends BaseResponse {
        public List<ProvinceCodeDetail> data;
    }

    // 获取市级信息
    public static class GetCityCodeRequest extends BaseRequest {
        // 省级代码
        @CheckParams(hintName = "省级代码")
        public String provinceCode;
        // 自己用，0-不处理，1-处理一下字段名
        public String type = "1";
    }

    public static class GetCityCodeResponse extends BaseResponse {
        public ProvinceCodeDetail data;
    }

    public static class ProvinceCode {
        public String code;
        public String name;

        public ProvinceCode() {

        }

        public ProvinceCode(ExtendCarInsurancePolicy.ProvinceCode provinceCode) {
            if (provinceCode == null) {
                return;
            }

            this.code = provinceCode.provinceCode;
            this.name = provinceCode.provinceName;
        }
    }

    public static class ProvinceCodeDetail extends ProvinceCode {
        public List<CityCode> children;

        public ProvinceCodeDetail() {

        }

        public ProvinceCodeDetail(ExtendCarInsurancePolicy.ProvinceCodeDetail provinceCodeDetail) {
            if (provinceCodeDetail == null) {
                return;
            }
            this.code = provinceCodeDetail.provinceCode;
            this.name = provinceCodeDetail.provinceName;
            this.children = new ArrayList<>();

            if (provinceCodeDetail.city != null && !provinceCodeDetail.city.isEmpty()) {
                for (ExtendCarInsurancePolicy.CityCode cityCode : provinceCodeDetail.city) {
                    this.children.add(new CityCode(cityCode));
                }
            }
        }
    }

    public static class CityCode {
        // 市级代码
        public String code;
        // 城市名称
        public String name;
        // 车牌号字段
        public String childrenPlate;
        public List<AreaCode> children;

        public CityCode() {

        }

        public CityCode(ExtendCarInsurancePolicy.CityCode cityCode) {
            if (cityCode == null) {
                return;
            }
            this.code = cityCode.cityCode;
            this.name = cityCode.cityName;
            this.childrenPlate = cityCode.cityPlate;
            this.children = new ArrayList<>();

            if (cityCode.countyList != null && !cityCode.countyList.isEmpty()) {
                for (ExtendCarInsurancePolicy.AreaCode areaCode : cityCode.countyList) {
                    this.children.add(new AreaCode(areaCode));
                }
            }
        }
    }

    public static class AreaCode {
        // 地区代码
        public String code;
        // 地区名称
        public String name;

        public AreaCode() {

        }

        public AreaCode(ExtendCarInsurancePolicy.AreaCode areaCode) {
            if (areaCode == null) {
                return;
            }
            this.code = areaCode.countyCode;
            this.name = areaCode.countyName;
        }
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
        @CheckParams(hintName = "车辆信息")
        public ExtendCarInsurancePolicy.CarInfo carInfo;
        // @CheckParams(hintName = "签名")
        public String signToken;
        // 0-不是，1-是
        @CheckParams(hintName = "是否为过户")
        public String isTrans;
        // 0-不是，1-是
        @CheckParams(hintName = "是否存在车牌")
        public String notLicenseNo;

        public String brandName;
    }

    public static class SearchCarModelRequest extends BaseRequest {
        @CheckParams(hintName = "品牌名称")
        public String brandName;
    }

    public static class GetCarModelResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.CarModel> data;
    }

    public static class GetCarModelInfoRequest extends BaseRequest {
        @CheckParams(hintName = "车牌号")
        public String licenseNo;
    }

    public static class GetCarModelInfoResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.CarModelInfo data;
        public String signToken;
    }

    public static class GetInsuranceCompanyRequest extends BaseRequest {
        @CheckParams(hintName = "省级代码")
        public String provinceCode;
    }

    public static class GetInsuranceCompanyResponse extends BaseResponse {
        public List<ExtendCarInsurancePolicy.InsuranceCompany> data;
    }

    public static class GetInsuranceStartTimeRequest extends BaseRequest {
        @CheckParams(hintName = "车辆信息")
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;
        @CheckParams(hintName = "市级代码")
        public String cityCode;
        // @CheckParams(hintName = "签名")
        public String signToken;
        @CheckParams(hintName = "是否存在车牌")
        public String notLicenseNo;

        // 非必传
        public ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo;
    }

    public static class GetInsuranceStartTimeResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.InsuranceStartTime data;
    }

    public static class GetPremiumRequest extends BaseRequest {
        @CheckParams(hintName = "市级代码")
        public String cityCode;
        @CheckParams(hintName = "险种代码")
        public String insurerCode;
        // @CheckParams(hintName = "签名")
        public String signToken;
        @CheckParams(hintName = "是否存在车牌")
        public String notLicenseNo;
        @CheckParams(hintName = "车辆信息")
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;
        @CheckParams(hintName = "险别列表")
        public List<InsuranceInfo> coverageList;
        @CheckParams(hintName = "人员信息")
        public ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo;
    }

    public static class GetPremiumResponse extends BaseResponse {
        public GetPremiumDetail data;
    }

    public static class GetPremiumDetail {
        public String totalInsuredPremium;
        public String totalInsuredPremiumText;
        public List<InsurancePolicy> insurancePolicies;
    }

    public static class InsurancePolicy {
        public String refId;
        public String insurerCode;
        public String biBeginDate;
        public String biPremium;
        public String integral;
        public String ciBeginDate;
        public String ciPremium;
        public String carshipTax;
        public String carshipTaxText;
        public String isChanged;
        public String productName;
        public String companyLogo;

        public String totalPremium;
        public String totalPremiumText;

        public List<InsuranceInfo> coverageList;

        public InsurancePolicy() {

        }

        public InsurancePolicy(ExtendCarInsurancePolicy.InsurancePolicy insurancePolicy) {
            this.refId = insurancePolicy.refId;
            this.insurerCode = insurancePolicy.insurerCode;
            this.biBeginDate = insurancePolicy.biBeginDate;
            this.biPremium = insurancePolicy.biPremium;
            this.integral = insurancePolicy.integral;
            this.ciBeginDate = insurancePolicy.ciBeginDate;
            this.ciPremium = insurancePolicy.ciPremium;
            this.carshipTax = insurancePolicy.carshipTax;
            this.carshipTaxText = insurancePolicy.carshipTaxText;
            this.totalPremium = insurancePolicy.totalPremium;
            this.totalPremiumText = insurancePolicy.totalPremiumText;
        }
    }

    public static class GetInsuranceInfoRequest extends BaseRequest {
    }

    public static class GetInsuranceInfoResponse extends BaseResponse {
        public List<InsuranceInfo> data;
    }

    public static class InsuranceInfo extends ExtendCarInsurancePolicy.InsurancePolicyInfo {
        public String hasExcessOption;
        @CheckParams(hintName = "是否不计免赔")
        public String isExcessOption;
        public List<String> insuredAmountList;
        public String insuredAmountText;

        // 玻璃破碎险 1-国产，2-进口
        public String source;
        public List<String> sourceOption;

        // 修理期间费用补偿险
        @CheckParams(stringType = CheckParams.StringType.NUMBER, isNecessity = false, hintName = "修理期间费用补偿险天数")
        public String day;

        @CheckParams(stringType = CheckParams.StringType.NUMBER, isNecessity = false, hintName = "修理期间费用补偿险保额")
        public String amount;
        public String maxDay;
        public String minDay;
        public String maxAmount;
        public String minAmount;
        public String insuredPremiumText;

    }

    public static class GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoRequest extends BaseRequest {
        @CheckParams(hintName = "市级代码")
        public String cityCode;
        @CheckParams(hintName = "省级代码")
        public String provinceCode;
        // @CheckParams(hintName = "签名")
        public String signToken;
        @CheckParams(hintName = "是否存在车牌")
        public String notLicenseNo;
        @CheckParams(hintName = "车辆信息")
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;

    }

    public static class GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoResponse extends BaseResponse {
        public InsuranceCompanyAndInsuranceStartTimeAndInsuranceInfo data;
    }

    public static class InsuranceCompanyAndInsuranceStartTimeAndInsuranceInfo {
        // 保险公司信息
        public List<ExtendCarInsurancePolicy.InsuranceCompany> insuranceCompanies;
        // 起保时间信息
        public ExtendCarInsurancePolicy.InsuranceStartTime startTimeInfo;
        // 险别列表
        public List<InsuranceInfo> insuranceInfo;
    }

    public static class GetInsuranceCompanyAndInsuranceStartTimeAndPremiumRequest extends BaseRequest {
        @CheckParams(hintName = "市级代码")
        public String cityCode;
        @CheckParams(hintName = "省级代码")
        public String provinceCode;
        // @CheckParams(hintName = "签名")
        public String signToken;
        @CheckParams(hintName = "是否存在车牌")
        public String notLicenseNo;
        @CheckParams(hintName = "车辆信息")
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;

        // 非必传
        public ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo;

        // 接口自动赋值
        public String insurerCode;
        public List<ExtendCarInsurancePolicy.InsuranceInfoDetail> coverageList;
    }

    public static class GetInsuranceCompanyAndInsuranceStartTimeAndPremiumResponse extends BaseResponse {
        public InsuranceCompanyAndInsuranceStartTimeAndPremium data;
    }

    public static class InsuranceCompanyAndInsuranceStartTimeAndPremium {
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

        @CheckParams(hintName = "签名")
        public String signToken;
        public String thpBizID;
        @CheckParams(hintName = "市级代码")
        public String cityCode;
        public String biBeginDate;
        public String ciBeginDate;
        @CheckParams(isNecessity = false, hintName = "商业险承保时间")
        public String biBeginDateValue;
        @CheckParams(isNecessity = false, hintName = "交强险承保时间")
        public String ciBeginDateValue;
        @CheckParams(hintName = "险种代码")
        public String insurerCode;
        @CheckParams(hintName = "响应码")
        public String responseNo;
        @CheckParams(hintName = "是否存在车牌")
        public String notLicenseNo;
        @CheckParams(hintName = "车辆信息")
        public ExtendCarInsurancePolicy.CarInfoDetail carInfo;
        @CheckParams(hintName = "人员信息")
        public ExtendCarInsurancePolicy.InsuranceParticipant personInfo;
        @CheckParams(hintName = "险别列表")
        public List<InsuranceInfo> coverageList;

    }

    public static class GetPremiumCalibrateResponse extends BaseResponse {
        public GetPremiumCalibrateDetail data;
    }

    public static class GetPremiumCalibrateDetail {
        public String totalInsuredPremium;
        public String totalInsuredPremiumText;
        public String ciInsuredPremium;
        public String ciInsuredPremiumText;
        public String biInsuredPremium;
        public String biInsuredPremiumText;
        public String productName;
        public String insuredName;
        public String ciInsuranceTermText;
        public String biInsuranceTermText;
        public String insuranceContent;
        public List<InsurancePolicyPremiumDetail> insurancePolicyPremiumDetails;
    }

    public static class InsurancePolicyPremiumDetail {
        public String state;
        public String msg;
        public String msgCode;
        public String bizID;
        public String thpBizID;
        public String productName;
        public String companyLogo;
        public String insurerCode;
        public String channelCode;
        public String biBeginDate;
        public String biBeginDateValue;
        public String biInsuranceTermText;
        public String biPremium;
        public String ciBeginDate;
        public String ciBeginDateValue;
        public String ciInsuranceTermText;
        public String ciPremium;
        public String carshipTax;
        public String carshipTaxText;
        public String integral;
        public String cIntegral;
        public String bIntegral;
        public String showCiCost;
        public String showBiCost;
        public String showSumIntegral;
        public String bjCodeFlag;
        public String isChanged;
        public boolean hasCommercialInsurance = false;
        public boolean hasCompulsoryInsurance = false;
        public ExtendCarInsurancePolicy.DiscountInfo discountInfo;
        public List<ExtendCarInsurancePolicy.SpAgreement> spAgreement;
        public ExtendCarInsurancePolicy.VehicleInfo vehicleInfo;
        public List<InsuranceInfo> coverageList;

        public InsurancePolicyPremiumDetail() {

        }

        public InsurancePolicyPremiumDetail(ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail insurancePolicy) {
            this.state = insurancePolicy.state;
            this.msg = insurancePolicy.msg;
            this.msgCode = insurancePolicy.msgCode;
            this.bizID = insurancePolicy.bizID;
            this.thpBizID = insurancePolicy.thpBizID;
            this.productName = insurancePolicy.productName;
            this.insurerCode = insurancePolicy.insurerCode;
            this.channelCode = insurancePolicy.channelCode;
            this.biBeginDate = insurancePolicy.biBeginDate;
            this.biBeginDateValue = insurancePolicy.biBeginDateValue;
            this.biInsuranceTermText = insurancePolicy.biInsuranceTermText;
            this.biPremium = insurancePolicy.biPremium;
            this.ciBeginDate = insurancePolicy.ciBeginDate;
            this.ciBeginDateValue = insurancePolicy.ciBeginDateValue;
            this.ciInsuranceTermText = insurancePolicy.ciInsuranceTermText;
            this.ciPremium = insurancePolicy.ciPremium;
            this.carshipTax = insurancePolicy.carshipTax;
            this.carshipTaxText = insurancePolicy.carshipTaxText;
            this.integral = insurancePolicy.integral;
            this.cIntegral = insurancePolicy.cIntegral;
            this.bIntegral = insurancePolicy.bIntegral;
            this.showCiCost = insurancePolicy.showCiCost;
            this.showBiCost = insurancePolicy.showBiCost;
            this.bjCodeFlag = insurancePolicy.bjCodeFlag;
            this.hasCompulsoryInsurance = insurancePolicy.hasCompulsoryInsurance;
            this.hasCommercialInsurance = insurancePolicy.hasCommercialInsurance;
            this.discountInfo = insurancePolicy.discountInfo;
            this.spAgreement = insurancePolicy.spAgreement;
            this.vehicleInfo = insurancePolicy.vehicleInfo;
        }
    }

//    public static class GetPremiumFactorRequest extends BaseRequest {
//        public String bizID;
//    }
//
//    public static class GetPremiumFactorResponse extends BaseResponse {
//        public ExtendCarInsurancePolicy.PremiumFactor data;
//    }


    public static class ApplyUnderwritingRequest extends BaseRequest {
        public String channelCode;
        @CheckParams(hintName = "险种代码")
        public String insurerCode;
        public String productId;
        public String bizID;
        @CheckParams(hintName = "收件人名称")
        public String addresseeName;
        @CheckParams(hintName = "收件人电话")
        public String addresseeMobile;
        @CheckParams(hintName = "收件人详细地址")
        public String addresseeDetails;
        @CheckParams(hintName = "保单寄送电子邮箱")
        public String policyEmail;
        @CheckParams(hintName = "保单寄送地区代码")
        public String addresseeCounty;
        @CheckParams(hintName = "保单寄送市级代码")
        public String addresseeCity;
        @CheckParams(hintName = "保单寄送省级代码")
        public String addresseeProvince;
        public String carshipTax;
        public String ciInsuredPremium;
        public String biInsuredPremium;
        public String ciCarShipTax;
        public String biCarShipTax;
        public String ciBeginDateValue;
        public String biBeginDateValue;

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
        public String integral;
        public String cIntegral;
        public String bIntegral;

        public boolean hasCommercialInsurance = false;
        public boolean hasCompulsoryInsurance = false;

        public List<InsuranceInfo> coverageList;
        public List<ExtendCarInsurancePolicy.SpAgreement> spAgreements;

        public boolean isNeedVerificationCode() {
            return StringKit.equals("1", bjCodeFlag);
        }
    }

    public static class ApplyUnderwritingResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.ApplyUnderwriting data;
    }

    public static class PremiumCalibrateAndApplyUnderwritingRequest extends BaseRequest {
        @CheckParams(hintName = "精准报价信息")
        public GetPremiumCalibrateRequest premiumCalibrate;
        @CheckParams(hintName = "核保信息")
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
        public List<InsurancePolicyPremiumDetail> insurancePolicyPremiumDetails;
    }

    public static class GetPayLinkRequest extends BaseRequest {
        @CheckParams(hintName = "流水号")
        public String bizID;
    }

    public static class GetPayLinkResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.PayLink data;
    }

    public static class VerifyPhoneCodeRequest extends BaseRequest {
        public String verificationCode;
        public String bizID;
        public String bjCodeFlag;
    }

    public static class VerifyPhoneCodeResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.PhoneCode data;
    }

    public static class ReGetPhoneVerifyCodeRequest extends BaseRequest {
        public String bizID;
    }

    public static class ReGetPhoneVerifyCodeResponse extends BaseResponse {
    }

    public static class ResolveIdentityCardRequest extends BaseRequest {
        public String frontCardUrl;
        public String backCardUrl;
        public String frontCardBase64;
        public String backCardBase64;
    }

    public static class ResolveIdentityCardResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.ResolveIdentityCard data;
    }

    public static class ResolveDrivingLicenseRequest extends BaseRequest {
        public String imgJustUrl;
        public String imgJustBase64;
        public String imgBackUrl;
        public String imgBackBase64;
    }

    public static class ResolveDrivingLicenseResponse extends BaseResponse {
        public ExtendCarInsurancePolicy.ResolveDrivingLicense data;
    }

}
