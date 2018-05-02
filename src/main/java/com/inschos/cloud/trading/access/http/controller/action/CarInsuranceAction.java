package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.CarInsurance;
import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.client.PersonClient;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.access.rpc.service.BrokerageService;
import com.inschos.cloud.trading.access.rpc.service.CustWarrantyService;
import com.inschos.cloud.trading.access.rpc.service.PremiumService;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.*;
import com.inschos.cloud.trading.data.dao.CarRecordDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.data.dao.ProductDao;
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

//        if (checkCoverageListResult.coverageList.isEmpty()) {
//            response.data = new CarInsurance.GetPremiumDetail();
//            response.data.insurancePolicies = new ArrayList<>();
//
//            response.data.totalInsuredPremium = "0.00";
//            response.data.totalInsuredPremiumText = "¥0.00";
//
//            CarInsurance.InsurancePolicy insurancePolicy = new CarInsurance.InsurancePolicy();
//            insurancePolicy.totalPremium = "0.00";
//            insurancePolicy.totalPremiumText = "¥0.00";
//
//            response.data.insurancePolicies.add(insurancePolicy);
//
//            return json(BaseResponse.CODE_SUCCESS, "获取参考报价成功", response);
//        }

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
                response.data.insurancePolicies = new ArrayList<>();

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

                    CarInsurance.InsurancePolicy insurancePolicy = new CarInsurance.InsurancePolicy(datum);

                    insurancePolicy.coverageList = dealInsurancePolicyInfoForShowList(datum.coverageList);
                    boolean b = checkCommitEqualsUltimate(checkCoverageListResult.coverageList, datum.coverageList);
                    insurancePolicy.isChanged = b ? "0" : "1";

                    response.data.insurancePolicies.add(insurancePolicy);
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
            return json(BaseResponse.CODE_FAILURE, "缺少人员信息或身份证号码不合法", response);
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
                StringBuilder stringBuilder = new StringBuilder();
                boolean flag = false;
                response.data.insurancePolicyPremiumDetails = new ArrayList<>();
                for (ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail datum : result.data) {
                    datum.biBeginDateValue = parseMillisecondByShowDate(dateSdf, datum.biBeginDate);
                    String s1 = nextYearMillisecond(datum.biBeginDateValue);
                    if (StringKit.isInteger(s1)) {
                        datum.biInsuranceTermText = dateSdf.format(new Date(Long.valueOf(datum.biBeginDateValue))) + "-" + dateSdf.format(new Date(Long.valueOf(s1)));
                    }

                    datum.ciBeginDateValue = parseMillisecondByShowDate(dateSdf, datum.ciBeginDate);
                    s1 = nextYearMillisecond(datum.ciBeginDateValue);
                    if (StringKit.isInteger(s1)) {
                        datum.ciInsuranceTermText = dateSdf.format(new Date(Long.valueOf(datum.ciBeginDateValue))) + "-" + dateSdf.format(new Date(Long.valueOf(s1)));
                    }

                    datum.productName = "";

                    for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : datum.coverageList) {
                        if (StringKit.isNumeric(insurancePolicyInfo.insuredPremium)) {
                            if (StringKit.equals(insurancePolicyInfo.coverageCode, "FORCEPREMIUM")) {
                                ci = ci.add(new BigDecimal(insurancePolicyInfo.insuredPremium));
                            } else {
                                bi = bi.add(new BigDecimal(insurancePolicyInfo.insuredPremium));
                            }
                        }
                    }

                    if (flag) {
                        stringBuilder.append("\n");
                    }

                    stringBuilder.append(dealInsurancePolicyInfoForShowString(datum.coverageList));
                    flag = true;

                    CarInsurance.InsurancePolicyPremiumDetail insurancePolicyPremiumDetail = new CarInsurance.InsurancePolicyPremiumDetail(datum);

                    insurancePolicyPremiumDetail.coverageList = dealInsurancePolicyInfoForShowList(datum.coverageList);
                    boolean b = checkCommitEqualsUltimate(checkCoverageListResult.coverageList, datum.coverageList);
                    insurancePolicyPremiumDetail.isChanged = b ? "0" : "1";
                    response.data.insurancePolicyPremiumDetails.add(insurancePolicyPremiumDetail);
                }

                response.data.biInsuredPremium = bi.toString();
                response.data.ciInsuredPremium = ci.toString();
                response.data.ciInsuredPremiumText = "¥" + ci.toString();
                response.data.biInsuredPremiumText = "¥" + bi.toString();
                BigDecimal add = bi.add(ci);
                response.data.totalInsuredPremium = add.toString();
                response.data.totalInsuredPremiumText = "¥" + add.toString();

                response.data.insuredName = request.personInfo.insuredName;
                response.data.insuranceContent = stringBuilder.toString();

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

            AgentBean agentInfoByPersonIdManagerUuid = personClient.getAgentInfoByPersonIdManagerUuid(actionBean.managerUuid, actionBean.userId);

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
                ciProposal.manager_uuid = actionBean.managerUuid;
                ciProposal.account_uuid = actionBean.accountUuid;
                ciProposal.count = "1";

                if (actionBean.userType == 4) {
                    ciProposal.agent_id = agentInfoByPersonIdManagerUuid != null ? String.valueOf(agentInfoByPersonIdManagerUuid.id) : "";
                    ciProposal.channel_id = agentInfoByPersonIdManagerUuid != null ? String.valueOf(agentInfoByPersonIdManagerUuid.channel_id) : "";
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

                ciProposal.plan_id = "0";
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
                biProposal.manager_uuid = actionBean.managerUuid;
                biProposal.account_uuid = actionBean.accountUuid;
                biProposal.count = "1";

                if (actionBean.userType == 4) {
                    biProposal.agent_id = agentInfoByPersonIdManagerUuid != null ? String.valueOf(agentInfoByPersonIdManagerUuid.id) : "";
                    biProposal.channel_id = agentInfoByPersonIdManagerUuid != null ? String.valueOf(agentInfoByPersonIdManagerUuid.channel_id) : "";
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

                biProposal.plan_id = "0";
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
            for (CarInsurance.InsurancePolicyPremiumDetail insurancePolicyPremiumDetail : getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails) {
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
//        CarInsurance.ResolveIdentityCardRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ResolveIdentityCardRequest.class);
        CarInsurance.ResolveIdentityCardRequest request = new CarInsurance.ResolveIdentityCardRequest();
        CarInsurance.ResolveIdentityCardResponse response = new CarInsurance.ResolveIdentityCardResponse();

        request.frontCardBase64 = "data:image/jpeg;base64,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vWmN7d2y3g7t2mqnDu+11rNHrbejEXBut0o+ZuNagmd61C5dwey6BvO5dtENQNQ0jJs375pgCp81+Ch8KACazYKU4es3rtmFS1M2CQiNDGnKQsTi4W5razltR48ds6MnT9upM+et+Xy7tbf3WGenj5qjx042d9mxM512tLHVjja12rHTrXYc5TqFQp0502EnGlvsEApxWB7+mlqsEdH+ziONtu1gg+08eMoOHDljh4422V4UaM+BE3bgUIMdOtJgh4+csH0Hj9jb+w8jR7h+zPYcPOr29x84agd1/u391CL7bSeyfcc+V2vId+8Oapkt2/bYxk07bR1KsX7dJtu8cattRkk2rN1kG9ZstM3rNztZv3qDbVyzwbaiNJvYbly93jajKBuXoxQow3q2GyTLVtm6ZStRmNUUJBQFRViFUqxavNRWUshXL1vKeY4XLbZVHGu7evESri+x1TqeR4GcO89WS+bN5xhBUZbPm2crKPQrFy6wFciy+fNtBYq1UoICrUCWzV9kyxfo2hwni996HQV4w5aiDMtRnOVzXif+HJ4338kKlGAlyrWK605QKG+/en7RW7ZGx+yvYLsSWcv+6iXzbOVitpJFc2wNspb9tUvm2nqubUJpNy2Z72TL8sW2ecUS24wibV29yLauWmbbVi237auX29Y1y2zrWmT1EvaXuv1t1eMd65bbzvUrbffG1bZ702rbtXElYCVZYXu2rLZ929Z4slWy2g7sWGcHd26ww7s3evL2Jrab7MierXZ07zYAc4cdP7AL2en2G9k2HtpljQd32enDe5C91nTobTt9bI+dPbHPzhzfY2ca9trZU/vs3Mn91nZyn7WwbWlkv+mgtTcdso6mw9Z99ghy3HrONSDnrbu51brOnTFfyykngaoEWxutr63JSajjtAWRvq4zFuo5Y+Hes0irxf0tlvCft3iwHem0ZKDdUn1UOtFOy8W7YYS9iM/6434rpELWn45YOROzkVLGJoZhp6NFm5kYsMsXRu3K5Sm7cv2CXbtxCbnsdPcGwCMQunXrXgb0uQOgWqZmZ+6999+zm7eu29UrM/y4GYeuI8MlGyhnMXdC1n3+FB92F7XcOltPgVu9GMVYtNCWoTQL575lC96ac2c77603bO6br9n8N1+3xW9y/Mab9uacN+wtFGThnNds8RuvsH3d5iJvEe+tN16zecSd/+bL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";
        request.backCardBase64 = "data:image/jpeg;base64,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";

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
//                if (!StringKit.isEmpty(request.frontCardBase64) && result.data != null) {
//                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
//                    request1.base64 = request.frontCardBase64;
//                    request1.fileKey = MD5Kit.MD5Digest(result.data.cardNo + result.data.name + "1");
//                    request1.fileName = MD5Kit.MD5Digest(result.data.cardNo + "1");
//                    FileUpload.getInstance().uploadByBase64(request1);
//                }
//
//                if (!StringKit.isEmpty(request.backCardBase64) && result.data != null) {
//                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
//                    request1.base64 = request.backCardBase64;
//                    request1.fileKey = MD5Kit.MD5Digest(result.data.cardNo + result.data.name + "2");
//                    request1.fileName = MD5Kit.MD5Digest(result.data.cardNo + "2");
//                    FileUpload.getInstance().uploadByBase64(request1);
//                }
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
     * 将投保的实际险别列表变成字符串显示文案
     *
     * @param list 投保的实际险别
     * @return 文案
     */
    public String dealInsurancePolicyInfoForShowString(List<ExtendCarInsurancePolicy.InsurancePolicyInfo> list) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, ExtendCarInsurancePolicy.InsurancePolicyInfo> map = new HashMap<>();
        for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : list) {
            if (insurancePolicyInfo.coverageCode.startsWith("M")) {
                map.put(insurancePolicyInfo.coverageCode.substring(1, insurancePolicyInfo.coverageCode.length()), insurancePolicyInfo);
            }
        }

        int size = list.size();
        for (int i = 0; i < size; i++) {
            ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo = list.get(i);
            if (!insurancePolicyInfo.coverageCode.startsWith("M")) {
                stringBuilder.append(insurancePolicyInfo.coverageName);
                if (map.get(insurancePolicyInfo.coverageCode) != null) {
                    stringBuilder.append(insurancePolicyInfo.coverageName).append("(不计免赔)");
                }

                if (i < size - 1) {
                    stringBuilder.append("、");
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 将投保的实际险别列表变成显示用险别列表
     *
     * @param list 投保的实际险别列表
     * @return 显示用险别列表
     */
    public List<CarInsurance.InsuranceInfo> dealInsurancePolicyInfoForShowList(List<ExtendCarInsurancePolicy.InsurancePolicyInfo> list) {
        Map<String, ExtendCarInsurancePolicy.InsurancePolicyInfo> map = new HashMap<>();
        for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : list) {
            if (insurancePolicyInfo.coverageCode.startsWith("M")) {
                map.put(insurancePolicyInfo.coverageCode.substring(1, insurancePolicyInfo.coverageCode.length()), insurancePolicyInfo);
            }
        }

        List<CarInsurance.InsuranceInfo> result = new ArrayList<>();
        for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : list) {
            CarInsurance.InsuranceInfo insuranceInfo = new CarInsurance.InsuranceInfo();

            insuranceInfo.coverageCode = insurancePolicyInfo.coverageCode;
            insuranceInfo.coverageName = insurancePolicyInfo.coverageName;
            insuranceInfo.insuredAmount = insurancePolicyInfo.insuredAmount;
            insuranceInfo.insuredPremium = insurancePolicyInfo.insuredPremium;

            if (!insurancePolicyInfo.coverageCode.startsWith("M")) {
                insuranceInfo.isExcessOption = map.get(insurancePolicyInfo.coverageCode) != null ? "1" : "0";

                if (StringKit.equals(insuranceInfo.coverageCode, "F")) {
                    insuranceInfo.flag = insurancePolicyInfo.flag;
                }

                if (StringKit.equals(insuranceInfo.coverageCode, "Z2") && !StringKit.isEmpty(insurancePolicyInfo.flag)) {

                    String[] split = insurancePolicyInfo.flag.split(",");

                    if (split.length == 2) {
                        insuranceInfo.day = split[0];
                        insuranceInfo.amount = split[1];
                    }
                }
            }

            result.add(insuranceInfo);
        }
        return result;
    }

    /**
     * 检查提交的投保险别列表是否与实际的投保险别列表一致
     *
     * @param commit   提交的投保险别列表
     * @param ultimate 实际的投保险别列表
     * @return 是否一致
     */
    public boolean checkCommitEqualsUltimate(List<ExtendCarInsurancePolicy.InsuranceInfoDetail> commit, List<ExtendCarInsurancePolicy.InsurancePolicyInfo> ultimate) {

        if (commit == null && ultimate == null) {
            return true;
        }

        if (commit != null && ultimate != null && commit.size() != ultimate.size()) {
            return false;
        }

        if (commit == null || ultimate == null) {
            return false;
        }

        Map<String, ExtendCarInsurancePolicy.InsuranceInfoDetail> map = new HashMap<>();

        for (ExtendCarInsurancePolicy.InsuranceInfoDetail insuranceInfoDetail : commit) {
            map.put(insuranceInfoDetail.coverageCode, insuranceInfoDetail);
        }

        for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : ultimate) {
            ExtendCarInsurancePolicy.InsuranceInfoDetail insuranceInfoDetail = map.get(insurancePolicyInfo.coverageCode);

            if (insuranceInfoDetail == null) {
                return false;
            }

            if (!StringKit.equals(insurancePolicyInfo.insuredAmount, insuranceInfoDetail.insuredAmount)) {
                return false;
            }

            if (!StringKit.equals(insurancePolicyInfo.flag, insuranceInfoDetail.flag)) {
                return false;
            }

        }

        return true;
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
    private PersonClient personClient;
//
//    @Autowired
//    private BrokerageService brokerageService;
//
//    @Autowired
//    private CustWarrantyService custWarrantyService;
//
//    @Autowired
//    private PremiumService premiumService;
//
//    @Autowired
//    private ProductDao productDao;
//
//    public String setData(ActionBean actionBean) {
//        CarInsurance.GetProvinceCodeRequest request1 = new CarInsurance.GetProvinceCodeRequest();
//
//        request1.type = "0";
//
//        actionBean.body = JsonKit.bean2Json(request1);
//
//        String provinceCode = getProvinceCode(actionBean);
//
//        Map<String, String> nameMap = new HashMap<>();
////        nameMap.put("ACIC", "安诚财产保险股份有限公司");
////        nameMap.put("ASTP", "安盛天平财产保险股份有限公司");
////        nameMap.put("AXIC", "安心财产保险有限责任公司");
////        nameMap.put("CCIC", "中国大地财产保险股份有限公司");
////        nameMap.put("CHAC", "诚泰财产保险股份有限公司");
////        nameMap.put("CICP", "中华联合财产保险股份有限公司");
////        nameMap.put("LIHI", "利宝保险有限公司");
////        nameMap.put("PAIC", "中国平安财产保险股份有限公司");
////        nameMap.put("PICC", "中国人民财产保险股份有限公司");
////        nameMap.put("TAIC", "天安财产保险股份有限公司");
////        nameMap.put("TPIC", "太平财产保险有限公司");
////        nameMap.put("YAIC", "永安财产保险股份有限公司");
////        nameMap.put("YGBX", "阳光财产保险股份有限公司");
////        nameMap.put("ZFIC", "珠峰财产保险股份有限公司");
////        nameMap.put("ZHONGAN", "众安在线财产保险股份有限公司");
//
////        nameMap.put("APIC", "永诚财产保险股份有限公司");
////        nameMap.put("TSBX", "泰山财产保险股份有限公司");
////        nameMap.put("CLPC", "中国人寿财产保险股份有限公司");
////        nameMap.put("CPIC", "中国太平洋财产保险股份有限公司");
//
//        ExtendCarInsurancePolicy.GetProvinceCodeResponse codeResponse = JsonKit.json2Bean(provinceCode, ExtendCarInsurancePolicy.GetProvinceCodeResponse.class);
//
//        LinkedHashMap<String, MyBean> map = new LinkedHashMap<>();
//        long time = System.currentTimeMillis();
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
//                        String s = nameMap.get(insuranceCompany.insurerCode);
//                        if (s != null) {
//                            map.put(insuranceCompany.insurerCode, new MyBean(insuranceCompany.insurerCode, s, insuranceCompany.insurerName, time));
//                        }
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
//        Map<String, String> idMap = new HashMap<>();
//
//        if (!myBeans.isEmpty()) {
//            for (MyBean myBean : myBeans) {
//                long l = productDao.addCompany(myBean);
//                idMap.put(myBean.code, myBean.id);
//            }
//        }
//
//        List<MyBean2> list = new ArrayList<>();
//        for (MyBean myBean : myBeans) {
//            MyBean2 myBean2_2 = new MyBean2(myBean.display_name + "交强险", idMap.get(myBean.code), myBean.code + "_CAR_COMPULSORY", "200040001", time);
//            MyBean2 myBean2_1 = new MyBean2(myBean.display_name + "商业险", idMap.get(myBean.code), myBean.code + "_CAR_BUSINESS", "200040002", time);
//            list.add(myBean2_2);
//            list.add(myBean2_1);
//        }
//
//        if (!list.isEmpty()) {
//            for (MyBean2 myBean2 : list) {
//                productDao.addProduct(myBean2);
//            }
//        }
//
//
////        ChannelIdBean channelIdBean = new ChannelIdBean();
////        channelIdBean.channelId = "1";
////
////        String brokerageByChannelIdForManagerSystem = brokerageService.getBrokerageByChannelIdForManagerSystem(channelIdBean);
////
////        IncomeBean incomeBean = new IncomeBean();
////        incomeBean.managerUuid = "1";
////        incomeBean.accountUuid = "1";
////
////        String incomeByManagerUuidAndAccountUuidForManagerSystem = brokerageService.getIncomeByManagerUuidAndAccountUuidForManagerSystem(incomeBean);
////
////        AccountUuidBean accountUuid = new AccountUuidBean();
////        accountUuid.accountUuid = "1";
////
////        String policyholderCountByTimeOrAccountId = custWarrantyService.getPolicyholderCountByTimeOrAccountId(accountUuid);
////
////        String premiumByAccountUuidForManagerSystem = premiumService.getPremiumByAccountUuidForManagerSystem(accountUuid);
////
////        String premiumByChannelIdForManagerSystem = premiumService.getPremiumByChannelIdForManagerSystem(channelIdBean);
////
////        String premiumCountByChannelIdForManagerSystem = premiumService.getPremiumCountByChannelIdForManagerSystem(channelIdBean);
//
////        AgentBean agentInfoByPersonIdManagerUuid = personClient.getAgentInfoByPersonIdManagerUuid("2", "92");
////
////        return agentInfoByPersonIdManagerUuid != null ? agentInfoByPersonIdManagerUuid.toString() : "null";
//
////        InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
////        insurancePolicyModel.manager_uuid = "2";
////        insurancePolicyModel.product_id_string = "0,1";
////
////        List<PolicyListCountModel> insurancePolicyListCountByTimeAndManagerUuidAndProductId = insurancePolicyDao.findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(insurancePolicyModel);
//
////        Map<String, String> typeMap = new HashMap<>();
////
////        养老险 000100010000
////        人寿险 000100020000
////        健康险 000100030000
////        意外险 000100040000
////        重疾险 000100050000
////        其他 000199990000
////        财产损失险 000200010000
////        责任保险 000200020000
////        信用保证险 000200030000
////        车险 000200040000
////        农险 000200050000
////        其他 000200060000
////        生存保险 000100020001
////        死亡保险 000100020002
////        两全保险 000100020003
////        医疗保险 000100030001
////        失能收入保险 000100030002
////        护理保险 000100030003
////        个人意外险 000100040001
////        团体意外险 000100040002
////        企财险 000200010001
////        家财险 000200010002
////        运输工具险 000200010003
////        工程险 000200010004
////        特殊风险 000200010005
////        指数险 000200010006
////        公众责任险 000200020001
////        第三者责任险 000200020002
////        产品责任险 000200020003
////        雇主责任险 000200020004
////        职业责任险 000200020005
////        物流责任险 000200020006
////        出口信用险 000200030001
////        投资保险 000200030002
////        雇员忠诚保证险 000200030003
////        履约保证险 000200030004
////        交强险 000200040001
////        商业险 000200040002
////        typeMap.put("", "");
//
//        MyBean3 root1 = new MyBean3("人身保险", "0", "1", "100000000");
//        productDao.addCategory(root1);
//        MyBean3 root2 = new MyBean3("财产保险", "0", "1", "200000000");
//        productDao.addCategory(root2);
//
//
//        return "";
//    }

}