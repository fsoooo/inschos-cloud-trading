package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.CarInsurance;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.extend.car.*;

import java.text.SimpleDateFormat;
import java.util.Date;

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
                        str = json(BaseResponse.CODE_FAILURE, "获取省级列表失败", response);
                    }
                } else {
                    str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);
                }
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
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
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
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

    // FINISH: 2018/3/31
    public String getInsuranceByArea(ActionBean actionBean) {
        CarInsurance.GetInsuranceCompanyRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceCompanyRequest.class);
        CarInsurance.GetInsuranceCompanyResponse response = new CarInsurance.GetInsuranceCompanyResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetInsuranceCompanyRequest getInsuranceCompanyRequest = new ExtendCarInsurancePolicy.GetInsuranceCompanyRequest();

        getInsuranceCompanyRequest.provinceCode = request.provinceCode;

        ExtendCarInsurancePolicy.GetInsuranceCompanyResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetInsuranceCompanyRequest, ExtendCarInsurancePolicy.GetInsuranceCompanyResponse>(get_insurance_by_area, getInsuranceCompanyRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取市级列表成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }
        return str;
    }

    /**
     * 险别查询
     */
    private static final String get_insurance_info = CarInsuranceCommon.getServerHost() + "/mdata/risks";

    // FINISH: 2018/3/31
    public String getInsuranceInfo(ActionBean actionBean) {
        CarInsurance.GetInsuranceInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceInfoRequest.class);
        CarInsurance.GetInsuranceInfoResponse response = new CarInsurance.GetInsuranceInfoResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetInsuranceInfoRequest getInsuranceInfoRequest = new ExtendCarInsurancePolicy.GetInsuranceInfoRequest();

        ExtendCarInsurancePolicy.GetInsuranceInfoResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetInsuranceInfoRequest, ExtendCarInsurancePolicy.GetInsuranceInfoResponse>(get_insurance_info, getInsuranceInfoRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取险别列表成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
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
     * FINISH: 2018/3/31
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

                if (result.data.engineNo == null) {
                    result.data.engineNo = "null";
                }
                response.signToken = SignatureTools.sign(result.data.frameNo + result.data.engineNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                str = json(BaseResponse.CODE_SUCCESS, "获取车辆号码信息成功", new BaseResponse());
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
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

    /**
     * 返回车型，需要调用通用的responseNo处理方法{@link #dealCarInfoResponseNo}
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    // FINISH: 2018/3/31
    public String getCarModel(ActionBean actionBean) {
        CarInsurance.GetCarModelRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarModelRequest.class);
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

        if (request.carInfo.engineNo == null) {
            request.carInfo.engineNo = "null";
        }

        // 是否新车
        boolean isNew = StringKit.equals(request.isNew, "1");
        // 是否更改车架号与发动机号
        boolean verify = SignatureTools.verify(request.carInfo.frameNo + request.carInfo.engineNo, request.signToken, SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);

        String s = dealCarInfoResponseNo(isNew, verify, request.carInfo);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
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
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 处理响应码，如果返回null，则正常处理，如果返回非null，就需要返回以String为message的response
     *
     * @param isNew   是否新车
     * @param verify  是否修改过发动机号或车架号
     * @param carInfo 车辆信息
     * @return 错误信息
     */
    private String dealCarInfoResponseNo(boolean isNew, boolean verify, ExtendCarInsurancePolicy.CarInfo carInfo) {
        if (!verify) {
            carInfo.responseNo = "";
            if (StringKit.isEmpty(carInfo.frameNo) || StringKit.equals(carInfo.frameNo, "null")) {
                return "请输入车架号";
            }

            if (StringKit.isEmpty(carInfo.engineNo) || StringKit.equals(carInfo.engineNo, "null")) {
                return "请输入发动机号";
            }
        }

        if (isNew) {
            carInfo.licenseNo = "新车";
            carInfo.responseNo = "";
            if (StringKit.isEmpty(carInfo.frameNo) || StringKit.equals(carInfo.frameNo, "null")) {
                return "请输入车架号";
            }
        }
        return null;
    }

    /**
     * 车辆号码与车型信息
     */
    private static final String get_car_model_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleAndModel";

    // FINISH: 2018/3/31
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
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                if (result.data.frameNo == null) {
                    result.data.frameNo = "null";
                }
                response.signToken = SignatureTools.sign(result.data.frameNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                str = json(BaseResponse.CODE_SUCCESS, "获取车辆号码与车型信息成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
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

    // FINISH: 2018/3/31
    public String getCarModelByKey(ActionBean actionBean) {
        CarInsurance.GetCarModelRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarModelRequest.class);
        CarInsurance.GetCarModelResponse response = new CarInsurance.GetCarModelResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        if (StringKit.isEmpty(request.pageNum)) {
            return json(BaseResponse.CODE_FAILURE, "缺少页码", new BaseResponse());
        }

        ExtendCarInsurancePolicy.GetCarModelRequest getCarModelRequest = new ExtendCarInsurancePolicy.GetCarModelRequest();

        getCarModelRequest.brandName = request.brandName;

        if (StringKit.isEmpty(request.pageSize)) {
            getCarModelRequest.row = "10";
        } else {
            getCarModelRequest.row = request.pageSize;
        }

        getCarModelRequest.page = request.pageNum;

        ExtendCarInsurancePolicy.GetCarModelResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetCarModelRequest, ExtendCarInsurancePolicy.GetCarModelResponse>(get_car_model_by_key, getCarModelRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "搜索车型信息成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 将获取保险公司、获取起保时间、获取险别列表、获取参考价格合并为一个请求
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoAndPremium(ActionBean actionBean) {
        CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumRequest.class);
        CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumResponse response = new CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumResponse();


        String insuranceByArea = getInsuranceByArea(actionBean);
        CarInsurance.GetInsuranceCompanyResponse getInsuranceCompanyResponse = JsonKit.json2Bean(insuranceByArea, CarInsurance.GetInsuranceCompanyResponse.class);

        if (getInsuranceCompanyResponse == null || getInsuranceCompanyResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceByArea;
        }

        response.data.insuranceCompanies = getInsuranceCompanyResponse.data;

        String insuranceStartTime = getInsuranceStartTime(actionBean);
        CarInsurance.GetInsuranceStartTimeResponse getInsuranceStartTimeResponse = JsonKit.json2Bean(insuranceStartTime, CarInsurance.GetInsuranceStartTimeResponse.class);

        if (getInsuranceStartTimeResponse == null || getInsuranceStartTimeResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceStartTime;
        }

        response.data.startTimeInfo = getInsuranceStartTimeResponse.data;

        String insuranceInfo = getInsuranceInfo(actionBean);
        CarInsurance.GetInsuranceInfoResponse getInsuranceInfoResponse = JsonKit.json2Bean(insuranceInfo, CarInsurance.GetInsuranceInfoResponse.class);

        if (getInsuranceInfoResponse == null || getInsuranceInfoResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceInfo;
        }

        // TODO: 2018/3/31 需要知道险别列表是否必传  getInsuranceInfoResponse.data == null || getInsuranceInfoResponse.data.isEmpty()

        String str;
        if (getInsuranceCompanyResponse.data == null || getInsuranceCompanyResponse.data.isEmpty()) {
            response.data.premiumInfo = null;
            str = json(BaseResponse.CODE_SUCCESS, "获取成功", response);
        } else {

            ExtendCarInsurancePolicy.InsuranceCompany insuranceCompany = getInsuranceCompanyResponse.data.get(0);

            request.insurerCode = insuranceCompany.insurerCode;

            // TODO: 2018/3/31 处理险别列表
            // request.coverageList

            actionBean.body = JsonKit.bean2Json(request);

            String premium = getPremium(actionBean);
            CarInsurance.GetPremiumResponse getPremiumResponse = JsonKit.json2Bean(premium, CarInsurance.GetPremiumResponse.class);

            if (getPremiumResponse == null || getPremiumResponse.code != BaseResponse.CODE_SUCCESS) {
                return premium;
            } else {
                str = json(BaseResponse.CODE_SUCCESS, "获取成功", response);
            }
        }

        return str;
    }

    /**
     * 获取投保起期
     */
    private static final String get_insurance_start_time = CarInsuranceCommon.getServerHost() + "/assist/effectiveDate";

    /**
     * {@link #dealCarInfoResponseNo}
     */
    // FINISH: 2018/3/31
    public String getInsuranceStartTime(ActionBean actionBean) {
        CarInsurance.GetInsuranceStartTimeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceStartTimeRequest.class);
        CarInsurance.GetInsuranceStartTimeResponse response = new CarInsurance.GetInsuranceStartTimeResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        if (request.carInfo == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少车辆信息", response);
        }

        // 是否过户车
        boolean isTrans = StringKit.equals(request.carInfo.isTrans, "1");
        // 是否更改车架号与发动机号
        boolean verify = SignatureTools.verify(request.carInfo.frameNo + request.carInfo.engineNo, request.signToken, SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);

        String s = dealCarInfoResponseNo(!isTrans, verify, request.carInfo);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        ExtendCarInsurancePolicy.GetInsuranceStartTimeRequest getInsuranceStartTimeRequest = new ExtendCarInsurancePolicy.GetInsuranceStartTimeRequest();

        getInsuranceStartTimeRequest.responseNo = request.carInfo.responseNo;

        getInsuranceStartTimeRequest.licenseNo = request.carInfo.licenseNo;
        getInsuranceStartTimeRequest.frameNo = request.carInfo.frameNo;
        getInsuranceStartTimeRequest.brandCode = request.carInfo.brandCode;
        getInsuranceStartTimeRequest.engineNo = request.carInfo.engineNo;

        getInsuranceStartTimeRequest.isTrans = request.carInfo.isTrans;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (isTrans) {
            Long aLong = formatterDate(request.carInfo.transDate);
            if (aLong == null) {
                return json(BaseResponse.CODE_FAILURE, "过户日期格式错误", response);
            }
            getInsuranceStartTimeRequest.transDate = simpleDateFormat.format(new Date(aLong));
        }

        Long aLong = formatterDate(request.carInfo.firstRegisterDate);
        if (aLong == null) {
            return json(BaseResponse.CODE_FAILURE, "初登日期格式错误", response);
        }
        getInsuranceStartTimeRequest.firstRegisterDate = simpleDateFormat.format(new Date(aLong));


        getInsuranceStartTimeRequest.cityCode = request.cityCode;

        if (request.personInfo != null) {
            if (!StringKit.isEmpty(request.personInfo.ownerName)) {
                getInsuranceStartTimeRequest.ownerName = request.personInfo.ownerName;
            }

            if (!StringKit.isEmpty(request.personInfo.ownerMobile)) {
                getInsuranceStartTimeRequest.ownerMobile = request.personInfo.ownerMobile;
            }

            if (!StringKit.isEmpty(request.personInfo.ownerID)) {
                getInsuranceStartTimeRequest.ownerID = request.personInfo.ownerID;
            }
        }

        ExtendCarInsurancePolicy.GetInsuranceStartTimeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetInsuranceStartTimeRequest, ExtendCarInsurancePolicy.GetInsuranceStartTimeResponse>(get_insurance_start_time, getInsuranceStartTimeRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取日期成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 参考报价
     */
    private static final String get_premium = CarInsuranceCommon.getServerHost() + "/main/referenceQuote";

    /**
     * {@link #dealCarInfoResponseNo}
     */
    public String getPremium(ActionBean actionBean) {
        CarInsurance.GetPremiumRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetPremiumRequest.class);
        CarInsurance.GetPremiumResponse response = new CarInsurance.GetPremiumResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        if (request.carInfo == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少车辆信息", response);
        }

        // 是否新车
        boolean isTrans = StringKit.equals(request.carInfo.isTrans, "1");
        // 是否更改车架号与发动机号
        boolean verify = SignatureTools.verify(request.carInfo.frameNo + request.carInfo.engineNo, request.signToken, SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);

        String s = dealCarInfoResponseNo(!isTrans, verify, request.carInfo);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        ExtendCarInsurancePolicy.GetPremiumRequest getPremiumRequest = new ExtendCarInsurancePolicy.GetPremiumRequest();

        // TODO: 2018/3/29  从我们的request里面获取这个值
        getPremiumRequest.cityCode = request.cityCode;
        getPremiumRequest.insurerCode = request.cityCode;
        getPremiumRequest.responseNo = request.carInfo.responseNo;
        ExtendCarInsurancePolicy.CarInfoDetail carInfo = new ExtendCarInsurancePolicy.CarInfoDetail();
        carInfo.engineNo = request.carInfo.engineNo;
        carInfo.licenseNo = request.carInfo.licenseNo;
        carInfo.frameNo = request.carInfo.frameNo;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Long firstRegisterDate = formatterDate(request.carInfo.firstRegisterDate);
        if (firstRegisterDate == null) {
            return json(BaseResponse.CODE_FAILURE, "初登日期格式错误", response);
        }
        carInfo.firstRegisterDate = simpleDateFormat.format(new Date(firstRegisterDate));

        carInfo.brandCode = request.carInfo.brandCode;
        carInfo.isTrans = request.carInfo.isTrans;

        if (isTrans) {
            Long transDate = formatterDate(request.carInfo.transDate);
            if (transDate == null) {
                return json(BaseResponse.CODE_FAILURE, "过户日期格式错误", response);
            }
            carInfo.transDate = simpleDateFormat.format(new Date(transDate));
        }

        if (request.personInfo != null) {
            ExtendCarInsurancePolicy.VehicleOwnerInfo personInfo = new ExtendCarInsurancePolicy.VehicleOwnerInfo();
            personInfo.ownerName = request.personInfo.ownerName;
            personInfo.ownerID = request.personInfo.ownerID;
            personInfo.ownerMobile = request.personInfo.ownerMobile;
        }

        // TODO: 2018/3/31 处理险别列表
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
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取参考报价成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 精准报价
     */
    private static final String get_premium_calibrate = CarInsuranceCommon.getServerHost() + "/main/exactnessQuote";

    public String getPremiumCalibrate(ActionBean actionBean) {
        CarInsurance.GetPremiumCalibrateRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetPremiumCalibrateRequest.class);
        CarInsurance.GetPremiumCalibrateResponse response = new CarInsurance.GetPremiumCalibrateResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetPremiumCalibrateRequest getPremiumCalibrateRequest = new ExtendCarInsurancePolicy.GetPremiumCalibrateRequest();

        getPremiumCalibrateRequest.refId = request.refId;
        // TODO: 2018/3/31 代理人下单，必须带
        getPremiumCalibrateRequest.agentMobile = request.agentMobile;
        getPremiumCalibrateRequest.payType = request.payType;
        getPremiumCalibrateRequest.invoiceType = request.invoiceType;
        getPremiumCalibrateRequest.remittingTax = request.remittingTax;


        getPremiumCalibrateRequest.thpBizID = getThpBizID();
        getPremiumCalibrateRequest.cityCode = request.cityCode;

        // TODO: 2018/3/31 判断是不是即时起保（非即时起保，格式yyyy-MM-dd，日期必须大于等于明天如2016-04-26；即时起保，格式yyyy-MM-dd HH:mm:ss， 时间必须大于当前时间，分和秒必须为00，如2016-04-26 15:00:00;）
        boolean isImmediately = true;
        SimpleDateFormat sdf;
        if (isImmediately) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }

        Long biBeginDate = formatterDate(request.biBeginDate);
        if (biBeginDate == null) {
            return json(BaseResponse.CODE_FAILURE, "商业险起保日期错误", response);
        }

        getPremiumCalibrateRequest.biBeginDate = sdf.format(new Date(biBeginDate));

        Long ciBeginDate = formatterDate(request.ciBeginDate);
        if (ciBeginDate == null) {
            return json(BaseResponse.CODE_FAILURE, "强险起保日期错误", response);
        }

        getPremiumCalibrateRequest.ciBeginDate = sdf.format(new Date(ciBeginDate));

        getPremiumCalibrateRequest.insurerCode = request.insurerCode;
        getPremiumCalibrateRequest.responseNo = request.responseNo;

        if (request.carInfo == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少车辆信息", response);
        }

        // 是否过户车
        boolean isTrans = StringKit.equals(request.carInfo.isTrans, "1");
        // 是否更改车架号与发动机号
        boolean verify = SignatureTools.verify(request.carInfo.frameNo + request.carInfo.engineNo, request.signToken, SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);

        String s = dealCarInfoResponseNo(!isTrans, verify, request.carInfo);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        if (!isTrans) {
            // 新车备案要用到的-来历凭证编号
            if (StringKit.isEmpty(request.carInfo.sourceCertificateNo)) {
                return json(BaseResponse.CODE_FAILURE, "缺少来历凭证编号", response);
            }
        }

        getPremiumCalibrateRequest.carInfo = request.carInfo;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (isTrans) {
            Long aLong = formatterDate(getPremiumCalibrateRequest.carInfo.transDate);
            if (aLong == null) {
                return json(BaseResponse.CODE_FAILURE, "过户日期格式错误", response);
            }
            getPremiumCalibrateRequest.carInfo.transDate = simpleDateFormat.format(new Date(aLong));
        }

        Long aLong = formatterDate(getPremiumCalibrateRequest.carInfo.firstRegisterDate);
        if (aLong == null) {
            return json(BaseResponse.CODE_FAILURE, "初登日期格式错误", response);
        }
        getPremiumCalibrateRequest.carInfo.firstRegisterDate = simpleDateFormat.format(new Date(aLong));

        if (request.personInfo == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少人员信息", response);
        }

        if (!request.personInfo.isEnable()) {
            return json(BaseResponse.CODE_FAILURE, "缺少人员信息", response);
        }

        getPremiumCalibrateRequest.personInfo = request.personInfo;

        if (request.coverageList == null || request.coverageList.isEmpty()) {
            return json(BaseResponse.CODE_FAILURE, "缺少险别", response);
        }

        getPremiumCalibrateRequest.coverageList = request.coverageList;

        ExtendCarInsurancePolicy.GetPremiumCalibrateResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetPremiumCalibrateRequest, ExtendCarInsurancePolicy.GetPremiumCalibrateResponse>(get_premium_calibrate, getPremiumCalibrateRequest).post();
        // 验签
        String str;
        // TODO: 2018/3/29 返回我们的response
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取报价成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 申请核保
     */
    private static final String apply_underwriting = CarInsuranceCommon.getServerHost() + "/main/applyUnderwrite";

    // FINISH: 2018/4/2
    public String applyUnderwriting(ActionBean actionBean) {
        CarInsurance.ApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ApplyUnderwritingRequest.class);
        CarInsurance.ApplyUnderwritingResponse response = new CarInsurance.ApplyUnderwritingResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        if (request.isNeedVerificationCode() && StringKit.isEmpty(request.verificationCode)) {
            return json(BaseResponse.CODE_FAILURE, "缺少验证码", response);
        }

        ExtendCarInsurancePolicy.ApplyUnderwritingRequest applyUnderwritingRequest = new ExtendCarInsurancePolicy.ApplyUnderwritingRequest();

        // 非必传
        applyUnderwritingRequest.payType = request.payType;
        applyUnderwritingRequest.applicantUrl = request.applicantUrl;

        applyUnderwritingRequest.refereeMobile = request.refereeMobile;

        if (request.isNeedVerificationCode()) {
            applyUnderwritingRequest.verificationCode = request.verificationCode;
        }

        applyUnderwritingRequest.channelCode = request.channelCode;
        applyUnderwritingRequest.insurerCode = request.insurerCode;
        applyUnderwritingRequest.bizID = request.bizID;
        applyUnderwritingRequest.addresseeName = request.addresseeName;
        applyUnderwritingRequest.addresseeMobile = request.addresseeMobile;
        applyUnderwritingRequest.addresseeDetails = request.addresseeDetails;
        applyUnderwritingRequest.policyEmail = request.policyEmail;
        applyUnderwritingRequest.addresseeCounty = request.addresseeCounty;
        applyUnderwritingRequest.addresseeCity = request.addresseeCity;
        applyUnderwritingRequest.addresseeProvince = request.addresseeProvince;

        ExtendCarInsurancePolicy.ApplyUnderwritingResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.ApplyUnderwritingRequest, ExtendCarInsurancePolicy.ApplyUnderwritingResponse>(apply_underwriting, applyUnderwritingRequest).post();
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "申请核保成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 将精准报价与申请核保合并为一个接口
     *
     * @param actionBean 请求参数
     * @return 响应json
     */
    // FINISH: 2018/4/2
    public String getPremiumCalibrateAndApplyUnderwriting(ActionBean actionBean) {
        CarInsurance.PremiumCalibrateAndApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.PremiumCalibrateAndApplyUnderwritingRequest.class);
        CarInsurance.PremiumCalibrateAndApplyUnderwritingResponse response = new CarInsurance.PremiumCalibrateAndApplyUnderwritingResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        if (request.premiumCalibrate == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少报价参数", response);
        }

        if (request.applyUnderwriting == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少核保参数", response);
        }

        actionBean.body = JsonKit.bean2Json(request.premiumCalibrate);

        String premiumCalibrate = getPremiumCalibrate(actionBean);
        CarInsurance.GetPremiumCalibrateResponse getPremiumCalibrateResponse = JsonKit.json2Bean(premiumCalibrate, CarInsurance.GetPremiumCalibrateResponse.class);

        if (getPremiumCalibrateResponse == null || getPremiumCalibrateResponse.code != BaseResponse.CODE_SUCCESS) {
            return premiumCalibrate;
        }

        if (getPremiumCalibrateResponse.data != null && !getPremiumCalibrateResponse.data.isEmpty()) {
            request.applyUnderwriting.bjCodeFlag = getPremiumCalibrateResponse.data.get(0).bjCodeFlag;
            actionBean.body = JsonKit.bean2Json(request.applyUnderwriting);
            return applyUnderwriting(actionBean);
        } else {
            return premiumCalibrate;
        }
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
     * 车辆定价因子信息接口
     */
    private static final String get_premium_factor = "http://apiplus-test.ztwltech.com/v2.0/assist/quoteFactors";

    public String getPremiumFactor(ActionBean actionBean) {
        CarInsurance.GetPremiumFactorRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetPremiumFactorRequest.class);
        CarInsurance.GetPremiumFactorResponse response = new CarInsurance.GetPremiumFactorResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetPremiumFactorRequest getPremiumFactorRequest = new ExtendCarInsurancePolicy.GetPremiumFactorRequest();

        getPremiumFactorRequest.bizID = request.bizID;

        ExtendCarInsurancePolicy.GetPremiumFactorResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetPremiumFactorRequest, ExtendCarInsurancePolicy.GetPremiumFactorResponse>(get_premium_factor, getPremiumFactorRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取定价因子成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;

    }

    /**
     * 获取支付链接
     */
    private static final String get_pay_link = CarInsuranceCommon.getServerHost() + "/payment/payLink";

    // FINISH: 2018/4/2
    public String getPayLink(ActionBean actionBean) {
        CarInsurance.GetPayLinkRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetPayLinkRequest.class);
        CarInsurance.GetPayLinkResponse response = new CarInsurance.GetPayLinkResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.GetPayLinkRequest getPayLinkRequest = new ExtendCarInsurancePolicy.GetPayLinkRequest();

        getPayLinkRequest.bizID = request.bizID;

        ExtendCarInsurancePolicy.GetPayLinkResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.GetPayLinkRequest, ExtendCarInsurancePolicy.GetPayLinkResponse>(get_pay_link, getPayLinkRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取支付链接成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 手机号验证码接口
     */
    private static final String verify_phone_code = CarInsuranceCommon.getServerHost() + "/assist/sendBjVerifyCode";

    // FINISH: 2018/4/2
    public String verifyPhoneCode(ActionBean actionBean) {
        CarInsurance.VerifyPhoneCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.VerifyPhoneCodeRequest.class);
        CarInsurance.VerifyPhoneCodeResponse response = new CarInsurance.VerifyPhoneCodeResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.VerifyPhoneCodeRequest verifyPhoneCodeRequest = new ExtendCarInsurancePolicy.VerifyPhoneCodeRequest();

        verifyPhoneCodeRequest.bizID = request.bizID;
        verifyPhoneCodeRequest.verificationCode = request.verificationCode;

        ExtendCarInsurancePolicy.VerifyPhoneCodeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.VerifyPhoneCodeRequest, ExtendCarInsurancePolicy.VerifyPhoneCodeResponse>(verify_phone_code, verifyPhoneCodeRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "验证成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 获取北京发送验证码接口
     */
    private static final String get_phone_verify_code = "http://api-mock.ztwltech.com/v2.0/assist/resendBjVerifyCode";

    // FINISH: 2018/4/2
    public String getPhoneVerifyCode(ActionBean actionBean) {
        CarInsurance.ReGetPhoneVerifyCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ReGetPhoneVerifyCodeRequest.class);
        CarInsurance.ReGetPhoneVerifyCodeResponse response = new CarInsurance.ReGetPhoneVerifyCodeResponse();

        String errorMessage = checkParam(request);
        if (!StringKit.isEmpty(errorMessage)) {
            return json(BaseResponse.CODE_FAILURE, errorMessage, response);
        }

        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest reGetPhoneVerifyCodeRequest = new ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest();

        reGetPhoneVerifyCodeRequest.bizID = request.bizID;

        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse result = new CarInsuranceHttpRequest<ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest, ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse>(get_phone_verify_code, reGetPhoneVerifyCodeRequest).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                str = json(BaseResponse.CODE_SUCCESS, "获取验证码成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
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

    /**
     * 返回long时间戳
     *
     * @param date 毫秒时间戳字符串
     * @return 如果转换失败，返回null，否则返回时间戳
     */
    private Long formatterDate(String date) {
        if (StringKit.isEmpty(date) || !StringKit.isInteger(date)) {
            return null;
        }
        return Long.valueOf(date);
    }

    /**
     * 获取第三方业务唯一标识
     *
     * @return 第三方业务唯一标识
     */
    private String getThpBizID() {
        return "";
    }

}
