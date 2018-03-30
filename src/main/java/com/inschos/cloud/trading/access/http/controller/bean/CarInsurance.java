package com.inschos.cloud.trading.access.http.controller.bean;

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

//        public String row;
//        public String page;
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

}
