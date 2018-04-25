package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.CarInsurance;
import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.access.rpc.service.BrokerageService;
import com.inschos.cloud.trading.access.rpc.service.CustWarrantyService;
import com.inschos.cloud.trading.access.rpc.service.PremiumService;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.*;
import com.inschos.cloud.trading.data.dao.CarInfoDao;
import com.inschos.cloud.trading.data.dao.CarRecordDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.extend.car.*;
import com.inschos.cloud.trading.extend.file.FileUpload;
import com.inschos.cloud.trading.model.*;
import com.inschos.cloud.trading.model.fordao.*;
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
                            ExtendCarInsurancePolicy.CityCode cityCode = new ExtendCarInsurancePolicy.CityCode();
                            cityCode.cityName = "";
                            cityCode.cityCode = "";
                            cityCode.countyList = new ArrayList<>();

                            ExtendCarInsurancePolicy.AreaCode areaCode = new ExtendCarInsurancePolicy.AreaCode();
                            areaCode.countyName = "";
                            areaCode.countyCode = "";
                            cityCode.countyList.add(areaCode);

                            response.data.get(i).city.add(cityCode);
                        }

                        str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);
                        if (request == null) {
                            str = dealFieldName("1", str);
                        } else {
                            str = dealFieldName(request.type, str);
                        }

                    } else {
                        str = json(BaseResponse.CODE_FAILURE, "获取省级列表失败", response);
                    }
                } else {
                    str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);
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
                str = dealFieldName(request.type, str);
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

                String frameNo = SignatureTools.sign(result.data.frameNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                String engineNo = SignatureTools.sign(result.data.engineNo, SignatureTools.SIGN_CAR_RSA_PRIVATE_KEY);
                response.signToken = frameNo + "*" + engineNo;
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

                // TODO: 2018/4/25 先获取manager_uuid
//                List<ProductInfo> productInfos = productClient.listProduct();
//
//                if (productInfos != null && !productInfos.isEmpty() && response.data != null && !response.data.isEmpty()) {
//                    Set<ProductInfo> hashSet = new HashSet<>(productInfos);
//                    Map<String, ProductInfo> hashMap = new HashMap<>();
//                    for (ProductInfo productInfo : hashSet) {
//                        if (StringKit.equals(productInfo.sell_status, "1")) {
//                            hashMap.put(productInfo.code, productInfo);
//                        }
//                    }
//
//                    List<ExtendCarInsurancePolicy.InsuranceCompany> list = new ArrayList<>();
//                    for (ExtendCarInsurancePolicy.InsuranceCompany datum : response.data) {
//                        ProductInfo productInfo = hashMap.get(datum.insurerCode);
//                        if (productInfo != null) {
//                            datum.productId = productInfo.id;
//                            list.add(datum);
//                        }
//                    }
//
//                    response.data.clear();
//                    response.data.addAll(list);
//
//                } else {
//                    if (response.data == null) {
//                        response.data = new ArrayList<>();
//                    }
//                    response.data.clear();
//                }

            } else {
                if (StringKit.equals(CarInsuranceResponse.ERROR_SI100100000063, result.msgCode)) {
                    response.data = new ArrayList<>();
                    str = json(BaseResponse.CODE_SUCCESS, "获取保险公司成功", response);
                } else {
                    str = json(BaseResponse.CODE_FAILURE, result.msg + "（" + result.msgCode + "）", response);
                }
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

        long current = System.currentTimeMillis();
        Long biBeginDate = formatterDate(request.biBeginDateValue);
        if (biBeginDate == null || current >= biBeginDate) {
            return json(BaseResponse.CODE_FAILURE, "商业险起保日期错误或时间早于" + sdf.format(new Date(current + 24 * 60 * 60 * 1000)), response);
        }

        getPremiumCalibrateRequest.biBeginDate = sdf.format(new Date(biBeginDate));

        Long ciBeginDate = formatterDate(request.ciBeginDateValue);
        if (ciBeginDate == null || current >= biBeginDate) {
            return json(BaseResponse.CODE_FAILURE, "强险起保日期错误或时间早于" + sdf.format(new Date(current + 24 * 60 * 60 * 1000)), response);
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

//        if (notLicenseNo) {
//            // 新车备案要用到的-来历凭证编号
//            if (StringKit.isEmpty(request.carInfo.sourceCertificateNo)) {
//                return json(BaseResponse.CODE_FAILURE, "缺少来历凭证编号", response);
//            }
//        }

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
    private String applyUnderwriting(ActionBean actionBean) {
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

        if (!StringKit.isEmpty(request.applyUnderwriting.verificationCode)) {
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
            String applyState;
            String payState = "";
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                response.data.bjCodeFlag = request.applyUnderwriting.bjCodeFlag;
                applyState = getApplyUnderwritingState(response.data.synchFlag);

                if (StringKit.equals(applyState, InsurancePolicyModel.APPLY_UNDERWRITING_SUCCESS)) {
                    payState = CustWarrantyCostModel.PAY_STATUS_WAIT;
                } else if (StringKit.equals(applyState, InsurancePolicyModel.APPLY_UNDERWRITING_PROCESSING)) {
                    payState = CustWarrantyCostModel.APPLY_UNDERWRITING_PROCESSING;
                }

            } else {
                // 核保失败
                payState = CustWarrantyCostModel.APPLY_UNDERWRITING_FAILURE;
                if (response.data == null) {
                    response.data = new ExtendCarInsurancePolicy.ApplyUnderwriting();
                }
            }

            // TODO: 2018/4/25 先获取manager_uuid
            List<ProductInfo> productInfos = productClient.listProduct();
            Map<String, ProductInfo> hashMap = new HashMap<>();

            if (productInfos != null && !productInfos.isEmpty()) {
                Set<ProductInfo> hashSet = new HashSet<>(productInfos);
                for (ProductInfo productInfo : hashSet) {
                    if (StringKit.equals(productInfo.sell_status, "1")) {
                        hashMap.put(productInfo.code, productInfo);
                    }
                }
            }

            ProductInfo productInfo = hashMap.get(request.premiumCalibrate.insurerCode);
            productInfo = new ProductInfo();
            productInfo.manager_uuid = "1";
            productInfo.id = "1";
            productInfo.product_company_id = "1";

            String warrantyStatus = InsurancePolicyModel.POLICY_STATUS_PENDING;

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

            // FORCEPREMIUM 强险
            if (!StringKit.isEmpty(result.data.ciProposalNo)) {
                InsurancePolicyModel ciProposal = new InsurancePolicyModel();
                ciProposal.warranty_uuid = getThpBizID();
                ciProposal.pre_policy_no = result.data.ciProposalNo;
                ciProposal.start_time = request.applyUnderwriting.ciBeginDateValue;

                String ciEndDateValue = nextYearMillisecond(request.applyUnderwriting.ciBeginDateValue);
                if (StringKit.isEmpty(ciEndDateValue)) {
                    ciEndDateValue = "null";
                }

                ciProposal.end_time = ciEndDateValue;
                ciProposal.manager_uuid = productInfo.manager_uuid;
                ciProposal.account_uuid = actionBean.accountUuid;
                ciProposal.count = "1";

                if (actionBean.userType == 4) {
                    ciProposal.warranty_from = InsurancePolicyModel.SOURCE_ONLINE;
                } else {
                    ciProposal.warranty_from = InsurancePolicyModel.SOURCE_SELF;
                }

                ciProposal.type = InsurancePolicyModel.POLICY_TYPE_CAR;
                ciProposal.warranty_status = warrantyStatus;
                ciProposal.by_stages_way = "趸缴";
                ciProposal.integral = "0";
                ciProposal.state = "1";
                ciProposal.created_at = time;
                ciProposal.updated_at = time;

                // TODO: 2018/4/11 以下数据测试用
                // agent_auuid;
                // ditch_id;
                // plan_id;
                ciProposal.product_id = productInfo.id;
                ciProposal.ins_company_id = productInfo.product_company_id;
                ciProposal.is_settlement = "1";

                insurancePolicyAndParticipantForCarInsurance.ciProposal = ciProposal;

                // 存支付信息
                CustWarrantyCostModel ciCustWarrantyCostModel = new CustWarrantyCostModel();
                ciCustWarrantyCostModel.warranty_uuid = ciProposal.warranty_uuid;
                ciCustWarrantyCostModel.pay_time = ciProposal.start_time;
                ciCustWarrantyCostModel.premium = request.applyUnderwriting.ciInsuredPremium;
                ciCustWarrantyCostModel.pay_status = payState;
                ciCustWarrantyCostModel.created_at = time;
                ciCustWarrantyCostModel.updated_at = time;

                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCostModel = ciCustWarrantyCostModel;

                insurancePolicyAndParticipantForCarInsurance.ciCarInfoModel = new CarInfoModel(ciProposal.warranty_uuid,
                        response.data.bizID,
                        response.data.thpBizID,
                        CarInfoModel.INSURANCE_TYPE_STRONG,
                        time, JsonKit.bean2Json(request.applyUnderwriting.coverageList),
                        JsonKit.bean2Json(request.applyUnderwriting.spAgreements),
                        response.data.bjCodeFlag,
                        request.premiumCalibrate.carInfo,
                        request.premiumCalibrate.personInfo);

                // 被保险人
                insurancePolicyAndParticipantForCarInsurance.ciInsured = new InsuranceParticipantModel(
                        ciProposal.warranty_uuid,
                        InsuranceParticipantModel.TYPE_INSURED,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        ciEndDateValue,
                        request.premiumCalibrate.personInfo);

                // 投保人
                insurancePolicyAndParticipantForCarInsurance.ciPolicyholder = new InsuranceParticipantModel(
                        ciProposal.warranty_uuid,
                        InsuranceParticipantModel.TYPE_POLICYHOLDER,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        ciEndDateValue,
                        request.premiumCalibrate.personInfo);
            }

            if (!StringKit.isEmpty(result.data.biProposalNo)) {
                // 商业险
                InsurancePolicyModel biProposal = new InsurancePolicyModel();
                biProposal.warranty_uuid = getThpBizID();
                biProposal.pre_policy_no = result.data.biProposalNo;
                biProposal.start_time = request.applyUnderwriting.biBeginDateValue;

                String biEndDateValue = nextYearMillisecond(request.applyUnderwriting.biBeginDateValue);
                if (StringKit.isEmpty(biEndDateValue)) {
                    biEndDateValue = "null";
                }

                biProposal.end_time = biEndDateValue;
                biProposal.manager_uuid = productInfo.manager_uuid;
                biProposal.account_uuid = actionBean.accountUuid;
                biProposal.count = "1";

                if (actionBean.userType == 4) {
                    biProposal.warranty_from = InsurancePolicyModel.SOURCE_ONLINE;
                } else {
                    biProposal.warranty_from = InsurancePolicyModel.SOURCE_SELF;
                }

                biProposal.type = InsurancePolicyModel.POLICY_TYPE_CAR;
                biProposal.warranty_status = warrantyStatus;
                biProposal.by_stages_way = "趸缴";
                biProposal.integral = request.applyUnderwriting.integral;
                biProposal.state = "1";
                biProposal.created_at = time;
                biProposal.updated_at = time;

                // TODO: 2018/4/11 以下数据测试用
                // agent_auuid;
                // ditch_id;
                // plan_id;
                biProposal.product_id = productInfo.id;
                biProposal.ins_company_id = productInfo.product_company_id;
                biProposal.is_settlement = "1";

                insurancePolicyAndParticipantForCarInsurance.biProposal = biProposal;

                CustWarrantyCostModel biCustWarrantyCostModel = new CustWarrantyCostModel();
                biCustWarrantyCostModel.warranty_uuid = biProposal.warranty_uuid;
                biCustWarrantyCostModel.pay_time = biProposal.start_time;
                biCustWarrantyCostModel.premium = request.applyUnderwriting.biInsuredPremium;
                biCustWarrantyCostModel.pay_status = payState;
                biCustWarrantyCostModel.created_at = time;
                biCustWarrantyCostModel.updated_at = time;

                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCostModel = biCustWarrantyCostModel;

                // 存保单车辆信息
                insurancePolicyAndParticipantForCarInsurance.biCarInfoModel = new CarInfoModel(biProposal.warranty_uuid,
                        response.data.bizID,
                        response.data.thpBizID,
                        CarInfoModel.INSURANCE_TYPE_COMMERCIAL,
                        time, JsonKit.bean2Json(request.applyUnderwriting.coverageList),
                        JsonKit.bean2Json(request.applyUnderwriting.spAgreements),
                        response.data.bjCodeFlag,
                        request.premiumCalibrate.carInfo,
                        request.premiumCalibrate.personInfo);

                // 存保单人员信息
                insurancePolicyAndParticipantForCarInsurance.biInsured = new InsuranceParticipantModel(
                        biProposal.warranty_uuid,
                        InsuranceParticipantModel.TYPE_INSURED,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        biEndDateValue,
                        request.premiumCalibrate.personInfo);


                insurancePolicyAndParticipantForCarInsurance.biPolicyholder = new InsuranceParticipantModel(
                        biProposal.warranty_uuid,
                        InsuranceParticipantModel.TYPE_POLICYHOLDER,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        biEndDateValue,
                        request.premiumCalibrate.personInfo);
            }

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

        if (getPremiumCalibrateResponse.data != null && !getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.isEmpty()) {
            request.applyUnderwriting.ciInsuredPremium = getPremiumCalibrateResponse.data.ciInsuredPremium;
            request.applyUnderwriting.biInsuredPremium = getPremiumCalibrateResponse.data.biInsuredPremium;
            // request.applyUnderwriting.bjCodeFlag = getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.get(0).;
            boolean flag = false;
            for (ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail insurancePolicyPremiumDetail : getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails) {
                if (StringKit.equals(insurancePolicyPremiumDetail.insurerCode, request.applyUnderwriting.insurerCode)) {
                    request.applyUnderwriting.bizID = insurancePolicyPremiumDetail.bizID;
                    request.applyUnderwriting.bjCodeFlag = insurancePolicyPremiumDetail.bjCodeFlag;
                    request.applyUnderwriting.channelCode = insurancePolicyPremiumDetail.channelCode;
                    request.applyUnderwriting.ciBeginDateValue = insurancePolicyPremiumDetail.ciBeginDateValue;
                    request.applyUnderwriting.biBeginDateValue = insurancePolicyPremiumDetail.biBeginDateValue;
                    request.applyUnderwriting.coverageList = insurancePolicyPremiumDetail.coverageList;
                    request.applyUnderwriting.spAgreements = insurancePolicyPremiumDetail.spAgreement;
                    request.applyUnderwriting.integral = insurancePolicyPremiumDetail.integral;
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
                if (response.data == null || StringKit.isEmpty(response.data.payLink)) {
                    UpdateInsurancePolicyStatusAndWarrantyCodeForCarInsurance updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance = new UpdateInsurancePolicyStatusAndWarrantyCodeForCarInsurance();
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.bizId = request.bizID;
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status = CustWarrantyCostModel.PAY_STATUS_CANCEL;
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.payMoney = "0.00";
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo = "";
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo = "";
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time = String.valueOf(System.currentTimeMillis());
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status = InsurancePolicyModel.POLICY_STATUS_INVALID;
                    int update = insurancePolicyDao.updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance);

                    if (update <= 0) {
                        return json(BaseResponse.CODE_FAILURE, "获取支付链接失败", response);
                    }
                }
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
            String warrantyStatus = "";
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                String applyState = getApplyUnderwritingState(response.data.synchFlag);
                if (StringKit.equals(applyState, InsurancePolicyModel.APPLY_UNDERWRITING_SUCCESS)) {
                    warrantyStatus = CustWarrantyCostModel.PAY_STATUS_WAIT;
                } else if (StringKit.equals(applyState, InsurancePolicyModel.APPLY_UNDERWRITING_PROCESSING)) {
                    warrantyStatus = CustWarrantyCostModel.APPLY_UNDERWRITING_PROCESSING;
                }
            } else {
                warrantyStatus = InsurancePolicyModel.POLICY_STATUS_INVALID;
                response.data = new ExtendCarInsurancePolicy.PhoneCode();
            }

            UpdateInsurancePolicyProPolicyNoForCarInsurance insurance = new UpdateInsurancePolicyProPolicyNoForCarInsurance();
            insurance.bizId = request.bizID;
            insurance.biProposalNo = result.data.biProposalNo;
            insurance.ciProposalNo = result.data.ciProposalNo;
            insurance.warrantyStatus = warrantyStatus;
            int update = insurancePolicyDao.updateInsurancePolicyProPolicyNoForCarInsurance(insurance);

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
                if (!StringKit.isEmpty(request.frontCardBase64) && result.data != null) {
                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
                    request1.base64 = request.frontCardBase64;
                    request1.fileKey = MD5Kit.MD5Digest(result.data.cardNo + result.data.name + "1");
                    request1.fileName = MD5Kit.MD5Digest(result.data.cardNo + "1");
                    FileUpload.getInstance().uploadByBase64(request1);
                }

                if (!StringKit.isEmpty(request.backCardBase64) && result.data != null) {
                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
                    request1.base64 = request.backCardBase64;
                    request1.fileKey = MD5Kit.MD5Digest(result.data.cardNo + result.data.name + "2");
                    request1.fileName = MD5Kit.MD5Digest(result.data.cardNo + "2");
                    FileUpload.getInstance().uploadByBase64(request1);
                }
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
            resolveDrivingLicenseRequest.imgBackBase64 = request.imgBackBase64;
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
                if (!StringKit.isEmpty(request.imgJustBase64) && result.data != null) {
                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
                    request1.base64 = request.imgJustBase64;
                    request1.fileKey = MD5Kit.MD5Digest(result.data.engineNo + result.data.frameNo + result.data.fileNumber + "1");
                    request1.fileName = result.data.fileNumber + "1";
                    FileUpload.getInstance().uploadByBase64(request1);
                }

                if (!StringKit.isEmpty(request.imgBackBase64) && result.data != null) {
                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
                    request1.base64 = request.imgBackBase64;
                    request1.fileKey = MD5Kit.MD5Digest(result.data.engineNo + result.data.frameNo + result.data.fileNumber + "2");
                    request1.fileName = result.data.fileNumber + "2";
                    FileUpload.getInstance().uploadByBase64(request1);
                }
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
     * 回调接口：获取保单核保结果
     * FINISH: 2018/4/14
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String sendApplyUnderwritingResult(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetApplyUnderwritingResultRequest request = JsonKit.json2Bean(actionBean.body, ExtendCarInsurancePolicy.GetApplyUnderwritingResultRequest.class);
        ExtendCarInsurancePolicy.GetApplyUnderwritingResultResponse response = new ExtendCarInsurancePolicy.GetApplyUnderwritingResultResponse();

        String s = dealCallBackRequestError(request);
        if (!StringKit.isEmpty(s)) {
            return s;
        }

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", "error_1", response);
        }

        String applyState = StringKit.equals(request.state, "1") ? InsurancePolicyModel.APPLY_UNDERWRITING_SUCCESS : InsurancePolicyModel.APPLY_UNDERWRITING_FAILURE;

        UpdateInsurancePolicyStatusForCarInsurance insurance = new UpdateInsurancePolicyStatusForCarInsurance();

        String payState = "";
        String warrantyStatus = "";
        if (StringKit.equals(applyState, InsurancePolicyModel.APPLY_UNDERWRITING_SUCCESS)) {
            payState = CustWarrantyCostModel.PAY_STATUS_WAIT;
            warrantyStatus = InsurancePolicyModel.POLICY_STATUS_PENDING;
        } else if (StringKit.equals(applyState, InsurancePolicyModel.APPLY_UNDERWRITING_PROCESSING)) {
            payState = CustWarrantyCostModel.PAY_STATUS_WAIT;
            warrantyStatus = InsurancePolicyModel.POLICY_STATUS_PENDING;
        } else if (StringKit.equals(applyState, InsurancePolicyModel.APPLY_UNDERWRITING_FAILURE)) {
            payState = CustWarrantyCostModel.PAY_STATUS_CANCEL;
            warrantyStatus = InsurancePolicyModel.POLICY_STATUS_INVALID;
        }

        insurance.pay_status = payState;
        insurance.warranty_status = warrantyStatus;
        insurance.biProposalNo = request.data.biProposalNo;
        insurance.ciProposalNo = request.data.ciProposalNo;
        insurance.bizId = request.data.bizID;
        insurance.thpBizID = request.data.thpBizID;

        if (!StringKit.isEmpty(request.data.bizID) || !StringKit.isEmpty(request.data.thpBizID)) {
            int update = insurancePolicyDao.updateInsurancePolicyStatusForCarInsurance(insurance);
            dealCallBackParamsIllegal(update, response);
        } else {
            response.msg = "未返回bizID或thpBizID";
            response.state = String.valueOf(CarInsuranceResponse.RESULT_FAIL);
            response.msgCode = "error_4";
        }

        return JsonKit.bean2Json(response);
    }

    /**
     * 回调接口：获取保单的付款信息
     * FINISH: 2018/4/14
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String sendInsurancePolicy(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetInsurancePolicyRequest request = JsonKit.json2Bean(actionBean.body, ExtendCarInsurancePolicy.GetInsurancePolicyRequest.class);
        ExtendCarInsurancePolicy.GetInsurancePolicyResponse response = new ExtendCarInsurancePolicy.GetInsurancePolicyResponse();

        String s = dealCallBackRequestError(request);
        if (!StringKit.isEmpty(s)) {
            return s;
        }

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", "error_1", response);
        }

        if (!StringKit.isEmpty(request.data.bizID) || !StringKit.isEmpty(request.data.thpBizID)) {
            UpdateInsurancePolicyStatusAndWarrantyCodeForCarInsurance updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance = new UpdateInsurancePolicyStatusAndWarrantyCodeForCarInsurance();
            updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.bizId = request.data.bizID;
            updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.thpBizID = request.data.thpBizID;

            if (StringKit.equals(request.data.payState, "0")) {
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status = CustWarrantyCostModel.PAY_STATUS_CANCEL;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.payMoney = "0.00";
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo = "";
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo = "";
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time = String.valueOf(System.currentTimeMillis());
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status = InsurancePolicyModel.POLICY_STATUS_INVALID;
            } else {
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status = CustWarrantyCostModel.PAY_STATUS_SUCCESS;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.payMoney = request.data.payMoney;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo = request.data.ciPolicyNo;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo = request.data.biPolicyNo;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time = parseMillisecondByShowDate(sdf, request.data.payTime);
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status = InsurancePolicyModel.POLICY_STATUS_WAITING;
            }

            int update = insurancePolicyDao.updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance);

            dealCallBackParamsIllegal(update, response);

        } else {
            response.msg = "未返回bizID或thpBizID";
            response.state = String.valueOf(CarInsuranceResponse.RESULT_FAIL);
            response.msgCode = "error_4";
        }

        return JsonKit.bean2Json(response);
    }

    /**
     * 回调接口：获取保单的配送信息
     * FINISH: 2018/4/14
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String sendExpressInfo(ActionBean actionBean) {
        ExtendCarInsurancePolicy.GetExpressInfoRequest request = JsonKit.json2Bean(actionBean.body, ExtendCarInsurancePolicy.GetExpressInfoRequest.class);
        ExtendCarInsurancePolicy.GetExpressInfoResponse response = new ExtendCarInsurancePolicy.GetExpressInfoResponse();

        String s = dealCallBackRequestError(request);
        if (!StringKit.isEmpty(s)) {
            return s;
        }

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", "error_1", response);
        }

        UpdateInsurancePolicyExpressInfoForCarInsurance updateInsurancePolicyExpressInfoForCarInsurance = new UpdateInsurancePolicyExpressInfoForCarInsurance();

        updateInsurancePolicyExpressInfoForCarInsurance.bizId = request.data.bizID;
        updateInsurancePolicyExpressInfoForCarInsurance.thpBizID = request.data.thpBizID;
        updateInsurancePolicyExpressInfoForCarInsurance.expressNo = request.data.expressNo;
        updateInsurancePolicyExpressInfoForCarInsurance.expressCompanyName = request.data.expressCompanyName;
        updateInsurancePolicyExpressInfoForCarInsurance.deliveryType = request.data.deliveryType;
        updateInsurancePolicyExpressInfoForCarInsurance.deliveryType = request.data.addresseeDetails;
        updateInsurancePolicyExpressInfoForCarInsurance.deliveryType = request.data.addresseeProvince;
        updateInsurancePolicyExpressInfoForCarInsurance.deliveryType = request.data.addresseeCity;
        updateInsurancePolicyExpressInfoForCarInsurance.deliveryType = request.data.addresseeCounty;

        int update = insurancePolicyDao.updateInsurancePolicyExpressInfoForCarInsurance(updateInsurancePolicyExpressInfoForCarInsurance);

        return JsonKit.bean2Json(dealCallBackParamsIllegal(update, response));
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
     * 处理回调接口，request的参数缺失
     *
     * @param request 请求体
     * @return 校验结果
     */
    private String dealCallBackRequestError(CallBackCarInsuranceRequest request) {
        if (request == null) {
            CallBackCarInsuranceResponse response = new CallBackCarInsuranceResponse();
            response.msg = "解析错误";
            response.state = String.valueOf(CarInsuranceResponse.RESULT_FAIL);
            response.msgCode = "error_1";
            return JsonKit.bean2Json(response);
        }

        if (request.data == null) {
            CallBackCarInsuranceResponse response = new CallBackCarInsuranceResponse();
            response.msg = "缺少data";
            response.state = String.valueOf(CarInsuranceResponse.RESULT_FAIL);
            response.msgCode = "error_2";
            return JsonKit.bean2Json(response);
        }
        return null;
    }

    /**
     * 处理回调接口，参数非法
     *
     * @param update 数据库操作结果
     * @return 校验结果Response
     */
    private CallBackCarInsuranceResponse dealCallBackParamsIllegal(int update, CallBackCarInsuranceResponse response) {
        if (update > 0) {
            response.msg = "成功";
            response.state = String.valueOf(CarInsuranceResponse.RESULT_OK);
            response.msgCode = "";
        } else {
            response.msg = "bizID或thpBizID不存在或无效";
            response.state = String.valueOf(CarInsuranceResponse.RESULT_FAIL);
            response.msgCode = "error_3";
        }
        return response;
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
                applyState = InsurancePolicyModel.APPLY_UNDERWRITING_SUCCESS;
            } else if (synchFlag == 1) {
                applyState = InsurancePolicyModel.APPLY_UNDERWRITING_PROCESSING;
            } else {
                applyState = "synchFlag = " + synchFlag;
            }
        }
        return applyState;
    }


    private String dealFieldName(String type, String str) {
        if (StringKit.equals(type, "1")) {
            str = str.replaceAll("provinceCode", "code");
            str = str.replaceAll("provinceName", "name");
            str = str.replaceAll("cityCode", "code");
            str = str.replaceAll("cityName", "name");
            str = str.replaceAll("countyCode", "code");
            str = str.replaceAll("countyName", "name");
            str = str.replaceAll("countyList", "children");
            str = str.replaceAll("city", "children");
        }
        return str;
    }

    @Autowired
    private ProductClient productClient;

    @Autowired
    private BrokerageService brokerageService;

    @Autowired
    private CustWarrantyService custWarrantyService;

    @Autowired
    private PremiumService premiumService;

    public String setData(ActionBean actionBean) {
//        CarInsurance.GetProvinceCodeRequest request1 = new CarInsurance.GetProvinceCodeRequest();
//
//        request1.type = "0";
//
//        actionBean.body = JsonKit.bean2Json(request1);
//
//        String provinceCode = getProvinceCode(actionBean);
//
//        ExtendCarInsurancePolicy.GetProvinceCodeResponse codeResponse = JsonKit.json2Bean(provinceCode, ExtendCarInsurancePolicy.GetProvinceCodeResponse.class);
//
//        LinkedHashMap<String, MyBean> map = new LinkedHashMap<>();
//
//        if (codeResponse != null && codeResponse.data != null && !codeResponse.data.isEmpty()) {
//            for (ExtendCarInsurancePolicy.ProvinceCodeDetail datum : codeResponse.data) {
//                CarInsurance.GetInsuranceCompanyRequest request = new CarInsurance.GetInsuranceCompanyRequest();
//
//                request.provinceCode = datum.provinceCode;
//                actionBean.body = JsonKit.bean2Json(request);
//
//                String insuranceByArea = getInsuranceByArea(actionBean);
//                ExtendCarInsurancePolicy.GetInsuranceCompanyResponse getInsuranceCompanyResponse = JsonKit.json2Bean(insuranceByArea, ExtendCarInsurancePolicy.GetInsuranceCompanyResponse.class);
//
//                if (getInsuranceCompanyResponse != null && getInsuranceCompanyResponse.data != null && !getInsuranceCompanyResponse.data.isEmpty()) {
//
//                    for (ExtendCarInsurancePolicy.InsuranceCompany insuranceCompany : getInsuranceCompanyResponse.data) {
//                        map.put(insuranceCompany.insurerCode, new MyBean(insuranceCompany.insurerCode + "_CAR", insuranceCompany.insurerName));
//                    }
//                }
//            }
//        }
//
//        Set<String> strings = map.keySet();
//        ArrayList<MyBean> myBeans = new ArrayList<>();
//
//        for (String string : strings) {
//            myBeans.add(map.get(string));
//        }
//
//        L.log.debug(JsonKit.bean2Json(myBeans));
//
//        if (!myBeans.isEmpty()) {
////            MyBean[] array = new MyBean[myBeans.size()];
////            MyBean[] myBeans1 = myBeans.toArray(array);
//            productClient.addCompany(myBeans);
//        }
        ChannelIdBean channelIdBean = new ChannelIdBean();
        channelIdBean.channelId = "1";

        String brokerageByChannelIdForManagerSystem = brokerageService.getBrokerageByChannelIdForManagerSystem(channelIdBean);

        IncomeBean incomeBean = new IncomeBean();
        incomeBean.managerUuid = "1";
        incomeBean.accountUuid = "1";

        String incomeByManagerUuidAndAccountUuidForManagerSystem = brokerageService.getIncomeByManagerUuidAndAccountUuidForManagerSystem(incomeBean);

        AccountUuidBean accountUuid = new AccountUuidBean();
        accountUuid.accountUuid = "1";

        String policyholderCountByTimeOrAccountId = custWarrantyService.getPolicyholderCountByTimeOrAccountId(accountUuid);

        String premiumByAccountUuidForManagerSystem = premiumService.getPremiumByAccountUuidForManagerSystem(accountUuid);

        String premiumByChannelIdForManagerSystem = premiumService.getPremiumByChannelIdForManagerSystem(channelIdBean);

        String premiumCountByChannelIdForManagerSystem = premiumService.getPremiumCountByChannelIdForManagerSystem(channelIdBean);

        return "";
    }

}