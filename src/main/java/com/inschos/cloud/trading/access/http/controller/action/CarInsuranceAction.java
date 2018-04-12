package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.CarInsurance;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.assist.kit.WarrantyUuidWorker;
import com.inschos.cloud.trading.data.dao.CarRecordDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.extend.car.CarInsuranceHttpRequest;
import com.inschos.cloud.trading.extend.car.CarInsuranceResponse;
import com.inschos.cloud.trading.extend.car.ExtendCarInsurancePolicy;
import com.inschos.cloud.trading.extend.car.SignatureTools;
import com.inschos.cloud.trading.model.CarInfoModel;
import com.inschos.cloud.trading.model.CarRecordModel;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.fordao.InsurancePolicyAndParticipantForCarInsurance;
import com.inschos.cloud.trading.model.fordao.UpdateInsurancePolicyProPolicyNoForCarInsurance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.inschos.cloud.trading.extend.car.CarInsuranceCommon.*;

/**
 * 创建日期：2018/3/29 on 14:27
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CarInsuranceAction extends BaseAction {

    @Autowired
    private CarRecordDao carRecordDao;

    @Autowired
    private InsurancePolicyDao insurancePolicyDao;

    /**
     * 获取省级区域代码
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getProvinceCode(ActionBean actionBean) {
        CarInsurance.GetProvinceCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetProvinceCodeRequest.class);
        CarInsurance.GetProvinceCodeResponse response = new CarInsurance.GetProvinceCodeResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetProvinceCodeRequest getProvinceCodeRequest = new ExtendCarInsurancePolicy.GetProvinceCodeRequest();

        ExtendCarInsurancePolicy.GetProvinceCodeResponse result = new CarInsuranceHttpRequest<>(get_province_code, getProvinceCodeRequest, ExtendCarInsurancePolicy.GetProvinceCodeResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetProvinceCodeResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                if (result.data != null && !result.data.isEmpty()) {
                    ActionBean bean = new ActionBean();


                    bean.buildCode = actionBean.buildCode;
                    bean.platform = actionBean.platform;
                    bean.apiCode = actionBean.apiCode;
                    bean.userId = actionBean.userId;
                    bean.url = actionBean.url;

                    CarInsurance.GetCityCodeRequest getCityCodeRequest = new CarInsurance.GetCityCodeRequest();
                    getCityCodeRequest.provinceCode = result.data.get(0).provinceCode;
                    getCityCodeRequest.type = "0";

                    bean.body = JsonKit.bean2Json(getCityCodeRequest);

                    CarInsurance.GetCityCodeResponse getCityCodeResponse = JsonKit.json2Bean(getCityCode(bean), CarInsurance.GetCityCodeResponse.class);

                    if (getCityCodeResponse != null && getCityCodeResponse.data != null && getCityCodeResponse.code == BaseResponse.CODE_SUCCESS) {
                        response.data = result.data;
                        response.data.get(0).city = getCityCodeResponse.data.city;

                        for (int i = 1; i < response.data.size(); i++) {
                            response.data.get(i).city = new ArrayList<>();
                        }

                        str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);

                        if (StringKit.equals(request.type,"1")) {
                            str = str.replaceAll("provinceCode", "code");
                            str = str.replaceAll("provinceName", "name");
                            str = str.replaceAll("cityCode", "code");
                            str = str.replaceAll("cityName", "name");
                            str = str.replaceAll("countyCode", "code");
                            str = str.replaceAll("countyName", "name");
                            str = str.replaceAll("countyList", "children");
                            str = str.replaceAll("city", "children");
                        }

                        // TODO: 2018/4/3  记得给自己的系统存数据
                    } else {
                        str = json(BaseResponse.CODE_FAILURE, "获取省级列表失败", response);
                    }
                } else {
                    str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);
                    // TODO: 2018/4/3  记得给自己的系统存数据
                }
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
        }

        return str;
    }

    /**
     * 根据省级代码获取市级代码与区级代码
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getCityCode(ActionBean actionBean) {
        CarInsurance.GetCityCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCityCodeRequest.class);
        CarInsurance.GetCityCodeResponse response = new CarInsurance.GetCityCodeResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetCityCodeRequest getCityCodeRequest = new ExtendCarInsurancePolicy.GetCityCodeRequest();
        getCityCodeRequest.provinceCode = request.provinceCode;

        ExtendCarInsurancePolicy.GetCityCodeResponse result = new CarInsuranceHttpRequest<>(get_city_code, getCityCodeRequest, ExtendCarInsurancePolicy.GetCityCodeResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetCityCodeResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = new ExtendCarInsurancePolicy.ProvinceCodeDetail();
                response.data.provinceCode = request.provinceCode;
                response.data.city = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取市级列表成功", response);

                if (StringKit.equals(request.type,"1")) {
                    str = str.replaceAll("provinceCode", "code");
                    str = str.replaceAll("provinceName", "name");
                    str = str.replaceAll("cityCode", "code");
                    str = str.replaceAll("cityName", "name");
                    str = str.replaceAll("countyCode", "code");
                    str = str.replaceAll("countyName", "name");
                    str = str.replaceAll("countyList", "children");
                    str = str.replaceAll("city", "children");
                }

                // TODO: 2018/4/3  记得给自己的系统存数据
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
        }

        return str;
    }

    /**
     * 自动判断根据车架号还是车牌号获取车辆号码信息{@link #getCarInfoByLicenceNumber}{@link #getCarInfoByFrameNumber}
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getCarInfoByLicenceNumberOrFrameNumber(ActionBean actionBean) {
        CarInsurance.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarInfoRequest.class);

        if (request == null || (StringKit.isEmpty(request.frameNo) && StringKit.isEmpty(request.licenseNo))) {
            List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();

            CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
            defaultEntry.details = CheckParamsKit.FAIL;
            list.add(defaultEntry);

            CheckParamsKit.Entry<String, String> frameNo = new CheckParamsKit.Entry<>();
            frameNo.digest = "frameNo";
            frameNo.details = "frameNo与licenseNo至少存在一个";
            list.add(frameNo);

            CheckParamsKit.Entry<String, String> licenseNo = new CheckParamsKit.Entry<>();
            licenseNo.digest = "licenseNo";
            licenseNo.details = "frameNo与licenseNo至少存在一个";
            list.add(licenseNo);

            return json(BaseResponse.CODE_PARAM_ERROR, list, new BaseResponse());
        }

        if (!StringKit.isEmpty(request.frameNo)) {
            return getCarInfoByFrameNumber(actionBean);
        } else {
            return getCarInfoByLicenceNumber(actionBean);
        }

    }

    /**
     * 根据车牌号获取车辆号码信息
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    private String getCarInfoByLicenceNumber(ActionBean actionBean) {
        CarInsurance.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarInfoRequest.class);
        CarInsurance.GetCarInfoResponse response = new CarInsurance.GetCarInfoResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetCarInfoRequest getCarInfoRequest = new ExtendCarInsurancePolicy.GetCarInfoRequest();
        getCarInfoRequest.licenseNo = request.licenseNo;

        ExtendCarInsurancePolicy.GetCarInfoResponse result = new CarInsuranceHttpRequest<>(get_car_info_licence_number, getCarInfoRequest, ExtendCarInsurancePolicy.GetCarInfoResponse.class).post();

        return dealResultAndResponse(response, result);
    }

    /**
     * 根据车架号获取车辆号码信息
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    private String getCarInfoByFrameNumber(ActionBean actionBean) {
        CarInsurance.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarInfoRequest.class);
        CarInsurance.GetCarInfoResponse response = new CarInsurance.GetCarInfoResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetCarInfoRequest getCarInfoRequest = new ExtendCarInsurancePolicy.GetCarInfoRequest();
        getCarInfoRequest.frameNo = request.frameNo;

        ExtendCarInsurancePolicy.GetCarInfoResponse result = new CarInsuranceHttpRequest<>(get_car_info_frame_number, getCarInfoRequest, ExtendCarInsurancePolicy.GetCarInfoResponse.class).post();

        return dealResultAndResponse(response, result);
    }

    /**
     * 根据车辆号码信息获取车型，需要调用通用的responseNo处理方法{@link #dealCarInfoResponseNo}
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getCarModel(ActionBean actionBean) {
        CarInsurance.GetCarModelRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarModelRequest.class);
        CarInsurance.GetCarModelResponse response = new CarInsurance.GetCarModelResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (request.carInfo.frameNo == null) {
            request.carInfo.frameNo = "null";
        }

        if (request.carInfo.engineNo == null) {
            request.carInfo.engineNo = "null";
        }

        boolean notLicenseNo = StringKit.equals(request.notLicenseNo, "1");
        String s = dealCarInfoResponseNo(request.signToken, notLicenseNo, request.carInfo);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        ExtendCarInsurancePolicy.GetCarModelRequest getCarModelRequest = new ExtendCarInsurancePolicy.GetCarModelRequest();

        getCarModelRequest.licenseNo = request.carInfo.licenseNo;
        getCarModelRequest.frameNo = request.carInfo.frameNo;
        getCarModelRequest.responseNo = request.carInfo.responseNo;

        ExtendCarInsurancePolicy.GetCarModelResponse result = new CarInsuranceHttpRequest<>(get_car_model, getCarModelRequest, ExtendCarInsurancePolicy.GetCarModelResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetCarModelResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;

                for (ExtendCarInsurancePolicy.CarModel datum : response.data) {
                    datum.showText = datum.createShowText();
                }

                str = json(BaseResponse.CODE_SUCCESS, "获取车型信息成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 根据车牌号信息获取其他号码信息与车型，需要调用通用的responseNo处理方法{@link #dealCarInfoResponseNo}
     * FINISH: 2018/3/31
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getCarModelInfo(ActionBean actionBean) {
        CarInsurance.GetCarModelInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetCarModelInfoRequest.class);
        CarInsurance.GetCarModelInfoResponse response = new CarInsurance.GetCarModelInfoResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetCarModelInfoRequest getCarModelInfoRequest = new ExtendCarInsurancePolicy.GetCarModelInfoRequest();

        getCarModelInfoRequest.licenseNo = request.licenseNo;

        ExtendCarInsurancePolicy.GetCarModelInfoResponse result = new CarInsuranceHttpRequest<>(get_car_model_info, getCarModelInfoRequest, ExtendCarInsurancePolicy.GetCarModelInfoResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetCarModelInfoResponse();
            dealNullResponse(result);
        }

        // 验签
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

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String calculateDateByShowDate = parseMillisecondByShowDate(sdf, result.data.firstRegisterDate);

                if (calculateDateByShowDate == null) {
                    result.data.firstRegisterDate = "";
                    result.data.firstRegisterDateValue = "";
                } else {
                    result.data.firstRegisterDateValue = calculateDateByShowDate;
                }

                if (response.data.vehicleList != null) {
                    for (ExtendCarInsurancePolicy.CarModel datum : response.data.vehicleList) {
                        datum.showText = datum.createShowText();
                    }
                }

                response.signToken = SignatureTools.sign(result.data.frameNo + result.data.engineNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                str = json(BaseResponse.CODE_SUCCESS, "获取车辆号码与车型信息成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }


    /**
     * 搜索车型信息
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getCarModelByKey(ActionBean actionBean) {
        CarInsurance.SearchCarModelRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.SearchCarModelRequest.class);
        CarInsurance.GetCarModelResponse response = new CarInsurance.GetCarModelResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (StringKit.isEmpty(request.pageNum)) {
            return json(BaseResponse.CODE_FAILURE, "缺少页码", response);
        }

        ExtendCarInsurancePolicy.GetCarModelRequest getCarModelRequest = new ExtendCarInsurancePolicy.GetCarModelRequest();

        getCarModelRequest.brandName = request.brandName;

        if (StringKit.isEmpty(request.pageSize)) {
            getCarModelRequest.row = "10";
        } else {
            getCarModelRequest.row = request.pageSize;
        }

        getCarModelRequest.page = request.pageNum;

        ExtendCarInsurancePolicy.GetCarModelResponse result = new CarInsuranceHttpRequest<>(get_car_model_by_key, getCarModelRequest, ExtendCarInsurancePolicy.GetCarModelResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetCarModelResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                if (response.data != null) {
                    for (ExtendCarInsurancePolicy.CarModel datum : response.data) {
                        datum.showText = datum.createShowText();
                    }
                }
                str = json(BaseResponse.CODE_SUCCESS, "搜索车型信息成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 获取当地的保险公司
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getInsuranceByArea(ActionBean actionBean) {
        CarInsurance.GetInsuranceCompanyRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceCompanyRequest.class);
        CarInsurance.GetInsuranceCompanyResponse response = new CarInsurance.GetInsuranceCompanyResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetInsuranceCompanyRequest getInsuranceCompanyRequest = new ExtendCarInsurancePolicy.GetInsuranceCompanyRequest();

        getInsuranceCompanyRequest.provinceCode = request.provinceCode;

        ExtendCarInsurancePolicy.GetInsuranceCompanyResponse result = new CarInsuranceHttpRequest<>(get_insurance_by_area, getInsuranceCompanyRequest, ExtendCarInsurancePolicy.GetInsuranceCompanyResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetInsuranceCompanyResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取保险公司成功", response);

                // TODO: 2018/4/8 验证这个保险公司的车险产品是否上架，可售
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
        }
        return str;
    }

    /**
     * 获取险别列表
     * FINISH: 2018/3/31
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getInsuranceInfo(ActionBean actionBean) {
        CarInsurance.GetInsuranceInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceInfoRequest.class);
        CarInsurance.GetInsuranceInfoResponse response = new CarInsurance.GetInsuranceInfoResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetInsuranceInfoRequest getInsuranceInfoRequest = new ExtendCarInsurancePolicy.GetInsuranceInfoRequest();

        ExtendCarInsurancePolicy.GetInsuranceInfoResponse result = new CarInsuranceHttpRequest<>(get_insurance_info, getInsuranceInfoRequest, ExtendCarInsurancePolicy.GetInsuranceInfoResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetInsuranceInfoResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = dealCoverageList(result.data);
                str = json(BaseResponse.CODE_SUCCESS, "获取险别列表成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
        }

        return str;
    }

    /**
     * 获取起保时间 {@link #dealCarInfoResponseNo}
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getInsuranceStartTime(ActionBean actionBean) {
        CarInsurance.GetInsuranceStartTimeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceStartTimeRequest.class);
        CarInsurance.GetInsuranceStartTimeResponse response = new CarInsurance.GetInsuranceStartTimeResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        // 是否过户车
        boolean isTrans = StringKit.equals(request.carInfo.isTrans, "1");
        boolean notLicenseNo = StringKit.equals(request.notLicenseNo, "1");

        String s = dealCarInfoResponseNo(request.signToken, notLicenseNo, request.carInfo);

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
            Long aLong = formatterDate(request.carInfo.transDateValue);
            if (aLong == null) {
                return json(BaseResponse.CODE_FAILURE, "过户日期格式错误", response);
            }
            getInsuranceStartTimeRequest.transDate = simpleDateFormat.format(new Date(aLong));
        }

        Long aLong = formatterDate(request.carInfo.firstRegisterDateValue);
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

        ExtendCarInsurancePolicy.GetInsuranceStartTimeResponse result = new CarInsuranceHttpRequest<>(get_insurance_start_time, getInsuranceStartTimeRequest, ExtendCarInsurancePolicy.GetInsuranceStartTimeResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetInsuranceStartTimeResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                response.data.biStartTimeValue = parseMillisecondByShowDate(sdf, result.data.biStartTime);
                response.data.ciStartTimeValue = parseMillisecondByShowDate(sdf, result.data.ciStartTime);
                str = json(BaseResponse.CODE_SUCCESS, "获取日期成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 将获取保险公司、获取起保时间、获取险别列表合并为一个请求{@link #getInsuranceStartTime}{@link #getInsuranceByArea}{@link #getInsuranceInfo}
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoActionBean(ActionBean actionBean) {
        // CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoRequest.class);
        CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoResponse response = new CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoResponse();

        String insuranceByArea = getInsuranceByArea(actionBean);
        CarInsurance.GetInsuranceCompanyResponse getInsuranceCompanyResponse = JsonKit.json2Bean(insuranceByArea, CarInsurance.GetInsuranceCompanyResponse.class);

        if (getInsuranceCompanyResponse == null || getInsuranceCompanyResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceByArea;
        }

        response.data = new CarInsurance.InsuranceCompanyAndInsuranceStartTimeAndInsuranceInfo();

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

        response.data.insuranceInfo = getInsuranceInfoResponse.data;

        return json(BaseResponse.CODE_SUCCESS, "获取车险投保信息成功", response);
    }

    /**
     * 获取参考保费 {@link #dealCarInfoResponseNo}{@link #checkCoverageList}
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getPremium(ActionBean actionBean) {
        CarInsurance.GetPremiumRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetPremiumRequest.class);
        CarInsurance.GetPremiumResponse response = new CarInsurance.GetPremiumResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        // 是否新车
        boolean isTrans = StringKit.equals(request.carInfo.isTrans, "1");
        boolean notLicenseNo = StringKit.equals(request.notLicenseNo, "1");

        String s = dealCarInfoResponseNo(request.signToken, notLicenseNo, request.carInfo);

        if (!StringKit.isEmpty(s)) {
            return json(BaseResponse.CODE_FAILURE, s, response);
        }

        ExtendCarInsurancePolicy.GetPremiumRequest getPremiumRequest = new ExtendCarInsurancePolicy.GetPremiumRequest();

        getPremiumRequest.cityCode = request.cityCode;
        getPremiumRequest.insurerCode = request.insurerCode;
        getPremiumRequest.responseNo = request.carInfo.responseNo;
        ExtendCarInsurancePolicy.CarInfoDetail carInfo = new ExtendCarInsurancePolicy.CarInfoDetail();
        carInfo.engineNo = request.carInfo.engineNo;
        carInfo.licenseNo = request.carInfo.licenseNo;
        carInfo.frameNo = request.carInfo.frameNo;
        getPremiumRequest.carInfo = carInfo;

        long time = System.currentTimeMillis();

        // 将车辆信息存入我们自己的数据库
        CarRecordModel oneByResponseNo = carRecordDao.findOneByResponseNo(request.carInfo.responseNo);

        CarRecordModel carRecordModel = new CarRecordModel();
        carRecordModel.car_code = request.carInfo.licenseNo;
        carRecordModel.name = request.personInfo.ownerName;
        carRecordModel.code = request.personInfo.ownerID;
        carRecordModel.phone = request.personInfo.ownerMobile;
        carRecordModel.frame_no = request.carInfo.frameNo;
        carRecordModel.engine_no = request.carInfo.engineNo;
        carRecordModel.vehicle_fgw_code = request.carInfo.vehicleFgwCode;
        carRecordModel.vehicle_fgw_name = request.carInfo.vehicleFgwName;
        carRecordModel.parent_veh_name = request.carInfo.parentVehName;
        carRecordModel.brand_code = request.carInfo.brandCode;
        carRecordModel.brand_name = request.carInfo.brandName;
        carRecordModel.engine_desc = request.carInfo.engineDesc;
        carRecordModel.new_car_price = request.carInfo.newCarPrice;
        carRecordModel.purchase_price_tax = request.carInfo.purchasePriceTax;
        carRecordModel.import_flag = request.carInfo.importFlag;
        carRecordModel.seat = request.carInfo.seat;
        carRecordModel.standard_name = request.carInfo.standardName;
        carRecordModel.is_trans = request.carInfo.isTrans;
        carRecordModel.remark = request.carInfo.remark;
        carRecordModel.response_no = request.carInfo.responseNo;
        carRecordModel.updated_at = String.valueOf(time);

        if (oneByResponseNo == null) {
            carRecordModel.created_at = String.valueOf(time);
            carRecordDao.addCarRecord(carRecordModel);
        } else {
            carRecordDao.updateCarRecord(carRecordModel);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Long firstRegisterDate = formatterDate(request.carInfo.firstRegisterDateValue);
        if (firstRegisterDate == null) {
            return json(BaseResponse.CODE_FAILURE, "初登日期格式错误", response);
        }
        carInfo.firstRegisterDate = simpleDateFormat.format(new Date(firstRegisterDate));

        carInfo.brandCode = request.carInfo.brandCode;
        carInfo.isTrans = request.carInfo.isTrans;

        if (isTrans) {
            Long transDate = formatterDate(request.carInfo.transDateValue);
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
            getPremiumRequest.personInfo = personInfo;
        }

        // 处理险别列表，险别列表要单独验证一下，是否符合规则
        String insuranceInfo = getInsuranceInfo(actionBean);
        CarInsurance.GetInsuranceInfoResponse getInsuranceInfoResponse = JsonKit.json2Bean(insuranceInfo, CarInsurance.GetInsuranceInfoResponse.class);

        if (getInsuranceInfoResponse == null || getInsuranceInfoResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceInfo;
        }

        CheckCoverageListResult checkCoverageListResult = checkCoverageList(getInsuranceInfoResponse.data, request.coverageList);

        if (!checkCoverageListResult.result) {
            return json(BaseResponse.CODE_FAILURE, checkCoverageListResult.message, response);
        }

        getPremiumRequest.coverageList = checkCoverageListResult.coverageList;

        ExtendCarInsurancePolicy.GetPremiumResponse result = new CarInsuranceHttpRequest<>(get_premium, getPremiumRequest, ExtendCarInsurancePolicy.GetPremiumResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetPremiumResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = new CarInsurance.GetPremiumDetail();
                response.data.insurancePolicies = result.data;

                BigDecimal total = new BigDecimal("0.0");
                for (ExtendCarInsurancePolicy.InsurancePolicy datum : result.data) {
                    BigDecimal bi;
                    if (StringKit.isNumeric(datum.biPremium)) {
                        bi = new BigDecimal(datum.biPremium);
                    } else {
                        bi = new BigDecimal("0.0");
                    }

                    BigDecimal ci;
                    if (StringKit.isNumeric(datum.ciPremium)) {
                        ci = new BigDecimal(datum.ciPremium);
                    } else {
                        ci = new BigDecimal("0.0");
                    }

                    BigDecimal add = bi.add(ci);
                    datum.totalPremium = add.toString();
                    datum.totalPremiumText = "¥" + datum.totalPremium;
                    total = total.add(add);
                }

                response.data.totalInsuredPremium = total.toString();
                response.data.totalInsuredPremiumText = "¥" + total.toString();

                str = json(BaseResponse.CODE_SUCCESS, "获取参考报价成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 获取精准保费{@link #checkCoverageList}
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getPremiumCalibrate(ActionBean actionBean) {
        CarInsurance.GetPremiumCalibrateRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetPremiumCalibrateRequest.class);
        CarInsurance.GetPremiumCalibrateResponse response = new CarInsurance.GetPremiumCalibrateResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Long biBeginDate = formatterDate(request.biBeginDateValue);
        if (biBeginDate == null) {
            return json(BaseResponse.CODE_FAILURE, "商业险起保日期错误", response);
        }

        getPremiumCalibrateRequest.biBeginDate = sdf.format(new Date(biBeginDate));

        Long ciBeginDate = formatterDate(request.ciBeginDateValue);
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
        boolean notLicenseNo = StringKit.equals(request.notLicenseNo, "1");

        String s = dealCarInfoResponseNo(request.signToken, notLicenseNo, request.carInfo);

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
            Long aLong = formatterDate(getPremiumCalibrateRequest.carInfo.transDateValue);
            if (aLong == null) {
                return json(BaseResponse.CODE_FAILURE, "过户日期格式错误", response);
            }
            getPremiumCalibrateRequest.carInfo.transDate = simpleDateFormat.format(new Date(aLong));
        }

        Long aLong = formatterDate(getPremiumCalibrateRequest.carInfo.firstRegisterDateValue);
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

        String insuranceInfo = getInsuranceInfo(actionBean);
        CarInsurance.GetInsuranceInfoResponse getInsuranceInfoResponse = JsonKit.json2Bean(insuranceInfo, CarInsurance.GetInsuranceInfoResponse.class);

        if (getInsuranceInfoResponse == null || getInsuranceInfoResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceInfo;
        }

        CheckCoverageListResult checkCoverageListResult = checkCoverageList(getInsuranceInfoResponse.data, request.coverageList);

        if (!checkCoverageListResult.result) {
            return json(BaseResponse.CODE_FAILURE, checkCoverageListResult.message, response);
        }

        getPremiumCalibrateRequest.coverageList = checkCoverageListResult.coverageList;

        ExtendCarInsurancePolicy.GetPremiumCalibrateResponse result = new CarInsuranceHttpRequest<>(get_premium_calibrate, getPremiumCalibrateRequest, ExtendCarInsurancePolicy.GetPremiumCalibrateResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetPremiumCalibrateResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = new CarInsurance.GetPremiumCalibrateDetail();

                BigDecimal ci = new BigDecimal("0.0");
                BigDecimal bi = new BigDecimal("0.0");
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                for (ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail datum : result.data) {
                    datum.biBeginDateValue = parseMillisecondByShowDate(dateSdf, datum.biBeginDate);
                    datum.ciBeginDateValue = parseMillisecondByShowDate(dateSdf, datum.ciBeginDate);

                    for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : datum.coverageList) {
                        if (StringKit.isNumeric(insurancePolicyInfo.insuredPremium)) {
                            if (StringKit.equals(insurancePolicyInfo.coverageCode, "FORCEPREMIUM")) {
                                ci = ci.add(new BigDecimal(insurancePolicyInfo.insuredPremium));
                            } else {
                                bi = bi.add(new BigDecimal(insurancePolicyInfo.insuredPremium));
                            }
                        }
                    }
                }

                response.data.biInsuredPremium = bi.toString();
                response.data.ciInsuredPremium = ci.toString();
                response.data.ciInsuredPremiumText = "¥" + ci.toString();
                response.data.biInsuredPremiumText = "¥" + bi.toString();

                response.data.insurancePolicyPremiumDetails = result.data;

                str = json(BaseResponse.CODE_SUCCESS, "获取报价成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 申请核保
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String applyUnderwriting(ActionBean actionBean) {
//        CarInsurance.ApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ApplyUnderwritingRequest.class);
        CarInsurance.PremiumCalibrateAndApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.PremiumCalibrateAndApplyUnderwritingRequest.class);
        CarInsurance.ApplyUnderwritingResponse response = new CarInsurance.ApplyUnderwritingResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        if (request.applyUnderwriting.isNeedVerificationCode() && StringKit.isEmpty(request.applyUnderwriting.verificationCode)) {
            return json(BaseResponse.CODE_VERIFY_CODE, "缺少验证码", response);
        }

        ExtendCarInsurancePolicy.ApplyUnderwritingRequest applyUnderwritingRequest = new ExtendCarInsurancePolicy.ApplyUnderwritingRequest();

        // 非必传
        applyUnderwritingRequest.payType = request.applyUnderwriting.payType;
        applyUnderwritingRequest.applicantUrl = request.applyUnderwriting.applicantUrl;

        applyUnderwritingRequest.refereeMobile = request.applyUnderwriting.refereeMobile;

        if (request.applyUnderwriting.isNeedVerificationCode()) {
            applyUnderwritingRequest.verificationCode = request.applyUnderwriting.verificationCode;
        }

        applyUnderwritingRequest.channelCode = request.applyUnderwriting.channelCode;
        applyUnderwritingRequest.insurerCode = request.applyUnderwriting.insurerCode;
        applyUnderwritingRequest.bizID = request.applyUnderwriting.bizID;
        applyUnderwritingRequest.addresseeName = request.applyUnderwriting.addresseeName;
        applyUnderwritingRequest.addresseeMobile = request.applyUnderwriting.addresseeMobile;
        applyUnderwritingRequest.addresseeDetails = request.applyUnderwriting.addresseeDetails;
        applyUnderwritingRequest.policyEmail = request.applyUnderwriting.policyEmail;
        applyUnderwritingRequest.addresseeCounty = request.applyUnderwriting.addresseeCounty;
        applyUnderwritingRequest.addresseeCity = request.applyUnderwriting.addresseeCity;
        applyUnderwritingRequest.addresseeProvince = request.applyUnderwriting.addresseeProvince;

        ExtendCarInsurancePolicy.ApplyUnderwritingResponse result = new CarInsuranceHttpRequest<>(apply_underwriting, applyUnderwritingRequest, ExtendCarInsurancePolicy.ApplyUnderwritingResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.ApplyUnderwritingResponse();
            dealNullResponse(result);
        }

        String str;
        if (result.verify) {
            String applyState = "";
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                response.data.bjCodeFlag = request.applyUnderwriting.bjCodeFlag;
                applyState = getApplyUnderwritingState(response.data.synchFlag);
            } else {
                // 核保失败
                applyState = "2";
                if (response.data == null) {
                    response.data = new ExtendCarInsurancePolicy.ApplyUnderwriting();
                }
            }

            InsurancePolicyAndParticipantForCarInsurance insurancePolicyAndParticipantForCarInsurance = new InsurancePolicyAndParticipantForCarInsurance();
            String time = String.valueOf(System.currentTimeMillis());

            // TODO: 2018/4/11 记得获取这几个信息
            // TODO: 2018/4/8 利用保险公司简称代码，获取产品id与保险公司id
            // 代理人ID为null则为用户自主购买
            // agent_auuid;
            // 渠道ID为0则为用户自主购买
            // ditch_id;
            // 计划书ID为0则为用户自主购买
            // plan_id;
            // 产品ID
            // product_id;
            // 保险公司
            // ins_company_id;
            // 佣金 0表示未结算，1表示已结算
            // is_settlement;

            // FORCEPREMIUM 强险
            InsurancePolicyModel ciProposal = new InsurancePolicyModel();
            ciProposal.warranty_uuid = getThpBizID();
            ciProposal.pro_policy_no = result.data.ciProposalNo;
            ciProposal.premium = request.applyUnderwriting.ciInsuredPremium;
            ciProposal.start_time = request.applyUnderwriting.ciBeginDateValue;

            String ciEndDateValue = nextYearMillisecond(request.applyUnderwriting.ciBeginDateValue);
            if (StringKit.isEmpty(ciEndDateValue)) {
                ciEndDateValue = "null";
            }

            ciProposal.end_time = ciEndDateValue;
            ciProposal.account_uuid = actionBean.accountUuid;
            ciProposal.buyer_auuid = actionBean.loginUuid;
            ciProposal.count = "1";

            if (actionBean.userType == 4) {
                ciProposal.warranty_from = "2";
            } else {
                ciProposal.warranty_from = "1";
            }

            ciProposal.type = "3";
            ciProposal.check_status = applyState;
            ciProposal.pay_status = "0";
            ciProposal.warranty_status = "2";
            ciProposal.state = "1";
            ciProposal.created_at = time;
            ciProposal.updated_at = time;

            // TODO: 2018/4/11 以下数据测试用
            // agent_auuid;
            // ditch_id;
            // plan_id;
            ciProposal.product_id = "0";
            ciProposal.ins_company_id = "0";
            ciProposal.is_settlement = "0";

            insurancePolicyAndParticipantForCarInsurance.ciProposal = ciProposal;

            // 商业险
            InsurancePolicyModel biProposal = new InsurancePolicyModel();
            biProposal.warranty_uuid = getThpBizID();
            biProposal.pro_policy_no = result.data.biProposalNo;
            biProposal.premium = request.applyUnderwriting.biInsuredPremium;
            biProposal.start_time = request.applyUnderwriting.biBeginDateValue;

            String biEndDateValue = nextYearMillisecond(request.applyUnderwriting.biBeginDateValue);
            if (StringKit.isEmpty(biEndDateValue)) {
                biEndDateValue = "null";
            }

            biProposal.end_time = biEndDateValue;
            biProposal.account_uuid = actionBean.accountUuid;
            biProposal.buyer_auuid = actionBean.loginUuid;
            biProposal.count = "1";

            if (actionBean.userType == 4) {
                biProposal.warranty_from = "2";
            } else {
                biProposal.warranty_from = "1";
            }

            biProposal.type = "3";
            biProposal.check_status = applyState;
            biProposal.pay_status = "0";
            biProposal.warranty_status = "2";
            biProposal.state = "1";
            biProposal.created_at = time;
            biProposal.updated_at = time;

            // TODO: 2018/4/11 以下数据测试用
            // agent_auuid;
            // ditch_id;
            // plan_id;
            biProposal.product_id = "0";
            biProposal.ins_company_id = "0";
            biProposal.is_settlement = "0";

            insurancePolicyAndParticipantForCarInsurance.biProposal = biProposal;

            // 存保单车辆信息
            CarInfoModel biCarInfoModel = new CarInfoModel();
            biCarInfoModel.warranty_uuid = biProposal.warranty_uuid;
            biCarInfoModel.biz_id = response.data.bizID;
            biCarInfoModel.thp_biz_id = response.data.thpBizID;
            biCarInfoModel.insurance_type = "2";
            biCarInfoModel.car_code = request.premiumCalibrate.carInfo.licenseNo;
            biCarInfoModel.name = request.premiumCalibrate.personInfo.ownerName;
            biCarInfoModel.code = request.premiumCalibrate.personInfo.ownerID;
            biCarInfoModel.phone = request.premiumCalibrate.personInfo.ownerMobile;
            biCarInfoModel.frame_no = request.premiumCalibrate.carInfo.frameNo;
            biCarInfoModel.engine_no = request.premiumCalibrate.carInfo.engineNo;
            biCarInfoModel.vehicle_fgw_code = request.premiumCalibrate.carInfo.vehicleFgwCode;
            biCarInfoModel.vehicle_fgw_name = request.premiumCalibrate.carInfo.vehicleFgwName;
            biCarInfoModel.brand_code = request.premiumCalibrate.carInfo.brandCode;
            biCarInfoModel.is_not_car_code = StringKit.isEmpty(biCarInfoModel.car_code) ? "1" : "0";
            biCarInfoModel.is_trans = request.premiumCalibrate.carInfo.isTrans;
            biCarInfoModel.brand_name = request.premiumCalibrate.carInfo.brandName;
            biCarInfoModel.parent_veh_name = request.premiumCalibrate.carInfo.parentVehName;
            biCarInfoModel.engine_desc = request.premiumCalibrate.carInfo.engineDesc;
            biCarInfoModel.trans_date = request.premiumCalibrate.carInfo.transDateValue;
            biCarInfoModel.first_register_date = request.premiumCalibrate.carInfo.firstRegisterDateValue;
            biCarInfoModel.family_name = request.premiumCalibrate.carInfo.familyName;
            biCarInfoModel.gearbox_type = request.premiumCalibrate.carInfo.gearboxType;
            biCarInfoModel.car_remark = request.premiumCalibrate.carInfo.remark;
            biCarInfoModel.new_car_price = request.premiumCalibrate.carInfo.newCarPrice;
            biCarInfoModel.purchase_price_tax = request.premiumCalibrate.carInfo.purchasePriceTax;
            biCarInfoModel.import_flag = request.premiumCalibrate.carInfo.importFlag;
            biCarInfoModel.purchase_price = request.premiumCalibrate.carInfo.purchasePrice;
            biCarInfoModel.car_seat = request.premiumCalibrate.carInfo.seat;
            biCarInfoModel.standard_name = request.premiumCalibrate.carInfo.standardName;
            biCarInfoModel.coverage_list = JsonKit.bean2Json(request.premiumCalibrate.coverageList);
            biCarInfoModel.created_at = time;
            biCarInfoModel.updated_at = time;
            insurancePolicyAndParticipantForCarInsurance.biCarInfoModel = biCarInfoModel;

            CarInfoModel ciCarInfoModel = new CarInfoModel();
            ciCarInfoModel.warranty_uuid = ciProposal.warranty_uuid;
            ciCarInfoModel.biz_id = response.data.bizID;
            ciCarInfoModel.thp_biz_id = response.data.thpBizID;
            ciCarInfoModel.insurance_type = "1";
            ciCarInfoModel.car_code = request.premiumCalibrate.carInfo.licenseNo;
            ciCarInfoModel.name = request.premiumCalibrate.personInfo.ownerName;
            ciCarInfoModel.code = request.premiumCalibrate.personInfo.ownerID;
            ciCarInfoModel.phone = request.premiumCalibrate.personInfo.ownerMobile;
            ciCarInfoModel.frame_no = request.premiumCalibrate.carInfo.frameNo;
            ciCarInfoModel.engine_no = request.premiumCalibrate.carInfo.engineNo;
            ciCarInfoModel.vehicle_fgw_code = request.premiumCalibrate.carInfo.vehicleFgwCode;
            ciCarInfoModel.vehicle_fgw_name = request.premiumCalibrate.carInfo.vehicleFgwName;
            ciCarInfoModel.brand_code = request.premiumCalibrate.carInfo.brandCode;
            ciCarInfoModel.is_not_car_code = StringKit.isEmpty(biCarInfoModel.car_code) ? "1" : "0";
            ciCarInfoModel.is_trans = request.premiumCalibrate.carInfo.isTrans;
            ciCarInfoModel.brand_name = request.premiumCalibrate.carInfo.brandName;
            ciCarInfoModel.parent_veh_name = request.premiumCalibrate.carInfo.parentVehName;
            ciCarInfoModel.engine_desc = request.premiumCalibrate.carInfo.engineDesc;
            ciCarInfoModel.trans_date = request.premiumCalibrate.carInfo.transDateValue;
            ciCarInfoModel.first_register_date = request.premiumCalibrate.carInfo.firstRegisterDateValue;
            ciCarInfoModel.family_name = request.premiumCalibrate.carInfo.familyName;
            ciCarInfoModel.gearbox_type = request.premiumCalibrate.carInfo.gearboxType;
            ciCarInfoModel.car_remark = request.premiumCalibrate.carInfo.remark;
            ciCarInfoModel.new_car_price = request.premiumCalibrate.carInfo.newCarPrice;
            ciCarInfoModel.purchase_price_tax = request.premiumCalibrate.carInfo.purchasePriceTax;
            ciCarInfoModel.import_flag = request.premiumCalibrate.carInfo.importFlag;
            ciCarInfoModel.purchase_price = request.premiumCalibrate.carInfo.purchasePrice;
            ciCarInfoModel.car_seat = request.premiumCalibrate.carInfo.seat;
            ciCarInfoModel.standard_name = request.premiumCalibrate.carInfo.standardName;
            ciCarInfoModel.coverage_list = JsonKit.bean2Json(request.premiumCalibrate.coverageList);
            ciCarInfoModel.created_at = time;
            ciCarInfoModel.updated_at = time;
            insurancePolicyAndParticipantForCarInsurance.ciCarInfoModel = ciCarInfoModel;

            // 存保单人员信息
            // 被保险人
            String insuredAge = getAge(request.premiumCalibrate.personInfo.applicantBirthday);

            InsuranceParticipantModel ciInsured = new InsuranceParticipantModel();
            ciInsured.warranty_uuid = ciProposal.warranty_uuid;
            ciInsured.type = "2";
            ciInsured.relation_name = "1";
            ciInsured.name = request.premiumCalibrate.personInfo.applicantName;
            ciInsured.card_type = 1;
            ciInsured.card_code = request.premiumCalibrate.personInfo.applicantID;
            ciInsured.phone = request.premiumCalibrate.personInfo.applicantMobile;
            ciInsured.birthday = request.premiumCalibrate.personInfo.applicantBirthday;
            ciInsured.sex = request.premiumCalibrate.personInfo.applicantSex;
            ciInsured.age = insuredAge;
            ciInsured.start_time = request.applyUnderwriting.ciBeginDateValue;
            ciInsured.end_time = ciEndDateValue;
            ciInsured.created_at = time;
            ciInsured.updated_at = time;
            insurancePolicyAndParticipantForCarInsurance.ciInsured = ciInsured;

            InsuranceParticipantModel biInsured = new InsuranceParticipantModel();
            biInsured.warranty_uuid = biProposal.warranty_uuid;
            biInsured.type = "2";
            biInsured.relation_name = "1";
            biInsured.name = request.premiumCalibrate.personInfo.applicantName;
            biInsured.card_type = 1;
            biInsured.card_code = request.premiumCalibrate.personInfo.applicantID;
            biInsured.phone = request.premiumCalibrate.personInfo.applicantMobile;
            biInsured.birthday = request.premiumCalibrate.personInfo.applicantBirthday;
            biInsured.sex = request.premiumCalibrate.personInfo.applicantSex;
            biInsured.age = insuredAge;
            biInsured.start_time = request.applyUnderwriting.biBeginDateValue;
            biInsured.end_time = biEndDateValue;
            biInsured.created_at = time;
            biInsured.updated_at = time;
            insurancePolicyAndParticipantForCarInsurance.biInsured = biInsured;


            // 投保人
            String policyholderAge = getAge(request.premiumCalibrate.personInfo.insuredBirthday);

            InsuranceParticipantModel ciPolicyholder = new InsuranceParticipantModel();
            ciPolicyholder.warranty_uuid = ciProposal.warranty_uuid;
            ciPolicyholder.type = "1";
            ciPolicyholder.relation_name = "1";
            ciPolicyholder.name = request.premiumCalibrate.personInfo.insuredName;
            ciPolicyholder.card_type = 1;
            ciPolicyholder.card_code = request.premiumCalibrate.personInfo.insuredID;
            ciPolicyholder.phone = request.premiumCalibrate.personInfo.insuredMobile;
            ciPolicyholder.birthday = request.premiumCalibrate.personInfo.insuredBirthday;
            ciPolicyholder.sex = request.premiumCalibrate.personInfo.insuredSex;
            ciPolicyholder.age = policyholderAge;
            ciPolicyholder.start_time = request.applyUnderwriting.ciBeginDateValue;
            ciPolicyholder.end_time = ciEndDateValue;
            ciPolicyholder.created_at = time;
            ciPolicyholder.updated_at = time;
            insurancePolicyAndParticipantForCarInsurance.ciPolicyholder = ciPolicyholder;

            InsuranceParticipantModel biPolicyholder = new InsuranceParticipantModel();
            biPolicyholder.warranty_uuid = biProposal.warranty_uuid;
            biPolicyholder.type = "1";
            biPolicyholder.relation_name = "1";
            biPolicyholder.name = request.premiumCalibrate.personInfo.insuredName;
            biPolicyholder.card_type = 1;
            biPolicyholder.card_code = request.premiumCalibrate.personInfo.insuredID;
            biPolicyholder.phone = request.premiumCalibrate.personInfo.insuredMobile;
            biPolicyholder.birthday = request.premiumCalibrate.personInfo.insuredBirthday;
            biPolicyholder.sex = request.premiumCalibrate.personInfo.insuredSex;
            biPolicyholder.age = policyholderAge;
            biPolicyholder.start_time = request.applyUnderwriting.biBeginDateValue;
            biPolicyholder.end_time = biEndDateValue;
            biPolicyholder.created_at = time;
            biPolicyholder.updated_at = time;
            insurancePolicyAndParticipantForCarInsurance.biPolicyholder = biPolicyholder;

            int i = insurancePolicyDao.addInsurancePolicyAndParticipantForCarInsurance(insurancePolicyAndParticipantForCarInsurance);

            if (i > 0) {
                if (result.state == CarInsuranceResponse.RESULT_OK) {
                    str = json(BaseResponse.CODE_SUCCESS, "申请核保成功", response);
                } else {
                    str = json(BaseResponse.CODE_SUCCESS, "申请核保失败", response);
                }
            } else {
                str = json(BaseResponse.CODE_FAILURE, "申请核保失败", response);
            }

        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 将精准报价与申请核保合并为一个接口(投保接口){@link #getPremiumCalibrate}{@link #applyUnderwriting}
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求参数
     * @return 响应json
     */
    public String getPremiumCalibrateAndApplyUnderwriting(ActionBean actionBean) {
        CarInsurance.PremiumCalibrateAndApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.PremiumCalibrateAndApplyUnderwritingRequest.class);
        CarInsurance.PremiumCalibrateAndApplyUnderwritingResponse response = new CarInsurance.PremiumCalibrateAndApplyUnderwritingResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        actionBean.body = JsonKit.bean2Json(request.premiumCalibrate);

        String premiumCalibrate = getPremiumCalibrate(actionBean);
        CarInsurance.GetPremiumCalibrateResponse getPremiumCalibrateResponse = JsonKit.json2Bean(premiumCalibrate, CarInsurance.GetPremiumCalibrateResponse.class);

        if (getPremiumCalibrateResponse == null || getPremiumCalibrateResponse.code != BaseResponse.CODE_SUCCESS) {
            return premiumCalibrate;
        }

        request.applyUnderwriting.ciInsuredPremium = getPremiumCalibrateResponse.data.ciInsuredPremium;
        request.applyUnderwriting.biInsuredPremium = getPremiumCalibrateResponse.data.biInsuredPremium;

        if (getPremiumCalibrateResponse.data != null && !getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.isEmpty()) {
            // request.applyUnderwriting.bjCodeFlag = getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.get(0).;
            boolean flag = false;
            for (ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail insurancePolicyPremiumDetail : getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails) {
                if (StringKit.equals(insurancePolicyPremiumDetail.insurerCode, request.applyUnderwriting.insurerCode)) {
                    request.applyUnderwriting.bizID = insurancePolicyPremiumDetail.bizID;
                    request.applyUnderwriting.bjCodeFlag = insurancePolicyPremiumDetail.bjCodeFlag;
                    request.applyUnderwriting.channelCode = insurancePolicyPremiumDetail.channelCode;
                    request.applyUnderwriting.ciBeginDateValue = insurancePolicyPremiumDetail.ciBeginDateValue;
                    request.applyUnderwriting.biBeginDateValue = insurancePolicyPremiumDetail.biBeginDateValue;
                    flag = true;
                    break;
                }
            }

            if (flag) {
                response.data = new CarInsurance.PremiumCalibrateAndApplyUnderwriting();
                actionBean.body = JsonKit.bean2Json(request);
                CarInsurance.ApplyUnderwritingResponse applyUnderwritingResponse = JsonKit.json2Bean(applyUnderwriting(actionBean), CarInsurance.ApplyUnderwritingResponse.class);

                if (applyUnderwritingResponse != null) {
                    response.data.applyUnderwriting = applyUnderwritingResponse.data;
                }

                response.data.insurancePolicyPremiumDetails = getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails;

                return json(BaseResponse.CODE_SUCCESS, "申请核保成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "insurerCode参数有误", response);
            }
        } else {
            return premiumCalibrate;
        }
    }

    /**
     * 获取支付连接
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getPayLink(ActionBean actionBean) {
        CarInsurance.GetPayLinkRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetPayLinkRequest.class);
        CarInsurance.GetPayLinkResponse response = new CarInsurance.GetPayLinkResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetPayLinkRequest getPayLinkRequest = new ExtendCarInsurancePolicy.GetPayLinkRequest();

        getPayLinkRequest.bizID = request.bizID;

        // TODO: 2018/4/10 需要去数据库动态判断一下，是否需要先校验验证码

        ExtendCarInsurancePolicy.GetPayLinkResponse result = new CarInsuranceHttpRequest<>(get_pay_link, getPayLinkRequest, ExtendCarInsurancePolicy.GetPayLinkResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetPayLinkResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取支付链接成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 校验验证码
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String verifyPhoneCode(ActionBean actionBean) {
        CarInsurance.VerifyPhoneCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.VerifyPhoneCodeRequest.class);
        CarInsurance.VerifyPhoneCodeResponse response = new CarInsurance.VerifyPhoneCodeResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.VerifyPhoneCodeRequest verifyPhoneCodeRequest = new ExtendCarInsurancePolicy.VerifyPhoneCodeRequest();

        verifyPhoneCodeRequest.bizID = request.bizID;
        verifyPhoneCodeRequest.verificationCode = request.verificationCode;

        ExtendCarInsurancePolicy.VerifyPhoneCodeResponse result = new CarInsuranceHttpRequest<>(verify_phone_code, verifyPhoneCodeRequest, ExtendCarInsurancePolicy.VerifyPhoneCodeResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.VerifyPhoneCodeResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            String applyState = "";
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                applyState = getApplyUnderwritingState(response.data.synchFlag);
            } else {
                applyState = "1";
                response.data = new ExtendCarInsurancePolicy.PhoneCode();
            }

            int update = 1;
            UpdateInsurancePolicyProPolicyNoForCarInsurance insurance = new UpdateInsurancePolicyProPolicyNoForCarInsurance();
            insurance.bizId = request.bizID;
            insurance.biProposalNo = result.data.biProposalNo;
            insurance.ciProposalNo = result.data.ciProposalNo;
            insurance.check_status = applyState;
            update = insurancePolicyDao.updateInsurancePolicyProPolicyNoForCarInsurance(insurance);

            if (update > 0) {
                str = json(BaseResponse.CODE_SUCCESS, "验证成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, "验证失败", response);
            }

        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 申请发送验证码
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getPhoneVerifyCode(ActionBean actionBean) {
        CarInsurance.ReGetPhoneVerifyCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ReGetPhoneVerifyCodeRequest.class);
        CarInsurance.ReGetPhoneVerifyCodeResponse response = new CarInsurance.ReGetPhoneVerifyCodeResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest reGetPhoneVerifyCodeRequest = new ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest();

        reGetPhoneVerifyCodeRequest.bizID = request.bizID;

        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse result = new CarInsuranceHttpRequest<>(get_phone_verify_code, reGetPhoneVerifyCodeRequest, ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                str = json(BaseResponse.CODE_SUCCESS, "获取验证码成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 解析身份证信息
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String resolveIdentityCard(ActionBean actionBean) {
        CarInsurance.ResolveIdentityCardRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ResolveIdentityCardRequest.class);
        CarInsurance.ResolveIdentityCardResponse response = new CarInsurance.ResolveIdentityCardResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.ResolveIdentityCardRequest resolveIdentityCardRequest = new ExtendCarInsurancePolicy.ResolveIdentityCardRequest();

        if (!StringKit.isEmpty(request.frontCardUrl) || !StringKit.isEmpty(request.frontCardBase64)) {
            resolveIdentityCardRequest.frontCardUrl = request.frontCardUrl;
            resolveIdentityCardRequest.frontCardBase64 = request.frontCardBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少正面信息", new BaseResponse());
        }

        if (!StringKit.isEmpty(request.backCardUrl) || !StringKit.isEmpty(request.backCardBase64)) {
            resolveIdentityCardRequest.backCardUrl = request.backCardUrl;
            resolveIdentityCardRequest.backCardBase64 = request.backCardBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少背面信息", new BaseResponse());
        }

        ExtendCarInsurancePolicy.ResolveIdentityCardResponse result = new CarInsuranceHttpRequest<>(resolve_identity_card, resolveIdentityCardRequest, ExtendCarInsurancePolicy.ResolveIdentityCardResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.ResolveIdentityCardResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                // TODO: 2018/4/3 记得存咱们的服务器
                str = json(BaseResponse.CODE_SUCCESS, "身份证信息获取成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 解析行驶证信息
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String resolveDrivingLicense(ActionBean actionBean) {
        CarInsurance.ResolveDrivingLicenseRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ResolveDrivingLicenseRequest.class);
        CarInsurance.ResolveDrivingLicenseResponse response = new CarInsurance.ResolveDrivingLicenseResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.ResolveDrivingLicenseRequest resolveDrivingLicenseRequest = new ExtendCarInsurancePolicy.ResolveDrivingLicenseRequest();

        if (!StringKit.isEmpty(request.imgJustUrl) || !StringKit.isEmpty(request.imgJustBase64)) {
            resolveDrivingLicenseRequest.imgJustUrl = request.imgJustUrl;
            resolveDrivingLicenseRequest.imgJustBase64 = request.imgJustBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少正面信息", new BaseResponse());
        }

        if (!StringKit.isEmpty(request.imgBackUrl) || !StringKit.isEmpty(request.imgBackBase64)) {
            resolveDrivingLicenseRequest.imgBackUrl = request.imgBackUrl;
            resolveDrivingLicenseRequest.imgJustBase64 = request.imgJustBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少背面信息", new BaseResponse());
        }

        ExtendCarInsurancePolicy.ResolveDrivingLicenseResponse result = new CarInsuranceHttpRequest<>(resolve_driving_license, resolveDrivingLicenseRequest, ExtendCarInsurancePolicy.ResolveDrivingLicenseResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.ResolveDrivingLicenseResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                // TODO: 2018/4/3 记得存咱们的服务器
                str = json(BaseResponse.CODE_SUCCESS, "行驶证信息获取成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
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

    // ========================================================== 其他方法 ===========================================================

    /**
     * 车辆信息(结果处理方法){@link #getCarInfoByLicenceNumber} {@link #getCarInfoByFrameNumber}
     *
     * @param response 给我们的接口返回的response
     * @param result   第三方接口给我们返回的response
     * @return 给我们返回的json
     */
    private String dealResultAndResponse(CarInsurance.GetCarInfoResponse response, ExtendCarInsurancePolicy.GetCarInfoResponse result) {
        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetCarInfoResponse();
            dealNullResponse(result);
        }

        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                String frameNo = "null";
                if (result.data.frameNo != null) {
                    frameNo = result.data.frameNo;
                }

                String engineNo = "null";
                if (result.data.engineNo != null) {
                    engineNo = result.data.engineNo;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String calculateDateByShowDate = parseMillisecondByShowDate(sdf, result.data.firstRegisterDate);

                if (calculateDateByShowDate == null) {
                    result.data.firstRegisterDate = "";
                    result.data.firstRegisterDateValue = "";
                } else {
                    result.data.firstRegisterDateValue = calculateDateByShowDate;
                }

                String sign1 = SignatureTools.sign(frameNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                String sign2 = SignatureTools.sign(engineNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                response.signToken = sign1 + "*" + sign2;

                str = json(BaseResponse.CODE_SUCCESS, "获取车辆号码信息成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }
        return str;
    }

    /**
     * 将险别列表处理为可显示的列表
     *
     * @param data 需要处理的险别列表
     * @return 可显示的险别列表
     */
    private List<CarInsurance.InsuranceInfo> dealCoverageList(List<ExtendCarInsurancePolicy.InsuranceInfoDetail> data) {
        List<CarInsurance.InsuranceInfo> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < data.size(); i++) {
            ExtendCarInsurancePolicy.InsuranceInfo datum = data.get(i);
            if (datum.coverageCode.startsWith("M")) {
                map.put(datum.coverageCode.substring(1), datum.coverageCode);
                data.remove(i);
                i--;
            }
        }

        for (ExtendCarInsurancePolicy.InsuranceInfoDetail datum : data) {
            CarInsurance.InsuranceInfo insuranceInfo = new CarInsurance.InsuranceInfo();
            insuranceInfo.coverageCode = datum.coverageCode;
            insuranceInfo.coverageName = datum.coverageName;

            if (map.get(datum.coverageCode) != null) {
                insuranceInfo.hasExcessOption = "1";
            } else {
                insuranceInfo.hasExcessOption = "0";
            }

            if (!StringKit.isEmpty(datum.insuredAmount) && !StringKit.equals(datum.insuredAmount, "Y")) {
                insuranceInfo.insuredAmountList = Arrays.asList(datum.insuredAmount.split(","));
                if (insuranceInfo.insuredAmountList.isEmpty()) {
                    insuranceInfo.insuredAmount = "";
                } else {
                    insuranceInfo.insuredAmount = insuranceInfo.insuredAmountList.get(0);
                }
            } else {
                insuranceInfo.insuredAmount = datum.insuredAmount;
            }

            if (StringKit.equals(datum.coverageCode, "F")) {
                insuranceInfo.sourceOption = new ArrayList<>();
                insuranceInfo.sourceOption.add("进口");
                insuranceInfo.sourceOption.add("国产");
                insuranceInfo.flag = datum.flag;
            }

            if (StringKit.equals(datum.coverageCode, "Z2")) {

                if (StringKit.isEmpty(datum.flag)) {
                    insuranceInfo.day = "30";
                    insuranceInfo.amount = "50";
                } else {
                    String[] split = datum.flag.split(",");
                    if (split.length == 2) {
                        insuranceInfo.day = split[0];
                        insuranceInfo.amount = split[1];
                    } else {
                        insuranceInfo.day = "30";
                        insuranceInfo.amount = "50";
                    }
                }

                insuranceInfo.minDay = String.valueOf(Z2_MIN_DAY);
                insuranceInfo.maxDay = String.valueOf(Z2_MAX_DAY);
                insuranceInfo.minAmount = String.valueOf(Z2_MIN_AMOUNT);
                insuranceInfo.maxAmount = String.valueOf(Z2_MAX_AMOUNT);
                insuranceInfo.flag = insuranceInfo.day + "," + insuranceInfo.amount;
            }

            list.add(insuranceInfo);
        }

        return list;
    }

    /**
     * 处理响应码，如果返回null，则正常处理，如果返回非null，就需要返回以String为message的response
     *
     * @param signToken    验证signToken
     * @param notLicenseNo 是否未上牌
     * @param carInfo      车辆信息
     * @return 错误信息
     */
    private String dealCarInfoResponseNo(String signToken, boolean notLicenseNo, ExtendCarInsurancePolicy.CarInfo carInfo) {
        String[] split = signToken.split("\\*");

        boolean verify = false;
        if (split.length == 2) {
            boolean frameNo = SignatureTools.verify(carInfo.frameNo, split[0], SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);
            boolean engineNo = SignatureTools.verify(carInfo.engineNo, split[1], SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);
            verify = frameNo || engineNo;
        }

        if (!verify) {
            if (StringKit.isEmpty(carInfo.frameNo) || StringKit.equals(carInfo.frameNo, "null")) {
                return "请输入车架号";
            }

            if (StringKit.isEmpty(carInfo.engineNo) || StringKit.equals(carInfo.engineNo, "null")) {
                return "请输入发动机号";
            }

            carInfo.responseNo = "";
        }

        if (notLicenseNo) {
            carInfo.licenseNo = "新车";
            if (StringKit.isEmpty(carInfo.frameNo) || StringKit.equals(carInfo.frameNo, "null")) {
                return "请输入车架号";
            }
        }

        return null;
    }

    /**
     * 检查提交的险别列表是否合法
     *
     * @param source    正确的险别列表
     * @param checkList 提交的险别列表
     * @return 校验结果，{@link CheckCoverageListResult}
     */
    private CheckCoverageListResult checkCoverageList(List<CarInsurance.InsuranceInfo> source, List<CarInsurance.InsuranceInfo> checkList) {
        CheckCoverageListResult checkCoverageListResult = new CheckCoverageListResult();
        checkCoverageListResult.result = true;
        checkCoverageListResult.message = "";

        checkCoverageListResult.coverageList = new ArrayList<>();
        Map<String, CarInsurance.InsuranceInfo> map = new LinkedHashMap<>();

        for (CarInsurance.InsuranceInfo insuranceInfo : source) {
            map.put(insuranceInfo.coverageCode, insuranceInfo);
            if (StringKit.equals(insuranceInfo.hasExcessOption, "1")) {
                map.put("M" + insuranceInfo.coverageCode, insuranceInfo);
            }
        }


        for (CarInsurance.InsuranceInfo insuranceInfo : checkList) {
            // 校验提交的数据的选项是否符合规定
            CarInsurance.InsuranceInfo sourceInfo = map.get(insuranceInfo.coverageCode);
            ExtendCarInsurancePolicy.InsuranceInfoDetail insuranceInfoDetail = new ExtendCarInsurancePolicy.InsuranceInfoDetail();
            insuranceInfoDetail.coverageCode = insuranceInfo.coverageCode;

            // 是否可以不计免赔
            if (StringKit.equals(sourceInfo.hasExcessOption, "1")) {
                ExtendCarInsurancePolicy.InsuranceInfoDetail excess = new ExtendCarInsurancePolicy.InsuranceInfoDetail();

                if (StringKit.equals(insuranceInfo.isExcessOption, "1")) {
                    if (StringKit.equals(insuranceInfo.insuredAmount, "N")) {
                        checkCoverageListResult.result = false;
                        checkCoverageListResult.message = sourceInfo.coverageName + "主险未投保，不能仅投保不计免赔责任！";
                        checkCoverageListResult.coverageList = null;
                        break;
                    } else {
                        excess.coverageCode = "M" + insuranceInfo.coverageCode;
                        excess.insuredAmount = "Y";
                        checkCoverageListResult.coverageList.add(excess);
                    }
                } else {
                    excess.insuredAmount = "";
                }
            }

            // 保额是否符合规则
            List<String> insuredAmountList = sourceInfo.insuredAmountList;
            if (insuredAmountList != null && !insuredAmountList.isEmpty()) {
                boolean flag = StringKit.equals("N", insuranceInfo.insuredAmount);
                if (flag) {
                    insuranceInfoDetail.insuredAmount = "";
                } else {
                    for (String s : insuredAmountList) {
                        flag = StringKit.equals(s, insuranceInfo.insuredAmount);
                        if (flag) {
                            break;
                        }
                    }

                    if (flag) {
                        insuranceInfoDetail.insuredAmount = insuranceInfo.insuredAmount;
                    } else {
                        checkCoverageListResult.result = false;
                        checkCoverageListResult.message = sourceInfo.coverageName + "的保额非法";
                        checkCoverageListResult.coverageList = null;
                        break;
                    }
                }
            } else {
                if (StringKit.equals(insuranceInfo.insuredAmount, "N") || StringKit.equals(insuranceInfo.insuredAmount, "Y") || StringKit.isNumeric(insuranceInfo.insuredAmount)) {
                    if (StringKit.equals(insuranceInfo.insuredAmount, "N")) {
                        insuranceInfoDetail.insuredAmount = "";
                    } else {
                        insuranceInfoDetail.insuredAmount = insuranceInfo.insuredAmount;
                    }
                } else {
                    checkCoverageListResult.result = false;
                    checkCoverageListResult.message = sourceInfo.coverageName + "的保额必须是数字，Y或者N";
                    checkCoverageListResult.coverageList = null;
                    break;
                }
            }

            // 修理期间费用补偿险Z2，校验天数和每天的保额
            if (StringKit.equals(insuranceInfoDetail.coverageCode, "Z2") && StringKit.equals(insuranceInfoDetail.insuredAmount, "Y")) {
                if (StringKit.isInteger(insuranceInfo.day)) {
                    Integer integer = Integer.valueOf(insuranceInfo.day);
                    if (integer > Z2_MAX_DAY || integer < Z2_MIN_DAY) {
                        checkCoverageListResult.result = false;
                        checkCoverageListResult.message = sourceInfo.coverageName + "的天数非法";
                        checkCoverageListResult.coverageList = null;
                        break;
                    }
                } else {
                    checkCoverageListResult.result = false;
                    checkCoverageListResult.message = sourceInfo.coverageName + "的天数必须是正整数";
                    checkCoverageListResult.coverageList = null;
                    break;
                }

                if (StringKit.isNumeric(insuranceInfo.amount)) {
                    Double aDouble = Double.valueOf(insuranceInfo.amount);
                    if (aDouble > Z2_MAX_AMOUNT || aDouble < Z2_MIN_AMOUNT) {
                        checkCoverageListResult.result = false;
                        checkCoverageListResult.message = sourceInfo.coverageName + "的保额非法";
                        checkCoverageListResult.coverageList = null;
                        break;
                    }
                } else {
                    checkCoverageListResult.result = false;
                    checkCoverageListResult.message = sourceInfo.coverageName + "的保额必须是数字";
                    checkCoverageListResult.coverageList = null;
                    break;
                }

                insuranceInfoDetail.flag = insuranceInfo.day + "," + insuranceInfo.amount;
            }

            // 玻璃险破碎险F，校验是否国产、进口
            if (StringKit.equals(insuranceInfoDetail.coverageCode, "F") && StringKit.equals(insuranceInfoDetail.insuredAmount, "Y")) {
                boolean flag = false;
                for (String s : insuranceInfo.sourceOption) {
                    flag = StringKit.equals(s, insuranceInfo.source);
                    if (flag) {
                        break;
                    }
                }

                if (flag) {
                    insuranceInfoDetail.flag = checkCoverageListResult.getFType(insuranceInfo.source);
                } else {
                    checkCoverageListResult.result = false;
                    checkCoverageListResult.message = sourceInfo.coverageName + "的保额必须是数字";
                    checkCoverageListResult.coverageList = null;
                    break;
                }
            }

            if (!StringKit.equals(insuranceInfo.insuredAmount, "N")) {
                checkCoverageListResult.coverageList.add(insuranceInfoDetail);
            }
        }

        return checkCoverageListResult;
    }

    // 维修期间补偿险，最小投保天数
    private static final int Z2_MIN_DAY = 1;
    // 维修期间补偿险，最小大投保天数
    private static final int Z2_MAX_DAY = 90;
    // 维修期间补偿险，最低保额
    private static final double Z2_MIN_AMOUNT = 50;
    // 维修期间补偿险，最高保额
    private static final double Z2_MAX_AMOUNT = 500;

    private static class CheckCoverageListResult {

        // 是否有效
        public boolean result;

        // 错误信息
        String message;

        // 校验正常后的实际提交险别列表
        List<ExtendCarInsurancePolicy.InsuranceInfoDetail> coverageList;

        // 获取玻璃险的对应type，只有确定玻璃险提交数据正确才有意义
        String getFType(String text) {
            switch (text) {
                case "国产":
                    return "1";
                case "进口":
                    return "2";
                default:
                    return "";
            }
        }
    }

    /**
     * 格式化时间戳用
     *
     * @param sdf      格式
     * @param showDate 时间
     * @return showDate指定sdf的格式
     */
    private String parseMillisecondByShowDate(SimpleDateFormat sdf, String showDate) {
        if (!StringKit.isEmpty(showDate)) {
            try {
                Date parse = sdf.parse(showDate);
                return String.valueOf(parse.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return "";
        }
    }

    /**
     * 获取明年当天的00：00：00的时间戳
     *
     * @param time 时间戳
     * @return 明年当天的00：00：00的时间戳
     */
    private String nextYearMillisecond(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(Long.valueOf(time));
        String[] split = format.split("-");
        if (split.length == 3) {
            Integer integer = Integer.valueOf(split[0]);
            integer += 1;
            String date = integer + "-" + split[1] + "-" + split[2];
            return parseMillisecondByShowDate(sdf, date);
        } else {
            return null;
        }
    }

    /**
     * 获取年龄
     *
     * @param birth 生日的时间戳
     * @return 年龄
     */
    private String getAge(String birth) {
        Date current = new Date(System.currentTimeMillis());
        Date birthday = new Date(Long.valueOf(birth));
        int year = current.getYear() - birthday.getYear();
        if (current.getMonth() < birthday.getMonth()) {
            year -= 1;
        } else if (current.getMonth() == birthday.getMonth()) {
            if (current.getDay() < birthday.getDay()) {
                year -= 1;
            }
        }
        return String.valueOf(year);
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
        return String.valueOf(WarrantyUuidWorker.getWorker(2, 1).nextId());
    }

    /**
     * 处理响应为null的情况
     *
     * @param carInsuranceResponse 需要初始化的response
     */
    private void dealNullResponse(CarInsuranceResponse carInsuranceResponse) {
        carInsuranceResponse.state = CarInsuranceResponse.RESULT_FAIL;
        carInsuranceResponse.msg = "解析错误";
        carInsuranceResponse.msgCode = "INSCHOS-1";
        carInsuranceResponse.verify = false;
    }

    /**
     * 获取核保状态
     *
     * @param mSynchFlag 核保状态
     * @return 核保状态
     */
    private String getApplyUnderwritingState(String mSynchFlag) {
        String applyState = "";
        if (StringKit.isInteger(mSynchFlag)) {
            int synchFlag = Integer.valueOf(mSynchFlag);
            if (synchFlag == 0) {
                applyState = "3";
            } else if (synchFlag == 1) {
                applyState = "1";
            } else {
                applyState = "synchFlag = " + synchFlag;
            }
        }
        return applyState;
    }


}
