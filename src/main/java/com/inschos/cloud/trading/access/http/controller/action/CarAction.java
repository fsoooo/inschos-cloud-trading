package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.CarInsurance;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.extend.car.*;

/**
 * 创建日期：2018/3/29 on 14:27
 * 描述：
 * 作者：zhangyunhe
 */
public class CarAction extends BaseAction {

    /**
     * 查询省代码
     */
    private static final String get_province_code = CarInsuranceCommon.getServerHost() + "/mdata/provinces";

    // FINISH: 2018/3/30
    public String getProvinceCode(ActionBean actionBean) {
        CarInsurance.GetProvinceCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetProvinceCodeRequest.class);
        CarInsurance.GetProvinceCodeResponse response = new CarInsurance.GetProvinceCodeResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetProvinceCodeRequest getProvinceCodeRequest = new ExtendCarInsurancePolicy.GetProvinceCodeRequest();

        ExtendCarInsurancePolicy.GetProvinceCodeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetProvinceCodeRequest, ExtendCarInsurancePolicy.GetProvinceCodeResponse>(get_province_code, getProvinceCodeRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                if (result.data != null && !result.data.isEmpty()) {
                    ActionBean bean = new ActionBean();

                    bean.salt = actionBean.salt;
                    bean.salt = actionBean.salt;
                    bean.buildCode = actionBean.buildCode;
                    bean.platform = actionBean.platform;
                    bean.apiCode = actionBean.apiCode;
                    bean.companyId = actionBean.companyId;
                    bean.userId = actionBean.userId;
                    bean.url = actionBean.url;

                    CarInsurance.GetCityCodeRequest getCityCodeRequest = new CarInsurance.GetCityCodeRequest();
                    getCityCodeRequest.provinceCode = result.data.get(0).provinceCode;

                    bean.body = JsonKit.bean2Json(getCityCodeRequest);

                    CarInsurance.GetCityCodeResponse getCityCodeResponse = JsonKit.json2Bean(getCityCode(bean), CarInsurance.GetCityCodeResponse.class);

                    if (getCityCodeResponse != null && getCityCodeResponse.data != null && getCityCodeResponse.code == BaseResponse.CODE_SUCCESS) {
                        response.data = result.data;
                        response.data.get(0).city = getCityCodeResponse.data.city;
                        str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);
                    } else {
                        str = json(BaseResponse.CODE_ACCESS_FAILURE, "获取省级列表失败", response);
                    }
                } else {
                    str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);
                }
            } else {
                str = json(BaseResponse.CODE_ACCESS_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", response);
        }

        return str;
    }

    /**
     * 查询城市
     */
    private static final String get_city_code = CarInsuranceCommon.getServerHost() + "/mdata/cities";

    // FINISH: 2018/3/30
    public String getCityCode(ActionBean actionBean) {
        CarInsurance.GetCityCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCityCodeRequest.class);
        CarInsurance.GetCityCodeResponse response = new CarInsurance.GetCityCodeResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetCityCodeRequest getCityCodeRequest = new ExtendCarInsurancePolicy.GetCityCodeRequest();
        getCityCodeRequest.provinceCode = request.provinceCode;

        ExtendCarInsurancePolicy.GetCityCodeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetCityCodeRequest, ExtendCarInsurancePolicy.GetCityCodeResponse>(get_city_code, getCityCodeRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = new ExtendCarInsurancePolicy.ProvinceCodeDetail();
                response.data.provinceCode = request.provinceCode;
                response.data.city = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取市级列表成功", response);
            } else {
                str = json(BaseResponse.CODE_ACCESS_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 保险公司支持的地区
     */
    private static final String get_area_by_insurance = CarInsuranceCommon.getServerHost() + "/mdata/areas";

    public String getAreaByInsurance(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetProvinceCodeRequest getProvinceCodeRequest = new ExtendCarInsurancePolicy.GetProvinceCodeRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
        // getProvinceCodeRequest.insurerCode

        ExtendCarInsurancePolicy.GetProvinceCodeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetProvinceCodeRequest, ExtendCarInsurancePolicy.GetProvinceCodeResponse>(get_area_by_insurance, getProvinceCodeRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }
        return "";
    }

    /**
     * 地区支持的保险公司
     */
    private static final String get_insurance_by_area = CarInsuranceCommon.getServerHost() + "/mdata/insurers";

    public String getInsuranceByArea(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetInsuranceCompanyRequest getInsuranceCompanyRequest = new ExtendCarInsurancePolicy.GetInsuranceCompanyRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
        // getProvinceCodeRequest.insurerCode

        ExtendCarInsurancePolicy.GetInsuranceCompanyResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetInsuranceCompanyRequest, ExtendCarInsurancePolicy.GetInsuranceCompanyResponse>(get_insurance_by_area, getInsuranceCompanyRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }
        return "";
    }

    /**
     * 险别查询
     */
    private static final String get_insurance_info = CarInsuranceCommon.getServerHost() + "/mdata/risks";

    public String getInsuranceInfo(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetInsuranceInfoRequest getInsuranceInfoRequest = new ExtendCarInsurancePolicy.GetInsuranceInfoRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
        // getInsuranceInfoRequest.provinceCode

        ExtendCarInsurancePolicy.GetInsuranceInfoResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetInsuranceInfoRequest, ExtendCarInsurancePolicy.GetInsuranceInfoResponse>(get_insurance_info, getInsuranceInfoRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 车辆号码信息(根据车牌号查询)
     */
    private static final String get_car_info_licence_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByLicenseNo";

    // FINISH: 2018/3/30
    public String getCarInfoByLicenceNumber(ActionBean actionBean) {
        CarInsurance.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarInfoRequest.class);
        CarInsurance.GetCarInfoResponse response = new CarInsurance.GetCarInfoResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetCarInfoRequest getCarInfoRequest = new ExtendCarInsurancePolicy.GetCarInfoRequest();
        getCarInfoRequest.licenseNo = request.licenseNo;

        ExtendCarInsurancePolicy.GetCarInfoResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetCarInfoRequest, ExtendCarInsurancePolicy.GetCarInfoResponse>(get_car_info_licence_number, getCarInfoRequest).post();

        return dealResultAndResponse(response, result);
    }

    /**
     * 车辆号码信息(根据车架号查询)
     */
    private static final String get_car_info_frame_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByFrameNo";

    // FINISH: 2018/3/30
    public String getCarInfoFrameNumber(ActionBean actionBean) {

        CarInsurance.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarInfoRequest.class);
        CarInsurance.GetCarInfoResponse response = new CarInsurance.GetCarInfoResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetCarInfoRequest getCarInfoRequest = new ExtendCarInsurancePolicy.GetCarInfoRequest();
        getCarInfoRequest.frameNo = request.frameNo;

        ExtendCarInsurancePolicy.GetCarInfoResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetCarInfoRequest, ExtendCarInsurancePolicy.GetCarInfoResponse>(get_car_info_frame_number, getCarInfoRequest).post();

        return dealResultAndResponse(response, result);
    }

    /**
     * 车辆信息(结果处理方法){@link #getCarInfoByLicenceNumber} {@link #getCarInfoFrameNumber}
     *
     * @param response 给我们的接口返回的response
     * @param result   第三方接口给我们返回的response
     * @return 给我们返回的json
     */
    private String dealResultAndResponse(CarInsurance.GetCarInfoResponse response, ExtendCarInsurancePolicy.GetCarInfoResponse result) {
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                if (result.data.frameNo == null) {
                    result.data.frameNo = "null";
                }
                response.signToken = SignatureTools.sign(result.data.frameNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                str = json(BaseResponse.CODE_SUCCESS, "获取车辆号码信息成功", new BaseResponse());
            } else {
                str = json(BaseResponse.CODE_ACCESS_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }
        return str;
    }

    /**
     * 一键修正车辆信息
     * <p>一键修正接口成本很高，所以限制的调用次数很少，默认10次，可根据实际成单量调整，此接口用于车辆信息接口带出的车架号、发动机号或者初等日期不对的情况，所以要在通过车辆信息接口获取到的信息不准的情况下再调用这个接口。
     * </p>
     */
    private static final String correct_car_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoRevision";

    public String correctCarInfo(ActionBean actionBean) {
        ExtendCarInsurancePolicy.CorrectCarInfoRequest correctCarInfoRequest = new ExtendCarInsurancePolicy.CorrectCarInfoRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
        // correctCarInfoRequest.licenseNo

        ExtendCarInsurancePolicy.CorrectCarInfoResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.CorrectCarInfoRequest, ExtendCarInsurancePolicy.CorrectCarInfoResponse>(correct_car_info, correctCarInfoRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 车型信息
     */
    private static final String get_car_model = CarInsuranceCommon.getServerHost() + "/auto/modelExactness";

    public String getCarModel(ActionBean actionBean) {
        CarInsurance.GetCarModelRequest request = new CarInsurance.GetCarModelRequest();
        CarInsurance.GetCarModelResponse response = new CarInsurance.GetCarModelResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        if (request.carInfo == null) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        if (request.carInfo.frameNo == null) {
            request.carInfo.frameNo = "null";
        }

        // 是否新车
        boolean isNew = StringKit.equals(request.isNew, "1");
        // 是否有车架号
        boolean verify = SignatureTools.verify(request.carInfo.frameNo, request.signToken, SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);

        if (isNew) {
            request.carInfo.licenseNo = "新车";
        }

        // TODO: 2018/3/30 判断逻辑正在问
        if (verify) {
            if (StringKit.isEmpty(request.carInfo.frameNo) || request.carInfo.frameNo.contains("*")) {
                // 必须传responseNo
            }
        } else {
            if (StringKit.isEmpty(request.carInfo.frameNo)) {
                return json(BaseResponse.CODE_FAILURE, "请输入车架号", response);
            }
        }

        ExtendCarInsurancePolicy.GetCarModelRequest getCarModelRequest = new ExtendCarInsurancePolicy.GetCarModelRequest();

        getCarModelRequest.licenseNo = request.carInfo.licenseNo;
        getCarModelRequest.frameNo = request.carInfo.frameNo;
        getCarModelRequest.responseNo = request.carInfo.responseNo;

        ExtendCarInsurancePolicy.GetCarModelResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetCarModelRequest, ExtendCarInsurancePolicy.GetCarModelResponse>(get_car_model, getCarModelRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取车型信息成功", response);
            } else {
                str = json(BaseResponse.CODE_ACCESS_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 车辆号码与车型信息
     */
    private static final String get_car_model_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleAndModel";

    public String getCarModelInfo(ActionBean actionBean) {
        CarInsurance.GetCarModelInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarModelInfoRequest.class);
        CarInsurance.GetCarModelInfoResponse response = new CarInsurance.GetCarModelInfoResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetCarModelInfoRequest getCarModelInfoRequest = new ExtendCarInsurancePolicy.GetCarModelInfoRequest();

        getCarModelInfoRequest.licenseNo = request.licenseNo;

        ExtendCarInsurancePolicy.GetCarModelInfoResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetCarModelInfoRequest, ExtendCarInsurancePolicy.GetCarModelInfoResponse>(get_car_model_info, getCarModelInfoRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                if (result.data.frameNo == null) {
                    result.data.frameNo = "null";
                }
                response.signToken = SignatureTools.sign(result.data.frameNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                str = json(BaseResponse.CODE_SUCCESS, "获取车辆号码与车型信息成功", response);
            } else {
                str = json(BaseResponse.CODE_ACCESS_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 模糊匹配车型
     */
    private static final String get_car_model_by_key = CarInsuranceCommon.getServerHost() + "/auto/modelMistiness";

    public String getCarModelByKey(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetCarModelRequest getCarModelRequest = new ExtendCarInsurancePolicy.GetCarModelRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
        // getCarModelRequest.brandName
        // getCarModelRequest.row
        // getCarModelRequest.page

        ExtendCarInsurancePolicy.GetCarModelResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetCarModelRequest, ExtendCarInsurancePolicy.GetCarModelResponse>(get_car_model_by_key, getCarModelRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 获取投保起期
     */
    private static final String get_insurance_start_time = CarInsuranceCommon.getServerHost() + "/assist/effectiveDate";

    public String getInsuranceStartTime(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetInsuranceStartTimeRequest getInsuranceStartTimeRequest = new ExtendCarInsurancePolicy.GetInsuranceStartTimeRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        getInsuranceStartTimeRequest.responseNo
//        getInsuranceStartTimeRequest.licenseNo
//        getInsuranceStartTimeRequest.frameNo
//        getInsuranceStartTimeRequest.brandCode
//        getInsuranceStartTimeRequest.engineNo
//        getInsuranceStartTimeRequest.isTrans
//        getInsuranceStartTimeRequest.transDate
//        getInsuranceStartTimeRequest.cityCode
//        getInsuranceStartTimeRequest.ownerName
//        getInsuranceStartTimeRequest.ownerMobile
//        getInsuranceStartTimeRequest.ownerID
//        getInsuranceStartTimeRequest.firstRegisterDate

        ExtendCarInsurancePolicy.GetCarModelResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetInsuranceStartTimeRequest, ExtendCarInsurancePolicy.GetCarModelResponse>(get_insurance_start_time, getInsuranceStartTimeRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 参考报价
     */
    private static final String get_premium = CarInsuranceCommon.getServerHost() + "/main/referenceQuote";

    public String getPremium(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetPremiumRequest getPremiumRequest = new ExtendCarInsurancePolicy.GetPremiumRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        getPremiumRequest.cityCode
//        getPremiumRequest.insurerCode
//        getPremiumRequest.responseNo
//        ExtendCarInsurancePolicy.CarInfoDetail carInfo = new ExtendCarInsurancePolicy.CarInfoDetail();
//        carInfo.engineNo;
//        carInfo.licenseNo;
//        carInfo.frameNo;
//        carInfo.firstRegisterDate;
//        carInfo.brandCode;
//        carInfo.isTrans;
//        carInfo.transDate;
//        carInfo.firstRegisterDate;
//
//        ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo = new ExtendCarInsurancePolicy.VehicleOwnerInfo();
//        personInfo.ownerName
//        personInfo.ownerID
//        personInfo.ownerMobile

//        getPremiumRequest.coverageList = new ArrayList<>();

//        for () {
//            ExtendCarInsurancePolicy.InsuranceInfoDetail insuranceInfoDetail = new ExtendCarInsurancePolicy.InsuranceInfoDetail();
//            insuranceInfoDetail.coverageCode;
//            insuranceInfoDetail.coverageName;
//            insuranceInfoDetail.insuredAmount;
//            insuranceInfoDetail.flag
//            carInfo.coverageList.add(insuranceInfoDetail);
//        }

        ExtendCarInsurancePolicy.GetPremiumResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetPremiumRequest, ExtendCarInsurancePolicy.GetPremiumResponse>(get_premium, getPremiumRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 精准报价
     */
    private static final String get_premium_calibrate = CarInsuranceCommon.getServerHost() + "/main/exactnessQuote";

    public String getPremiumCalibrate(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetPremiumCalibrateRequest getPremiumCalibrateRequest = new ExtendCarInsurancePolicy.GetPremiumCalibrateRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        getPremiumCalibrateRequest.refId
//        getPremiumCalibrateRequest.thpBizID
//        getPremiumCalibrateRequest.cityCode
//        getPremiumCalibrateRequest.biBeginDate
//        getPremiumCalibrateRequest.ciBeginDate
//        getPremiumCalibrateRequest.agentMobile
//        getPremiumCalibrateRequest.insurerCode
//        getPremiumCalibrateRequest.remittingTax
//        getPremiumCalibrateRequest.invoiceType
//        getPremiumCalibrateRequest.payType
//        getPremiumCalibrateRequest.responseNo

//        ExtendCarInsurancePolicy.CarInfoDetail carInfo = new ExtendCarInsurancePolicy.CarInfoDetail();
//        carInfo.engineNo;
//        carInfo.licenseNo;
//        carInfo.frameNo;
//        carInfo.firstRegisterDate;
//        carInfo.brandCode;
//        carInfo.isTrans;
//        carInfo.transDate;
//        carInfo.firstRegisterDate;
//        carInfo.sourceCertificateNo;

//        ExtendCarInsurancePolicy.InsuranceParticipant personInfo = new ExtendCarInsurancePolicy.InsuranceParticipant();
//        personInfo.ownerName
//        personInfo.ownerID
//        personInfo.ownerMobile
//        personInfo.insuredName
//        personInfo.insuredID
//        personInfo.insuredMobile
//        personInfo.applicantName
//        personInfo.applicantID
//        personInfo.applicantMobile

//        getPremiumCalibrateRequest.coverageList = new ArrayList<>();

//        for () {
//            ExtendCarInsurancePolicy.InsuranceInfoDetail insuranceInfoDetail = new ExtendCarInsurancePolicy.InsuranceInfoDetail();
//            insuranceInfoDetail.coverageCode;
//            insuranceInfoDetail.coverageName;
//            insuranceInfoDetail.insuredAmount;
//            insuranceInfoDetail.flag
//            carInfo.coverageList.add(insuranceInfoDetail);
//        }


        ExtendCarInsurancePolicy.GetPremiumCalibrateResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetPremiumCalibrateRequest, ExtendCarInsurancePolicy.GetPremiumCalibrateResponse>(get_premium_calibrate, getPremiumCalibrateRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 车辆定价因子信息接口
     */
    private static final String get_premium_factor = "http://apiplus-test.ztwltech.com/v2.0/assist/quoteFactors";

    public String getPremiumFactor(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetPremiumFactorRequest getPremiumFactorRequest = new ExtendCarInsurancePolicy.GetPremiumFactorRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        getPremiumFactorRequest.bizID

        ExtendCarInsurancePolicy.GetPremiumFactorResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetPremiumFactorRequest, ExtendCarInsurancePolicy.GetPremiumFactorResponse>(get_premium_factor, getPremiumFactorRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";

    }

    /**
     * 申请核保
     */
    private static final String apply_underwriting = CarInsuranceCommon.getServerHost() + "/main/applyUnderwrite";

    public String applyUnderwriting(ActionBean actionBean) {
        ExtendCarInsurancePolicy.ApplyUnderwritingRequest applyUnderwritingRequest = new ExtendCarInsurancePolicy.ApplyUnderwritingRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        applyUnderwritingRequest.channelCode
//        applyUnderwritingRequest.insurerCode
//        applyUnderwritingRequest.bizID
//        applyUnderwritingRequest.addresseeName
//        applyUnderwritingRequest.addresseeMobile
//        applyUnderwritingRequest.addresseeDetails
//        applyUnderwritingRequest.policyEmail
//        applyUnderwritingRequest.addresseeCounty
//        applyUnderwritingRequest.addresseeCity
//        applyUnderwritingRequest.addresseeProvince
//        applyUnderwritingRequest.verificationCode
//        applyUnderwritingRequest.refereeMobile
//        applyUnderwritingRequest.payType

        ExtendCarInsurancePolicy.ApplyUnderwritingResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.ApplyUnderwritingRequest, ExtendCarInsurancePolicy.ApplyUnderwritingResponse>(apply_underwriting, applyUnderwritingRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 回写核保信息（回调接口）
     */
    private static final String get_apply_underwriting_result = "";

    public String getApplyUnderwritingResult(ActionBean actionBean) {
        // TODO: 2018/3/30  actionBean 就是我们的Request，处理数据，处理回执
        ExtendCarInsurancePolicy.GetApplyUnderwritingResultRequest request = JsonKit.json2Bean(actionBean.body, ExtendCarInsurancePolicy.GetApplyUnderwritingResultRequest.class);

        ExtendCarInsurancePolicy.GetApplyUnderwritingResultResponse response = new ExtendCarInsurancePolicy.GetApplyUnderwritingResultResponse();

        return JsonKit.bean2Json(response);
    }

    /**
     * 获取支付链接
     */
    private static final String get_pay_link = CarInsuranceCommon.getServerHost() + "/payment/payLink";

    public String getPayLink(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetPayLinkRequest getPayLinkRequest = new ExtendCarInsurancePolicy.GetPayLinkRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        getPayLinkRequest.bizID

        ExtendCarInsurancePolicy.GetPayLinkResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetPayLinkRequest, ExtendCarInsurancePolicy.GetPayLinkResponse>(get_pay_link, getPayLinkRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 手机号验证码接口
     */
    private static final String verify_phone_code = CarInsuranceCommon.getServerHost() + "/assist/sendBjVerifyCode";

    public String verifyPhoneCode(ActionBean actionBean) {
        ExtendCarInsurancePolicy.VerifyPhoneCodeRequest verifyPhoneCodeRequest = new ExtendCarInsurancePolicy.VerifyPhoneCodeRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        verifyPhoneCodeRequest.bizID
//        verifyPhoneCodeRequest.verificationCode

        ExtendCarInsurancePolicy.VerifyPhoneCodeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.VerifyPhoneCodeRequest, ExtendCarInsurancePolicy.VerifyPhoneCodeResponse>(verify_phone_code, verifyPhoneCodeRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 北京重新发送验证码接口
     */
    private static final String re_get_phone_verify_code = "http://api-mock.ztwltech.com/v2.0/assist/resendBjVerifyCode";

    public String reGetPhoneVerifyCode(ActionBean actionBean) {
        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest reGetPhoneVerifyCodeRequest = new ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        reGetPhoneVerifyCodeRequest.bizID

        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest, ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse>(re_get_phone_verify_code, reGetPhoneVerifyCodeRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

    /**
     * 回写保单信息(回调接口)
     */
    private static final String get_insurance_policy = "";

    public String getInsurancePolicy(ActionBean actionBean) {
        // TODO: 2018/3/30  actionBean 就是我们的Request，处理数据，处理回执
        ExtendCarInsurancePolicy.GetInsurancePolicyRequest request = JsonKit.json2Bean(actionBean.body, ExtendCarInsurancePolicy.GetInsurancePolicyRequest.class);

        ExtendCarInsurancePolicy.GetInsurancePolicyResponse response = new ExtendCarInsurancePolicy.GetInsurancePolicyResponse();

        return JsonKit.bean2Json(response);
    }

    /**
     * 回写配送信息(回调接口)
     */
    private static final String get_express_info = "";

    public String getExpressInfo(ActionBean actionBean) {
        // TODO: 2018/3/30  actionBean 就是我们的Request，处理数据，处理回执
        ExtendCarInsurancePolicy.GetExpressInfoRequest request = JsonKit.json2Bean(actionBean.body, ExtendCarInsurancePolicy.GetExpressInfoRequest.class);

        ExtendCarInsurancePolicy.GetExpressInfoResponse response = new ExtendCarInsurancePolicy.GetExpressInfoResponse();

        return JsonKit.bean2Json(response);
    }

    /**
     * 保险公司投保声明
     */
    private static final String get_insurance_instruction = CarInsuranceCommon.getServerHost() + "/assist/statement";

    public String getInsuranceInstruction(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetInsuranceInstructionRequest getInsuranceInstructionRequest = new ExtendCarInsurancePolicy.GetInsuranceInstructionRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
//        reGetPhoneVerifyCodeRequest.bizID

        ExtendCarInsurancePolicy.GetInsuranceInstructionResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetInsuranceInstructionRequest, ExtendCarInsurancePolicy.GetInsuranceInstructionResponse>(get_insurance_instruction, getInsuranceInstructionRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {

        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return "";
    }

}
