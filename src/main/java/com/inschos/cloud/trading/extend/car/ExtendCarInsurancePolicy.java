package com.inschos.cloud.trading.extend.car;

import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.assist.kit.StringKit;

import java.util.Date;
import java.util.List;

/**
 * 创建日期：2018/3/29 on 15:54
 * 描述：
 * 作者：zhangyunhe
 */
public class ExtendCarInsurancePolicy {

    public static class GetProvinceCodeRequest extends CarInsuranceRequest {
        // 指定保险公司，不传代表全部
        public String insurerCode;
    }

    public static class GetProvinceCodeResponse extends CarInsuranceResponse {
        public List<ProvinceCodeDetail> data;
    }

    public static class ProvinceCode {
        public String provinceCode;
        public String provinceName;
    }

    public static class ProvinceCodeDetail extends ProvinceCode {
        public List<ExtendCarInsurancePolicy.CityCode> city;
    }

    public static class GetCityCodeRequest extends CarInsuranceRequest {
        public String provinceCode;
    }

    public static class GetCityCodeResponse extends CarInsuranceResponse {
        public List<CityCode> data;
    }

    public static class CityCode {
        // 市级代码
        public String cityCode;
        // 城市名称
        public String cityName;
        // 车牌号字段
        public String cityPlate;
        public List<AreaCode> countyList;
    }

    public static class AreaCode {
        // 地区代码
        public String countyCode;
        // 地区名称
        public String countyName;
    }

    public static class GetInsuranceCompanyRequest extends CarInsuranceRequest {
        public String provinceCode;
    }

    public static class GetInsuranceCompanyResponse extends CarInsuranceResponse {
        public List<InsuranceCompany> data;
    }

    public static class InsuranceCompany {
        public String insurerCode;
        public String insurerName;
    }

    public static class GetInsuranceInfoRequest extends CarInsuranceRequest {
        public String provinceCode;
    }

    public static class GetInsuranceInfoResponse extends CarInsuranceResponse {
        public List<InsuranceInfoDetail> data;
    }

    public static class InsuranceInfo {
        public String coverageCode;
        public String coverageName;
        public String insuredAmount;
    }

    public static class GetCarInfoRequest extends CarInsuranceRequest {
        public String licenseNo;
        public String frameNo;
    }

    public static class GetCarInfoResponse extends CarInsuranceResponse {
        public CarInfo data;
    }

    public static class CarInfo {
        public String responseNo;
        @CheckParams
        public String engineNo;
        @CheckParams
        public String licenseNo;
        @CheckParams
        public String frameNo;

        public String firstRegisterDate;
        @CheckParams
        public String firstRegisterDateValue;
    }

    public static class CorrectCarInfoRequest extends CarInsuranceRequest {
        public String licenseNo;
    }

    public static class CorrectCarInfoResponse extends CarInsuranceResponse {
        public CarInfo data;
    }

    public static class GetCarModelRequest extends CarInsuranceRequest {
        public String responseNo;
        public String licenseNo;
        public String frameNo;

        public String brandName;
        public String row;
        public String page;
    }

    public static class GetCarModelResponse extends CarInsuranceResponse {
        public List<CarModel> data;
    }

    public static class CarModel {
        public String vehicleFgwCode;
        public String vehicleFgwName;
        public String parentVehName;
        public String brandCode;
        public String brandName;
        public String engineDesc;
        public String familyName;
        public String gearboxType;
        public String remark;
        public String newCarPrice;
        public String purchasePriceTax;
        public String importFlag;
        public String purchasePrice;
        public String seat;
        public String standardName;
        public String showText;

        public String createShowText() {
            return brandName + familyName + engineDesc + parentVehName + remark;
        }
    }

    public static class GetCarModelInfoRequest extends CarInsuranceRequest {
        public String licenseNo;
    }

    public static class GetCarModelInfoResponse extends CarInsuranceResponse {
        public CarModelInfo data;
    }

    public static class CarModelInfo extends CarInfo {
        public List<CarModel> vehicleList;
    }

    public static class GetInsuranceStartTimeRequest extends CarInsuranceRequest {
        public String responseNo;
        public String licenseNo;
        public String frameNo;
        public String brandCode;
        public String engineNo;
        public String isTrans;
        public String transDate;
        public String cityCode;
        public String ownerName;
        public String ownerMobile;
        public String ownerID;
        public String firstRegisterDate;
    }

    public static class GetInsuranceStartTimeResponse extends CarInsuranceResponse {
        public InsuranceStartTime data;
    }

    public static class InsuranceStartTime {
        //        public String ciLastEffectiveDate;
//        public String biLastEffectiveDate;
        public String ciStartDateFlag;
        public String biStartDateFlag;
        public String biStartTime;
        public String biStartTimeValue;
        public String ciStartTime;
        public String ciStartTimeValue;
    }


    public static class GetPremiumRequest extends CarInsuranceRequest {
        public String cityCode;
        public String insurerCode;
        public String responseNo;
        public CarInfoDetail carInfo;
        public VehicleOwnerInfo personInfo;
        public List<InsuranceInfoDetail> coverageList;
    }

    public static class GetPremiumResponse extends CarInsuranceResponse {
        public List<InsurancePolicy> data;
    }

    public static class CarInfoDetail extends CarInfo {
        @CheckParams
        public String brandCode;
        @CheckParams
        public String isTrans;
        public String vehicleFgwCode;
        public String vehicleFgwName;
        public String parentVehName;
        public String brandName;
        public String engineDesc;
        @CheckParams(isNecessity = false, stringType = CheckParams.StringType.NUMBER)
        public String newCarPrice;
        @CheckParams(isNecessity = false, stringType = CheckParams.StringType.NUMBER)
        public String purchasePriceTax;
        public String importFlag;
        public String seat;
        public String standardName;
        public String remark;
        public String familyName;
        public String gearboxType;
        @CheckParams(isNecessity = false, stringType = CheckParams.StringType.NUMBER)
        public String purchasePrice;

        public String transDate;

        public String transDateValue;

        public String sourceCertificateNo;

        public CarInfoDetail() {

        }

    }

    public static class VehicleOwnerInfo {
        @CheckParams
        public String ownerName;
        @CheckParams
        public String ownerID;
        @CheckParams
        public String ownerMobile;
        @CheckParams(isNecessity = false, stringType = CheckParams.StringType.NUMBER)
        public String ownerIdType;
        @CheckParams(isNecessity = false, stringType = CheckParams.StringType.NUMBER)
        public String ownerBirthday;
        @CheckParams(isNecessity = false, stringType = CheckParams.StringType.NUMBER)
        public String ownerSex;

        /**
         * 获取年龄
         *
         * @param birth 生日的时间戳
         * @return 年龄
         */
        public String getAge(String birth) {
            if (StringKit.isInteger(birth)) {
                return getAge(new Date(Long.valueOf(birth)));
            } else {
                return "";
            }
        }

        /**
         * 获取年龄
         *
         * @param date 生日
         * @return 年龄
         */
        public String getAge(Date date) {
            Date current = new Date(System.currentTimeMillis());
            int year = current.getYear() - date.getYear();
            if (current.getMonth() < date.getMonth()) {
                year -= 1;
            } else if (current.getMonth() == date.getMonth()) {
                if (current.getDay() < date.getDay()) {
                    year -= 1;
                }
            }
            return String.valueOf(year);
        }
    }

    public static class InsuranceInfoDetail extends InsuranceInfo {
        public String flag;
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

        public String totalPremium;
        public String totalPremiumText;

        public List<InsurancePolicyInfo> coverageList;
    }

    public static class InsurancePolicyInfo extends InsuranceInfo {
        public String insuredPremium;
        public String flag;
        public String amount;
    }

    public static class GetPremiumCalibrateRequest extends CarInsuranceRequest {
        public String refId;
        public String thpBizID;
        public String cityCode;
        public String biBeginDate;
        public String ciBeginDate;
        public String agentMobile;
        public String insurerCode;
        public String remittingTax;
        public String invoiceType;
        public String payType;
        public String responseNo;
        public CarInfoDetail carInfo;
        public InsuranceParticipant personInfo;
        public List<InsuranceInfoDetail> coverageList;
    }

    public static class GetPremiumCalibrateResponse extends CarInsuranceResponse {
        public List<InsurancePolicyPremiumDetail> data;
    }

    public static class InsuranceParticipant extends VehicleOwnerInfo {
        @CheckParams
        public String insuredName;
        @CheckParams
        public String insuredID;
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String insuredIdType;
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String insuredBirthday;
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String insuredSex;
        @CheckParams
        public String insuredMobile;
        @CheckParams
        public String applicantName;
        @CheckParams
        public String applicantID;
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String applicantIdType;
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String applicantBirthday;
        @CheckParams(stringType = CheckParams.StringType.NUMBER)
        public String applicantSex;
        @CheckParams
        public String applicantMobile;

        public boolean isEnable() {
            return !StringKit.isEmpty(this.ownerName) &&
                    !StringKit.isEmpty(this.ownerID) &&
                    !StringKit.isEmpty(this.ownerMobile) &&
                    !StringKit.isEmpty(this.insuredName) &&
                    !StringKit.isEmpty(this.insuredID) &&
                    !StringKit.isEmpty(this.insuredMobile) &&
                    !StringKit.isEmpty(this.applicantName) &&
                    !StringKit.isEmpty(this.applicantID) &&
                    !StringKit.isEmpty(this.applicantMobile);
        }
    }

    public static class InsurancePolicyPremiumDetail {
        public String state;
        public String msg;
        public String msgCode;
        public String bizID;
        public String thpBizID;
        public String insurerCode;
        public String channelCode;
        public String biBeginDate;
        public String biBeginDateValue;
        public String biPremium;
        public String ciBeginDate;
        public String ciBeginDateValue;
        public String ciPremium;
        public String carshipTax;
        public String integral;
        public String cIntegral;
        public String bIntegral;
        public String showCiCost;
        public String showBiCost;
        public String showSumIntegral;
        public String bjCodeFlag;
        public DiscountInfo discountInfo;
        public List<SpAgreement> spAgreement;
        public VehicleInfo vehicleInfo;
        public List<InsurancePolicyInfo> coverageList;
    }

    public static class DiscountInfo {
        public String trafficViolationFactor;
        public String selfUWFactor;
        public String selfChannelsFactor;
        public String biNcdFactor;
        public String biDiscount;
        public String ciDiscount;
    }

    public static class SpAgreement {
        public String spaCode;
        public String spaName;
        public String spaContent;
        public String riskCode;
    }

    public static class VehicleInfo {
        public String brandCode;
        public String newCarPrice;
        public String standardName;
    }

//    public static class GetPremiumFactorRequest extends CarInsuranceRequest {
//        public String bizID;
//    }
//
//    public static class GetPremiumFactorResponse extends CarInsuranceResponse {
//        public PremiumFactor data;
//    }
//
//    public static class PremiumFactor {
//        public String centile;
//        public String ecompensationRate;
//        public String totalEcompensationRate;
//        public String ciApperNo;
//        public String biApperNo;
//        public DiscountInfo discountInfo;
//        public List<InsuranceClaims> lossInfoList;
//    }
//
//    public static class InsuranceClaims {
//        public String insurerCode;
//        public String payAmount;
//        public String endCaseTime;
//        public String lossTime;
//        public String kindCode;
//    }


    public static class ApplyUnderwritingRequest extends CarInsuranceRequest {
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
    }

    public static class ApplyUnderwritingResponse extends CarInsuranceResponse {
        public ApplyUnderwriting data;
    }

    public static class ApplyUnderwriting {
        public String biProposalNo;
        public String ciProposalNo;
        public String payLink;
        // 核保状态说明：
        // state=1 和 synchFlag=0  这个就是核保成功
        // state=1 和 synchFlag=1  这个就是核保中，需要等待几分钟回写核保结果
        // state=0 这个就是核保失败
        public String synchFlag;
        public String bjCodeFlag;
        public String bizID;
        public String thpBizID;
        public String operType;
        public String uploadType;
        public String billNo;
    }

    public static class GetPayLinkRequest extends CarInsuranceRequest {
        public String bizID;
    }

    public static class GetPayLinkResponse extends CarInsuranceResponse {
        public PayLink data;
    }

    public static class PayLink {
        public String biProposalNo;
        public String ciProposalNo;
        public String payLink;
        public String bizID;
        public String expiredTime;
        public String thpBizID;
    }

    public static class VerifyPhoneCodeRequest extends CarInsuranceRequest {
        public String verificationCode;
        public String bizID;
    }

    public static class VerifyPhoneCodeResponse extends CarInsuranceResponse {
        public PhoneCode data;
    }

    public static class PhoneCode {
        public String thpBizID;
        public String bizID;
        public String biProposalNo;
        public String ciProposalNo;
        public String payLink;
        public String synchFlag;
    }

    public static class ReGetPhoneVerifyCodeRequest extends CarInsuranceRequest {
        public String bizID;
    }

    public static class ReGetPhoneVerifyCodeResponse extends CarInsuranceResponse {
    }


    /**
     * 回调接口
     */
    public static class GetApplyUnderwritingResultRequest extends CallBackCarInsuranceRequest<UnderwritingInfo> {
        // public UnderwritingInfo data;
    }

    /**
     * 回调接口
     */
    public static class GetApplyUnderwritingResultResponse extends CallBackCarInsuranceResponse {
    }

    /**
     * 回调接口
     */
    public static class UnderwritingInfo {
        public String operType;
        public String thpBizID;
        public String bizID;
        public String biProposalNo;
        public String ciProposalNo;
        public String payLink;
        public String expiredTime;
        public String uploadType;
    }


    /**
     * 回调接口
     */
    public static class GetInsurancePolicyRequest extends CallBackCarInsuranceRequest<InsurancePolicyByCallback> {
        // public InsurancePolicyByCallback data;
    }

    /**
     * 回调接口
     */
    public static class GetInsurancePolicyResponse extends CallBackCarInsuranceResponse {
    }

    /**
     * 回调接口
     */
    public static class InsurancePolicyByCallback {
        public String operType;
        public String thpBizID;
        public String bizID;
        public String payState;
        public String payMoney;
        public String payTime;
        public String biPolicyNo;
        public String ciPolicyNo;
    }

    /**
     * 回调接口
     */
    public static class GetExpressInfoRequest extends CallBackCarInsuranceRequest<ExpressInfoByCallback> {
        // public ExpressInfoByCallback data;
    }

    /**
     * 回调接口
     */
    public static class GetExpressInfoResponse extends CallBackCarInsuranceResponse {
    }

    public static class ExpressInfoByCallback {
        public String operType;
        public String thpBizID;
        public String bizID;
        public String addresseeName;
        public String addresseeMobile;
        public String addresseeProvince;
        public String addresseeCity;
        public String addresseeCounty;
        public String addresseeDetails;
        public String expressNo;
        public String expressCompanyName;
        // 0-自取，1-快递
        public String deliveryType;
    }


    public static class GetInsuranceInstructionRequest extends CarInsuranceRequest {
        public String insurerCodes;
    }

    public static class GetInsuranceInstructionResponse extends CarInsuranceResponse {
        public List<InsuranceInstruction> data;
    }

    public static class InsuranceInstruction {
        public String insurerCode;
        public String statementContent;
    }

    public static class ResolveIdentityCardRequest extends CarInsuranceRequest {
        public String frontCardUrl;
        public String backCardUrl;
        public String frontCardBase64;
        public String backCardBase64;
    }

    public static class ResolveIdentityCardResponse extends CarInsuranceResponse {
        public ResolveIdentityCard data;
    }

    public static class ResolveIdentityCard {
        public String name;
        public String cardNo;
        public String sex;
        public String folk;
        public String birthday;
        public String address;
        public String issueAuthority;
        public String certStartDate;
        public String certEndDate;
    }

    public static class ResolveDrivingLicenseRequest extends CarInsuranceRequest {
        public String imgJustUrl;
        public String imgJustBase64;
        public String imgBackUrl;
        public String imgBackBase64;
    }

    public static class ResolveDrivingLicenseResponse extends CarInsuranceResponse {
        public ResolveDrivingLicense data;
    }

    public static class ResolveDrivingLicense {
        public String licenseNo;
        public String fileNumber;
        public String seat;
        public String curbWeight;
        public String totalMass;
        public String overallSize;
        public String vehicleTpye;
        public String owner;
        public String address;
        public String useCharacter;
        public String brandName;
        public String frameNo;
        public String engineNo;
        public String firstRegisterDate;
        public String issueDate;
        public String showText;

        public String createShowText() {
            // return brandName + familyName + engineDesc + parentVehName + remark;
            return "";
        }
    }

}
