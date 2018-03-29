package com.inschos.cloud.trading.extend.car;

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
        public List<ProvinceCode> data;
    }

    public static class ProvinceCode {
        public String provinceCode;
        public String provinceName;
    }

    public static class GetCityCodeRequest extends CarInsuranceRequest {
        public String provinceCode;
    }

    public static class GetCityCodeResponse extends CarInsuranceResponse {
        public List<CityCode> data;
    }

    public static class CityCode {
        public String cityCode;
        public String cityName;
        public String cityPlate;
        public List<AreaCode> countyList;
    }

    public static class AreaCode {
        public String countyCode;
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
        public List<InsuranceInfo> data;
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
        public String engineNo;
        public String licenseNo;
        public String frameNo;
        public String firstRegisterDate;
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
        public String ciLastEffectiveDate;
        public String biLastEffectiveDate;
        public String ciStartDateFlag;
        public String biStartDateFlag;
        public String biStartTime;
        public String ciStartTime;
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
        public String brandCode;
        public String isTrans;
        public String transDate;
        public String firstRegisterDate;
        public String sourceCertificateNo;
    }

    public static class VehicleOwnerInfo {
        public String ownerName;
        public String ownerID;
        public String ownerMobile;
    }

    public static class InsuranceInfoDetail extends InsuranceInfo {
        public String flag;
    }

    public static class InsurancePolicy {
        public String refId;
        public String insurerCode;
        public String biBeginDate;
        public String biPremium;
        public String state;
        public String msg;
        public String msgCode;
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
        public List<InsurancePolicyInfo> coverageList;
    }

    public static class GetPremiumCalibrateResponse extends CarInsuranceResponse {
        public List<InsurancePolicyPremiumDetail> data;
    }

    public static class InsuranceParticipant extends VehicleOwnerInfo {
        public String insuredName;
        public String insuredID;
        public String insuredMobile;
        public String applicantName;
        public String applicantID;
        public String applicantMobile;
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
        public String biPremium;
        public String ciBeginDate;
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
        public SpAgreement spAgreement;
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

}
