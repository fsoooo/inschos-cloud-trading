package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.http.controller.bean.CarInsuranceBean;
import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.client.AgentClient;
import com.inschos.cloud.trading.access.rpc.client.FileClient;
import com.inschos.cloud.trading.access.rpc.client.ProductClient;
import com.inschos.cloud.trading.access.rpc.client.TaskResultClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.assist.kit.CardCodeKit;
import com.inschos.cloud.trading.assist.kit.Time2Kit;
import com.inschos.cloud.trading.assist.kit.WarrantyUuidWorker;
import com.inschos.cloud.trading.data.dao.*;
import com.inschos.cloud.trading.extend.car.*;
import com.inschos.cloud.trading.model.*;
import com.inschos.cloud.trading.model.fordao.*;
import com.inschos.common.assist.kit.JsonKit;
import com.inschos.common.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    private CarInfoDao carInfoDao;

    @Autowired
    private CustWarrantyDao custWarrantyDao;

    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Autowired
    private CustWarrantyBrokerageDao custWarrantyBrokerageDao;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private TaskResultClient taskResultClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private AgentClient agentClient;

    /**
     * 获取省级区域代码
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getProvinceCode(ActionBean actionBean) {
        CarInsuranceBean.GetProvinceCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetProvinceCodeRequest.class);
        CarInsuranceBean.GetProvinceCodeResponse response = new CarInsuranceBean.GetProvinceCodeResponse();

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

                    CarInsuranceBean.GetCityCodeRequest getCityCodeRequest = new CarInsuranceBean.GetCityCodeRequest();
                    getCityCodeRequest.provinceCode = result.data.get(0).provinceCode;
                    getCityCodeRequest.type = "0";

                    bean.body = JsonKit.bean2Json(getCityCodeRequest);

                    CarInsuranceBean.GetCityCodeResponse getCityCodeResponse = JsonKit.json2Bean(getCityCode(bean), CarInsuranceBean.GetCityCodeResponse.class);

                    if (getCityCodeResponse != null && getCityCodeResponse.data != null && getCityCodeResponse.code == BaseResponse.CODE_SUCCESS) {

                        response.data = new ArrayList<>();
                        for (ExtendCarInsurancePolicy.ProvinceCodeDetail datum : result.data) {
                            response.data.add(new CarInsuranceBean.ProvinceCodeDetail(datum));
                        }

                        response.data.get(0).children = getCityCodeResponse.data.children;

                        for (int i = 1; i < response.data.size(); i++) {
                            response.data.get(i).children = new ArrayList<>();
                            CarInsuranceBean.CityCode cityCode = new CarInsuranceBean.CityCode();
                            cityCode.name = "";
                            cityCode.code = "";
                            cityCode.children = new ArrayList<>();

                            CarInsuranceBean.AreaCode areaCode = new CarInsuranceBean.AreaCode();
                            areaCode.name = "";
                            areaCode.code = "";
                            cityCode.children.add(areaCode);

                            response.data.get(i).children.add(cityCode);
                        }

                        str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);

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
        CarInsuranceBean.GetCityCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetCityCodeRequest.class);
        CarInsuranceBean.GetCityCodeResponse response = new CarInsuranceBean.GetCityCodeResponse();

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
                response.data = new CarInsuranceBean.ProvinceCodeDetail();
                response.data.code = request.provinceCode;
                response.data.children = new ArrayList<>();

                if (result.data != null && !result.data.isEmpty()) {
                    for (ExtendCarInsurancePolicy.CityCode datum : result.data) {
                        response.data.children.add(new CarInsuranceBean.CityCode(datum));
                    }
                }

                str = json(BaseResponse.CODE_SUCCESS, "获取市级列表成功", response);
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
        CarInsuranceBean.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetCarInfoRequest.class);

        if (request == null || (StringKit.isEmpty(request.frameNo) && StringKit.isEmpty(request.licenseNo))) {
            List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();

            CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
            defaultEntry.details = CheckParamsKit.FAIL;
            list.add(defaultEntry);

            CheckParamsKit.Entry<String, String> frameNo = new CheckParamsKit.Entry<>();
            frameNo.digest = "frameNo";
            frameNo.details = "车架号与车牌号至少存在一个";
            list.add(frameNo);

            CheckParamsKit.Entry<String, String> licenseNo = new CheckParamsKit.Entry<>();
            licenseNo.digest = "licenseNo";
            licenseNo.details = "车架号与车牌号至少存在一个";
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
        CarInsuranceBean.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetCarInfoRequest.class);
        CarInsuranceBean.GetCarInfoResponse response = new CarInsuranceBean.GetCarInfoResponse();

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
        CarInsuranceBean.GetCarInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetCarInfoRequest.class);
        CarInsuranceBean.GetCarInfoResponse response = new CarInsuranceBean.GetCarInfoResponse();

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
        CarInsuranceBean.GetCarModelRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetCarModelRequest.class);
        CarInsuranceBean.GetCarModelResponse response = new CarInsuranceBean.GetCarModelResponse();

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
        CarInsuranceBean.GetCarModelInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetCarModelInfoRequest.class);
        CarInsuranceBean.GetCarModelInfoResponse response = new CarInsuranceBean.GetCarModelInfoResponse();

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
                String calculateDateByShowDate = Time2Kit.parseMillisecondByShowDate(sdf, result.data.firstRegisterDate);

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
        CarInsuranceBean.SearchCarModelRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.SearchCarModelRequest.class);
        CarInsuranceBean.GetCarModelResponse response = new CarInsuranceBean.GetCarModelResponse();

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
        CarInsuranceBean.GetInsuranceCompanyRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetInsuranceCompanyRequest.class);
        CarInsuranceBean.GetInsuranceCompanyResponse response = new CarInsuranceBean.GetInsuranceCompanyResponse();

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

                List<ProductBean> productBeans = productClient.getProductByAutomobileList(actionBean.managerUuid);
                Map<String, ProductBean> hashMap = new HashMap<>();

//                L.log.debug("=============================================================================================================================");
//                if (productBeans != null && !productBeans.isEmpty()) {
//                    L.log.debug("productBeans = " + Arrays.toString(productBeans.toArray(new ProductBean[1])));
//                } else {
//                    L.log.debug("productBeans = null");
//                }
//                L.log.debug("=============================================================================================================================");

                if (productBeans != null && !productBeans.isEmpty()) {
                    Set<String> set = new HashSet<>();
                    for (ProductBean productBean : productBeans) {
                        String[] split = productBean.code.split("_");
                        if (split.length > 0 && productBean.sellStatus == 1) {
                            if (set.contains(split[0])) {
                                hashMap.put(split[0], productBean);
                            } else {
                                set.add(split[0]);
                            }
                        }
                    }

                    List<ExtendCarInsurancePolicy.InsuranceCompany> list = new ArrayList<>();
                    if (!response.data.isEmpty()) {
                        for (ExtendCarInsurancePolicy.InsuranceCompany datum : response.data) {
                            if (hashMap.get(datum.insurerCode) != null) {
                                // PICC,PAIC,CPIC
                                datum.insurerName = String.format("%s车险", datum.insurerName);
                                datum.logoUrl = fileClient.getFileUrl("property_key_" + datum.insurerCode);
                                list.add(datum);

                                if (StringKit.equals(datum.insurerCode, "PICC")) {
                                    datum.sort = 1;
                                } else if (StringKit.equals(datum.insurerCode, "PAIC")) {
                                    datum.sort = 2;
                                } else if (StringKit.equals(datum.insurerCode, "CPIC")) {
                                    datum.sort = 3;
                                }
                            }
                        }
                    }

                    list.sort((o1, o2) -> o1.sort - o2.sort);

                    response.data = list;
                } else {
                    if (response.data == null) {
                        response.data = new ArrayList<>();
                    } else {
                        response.data.clear();
                    }
                }

                if (response.data == null || response.data.isEmpty()) {
                    str = json(BaseResponse.CODE_FAILURE, "您选择的地区暂不支持在线投保", response);
                } else {
                    str = json(BaseResponse.CODE_SUCCESS, "获取保险公司成功", response);
                }

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
        CarInsuranceBean.GetInsuranceInfoRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetInsuranceInfoRequest.class);
        CarInsuranceBean.GetInsuranceInfoResponse response = new CarInsuranceBean.GetInsuranceInfoResponse();

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
        CarInsuranceBean.GetInsuranceStartTimeRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetInsuranceStartTimeRequest.class);
        CarInsuranceBean.GetInsuranceStartTimeResponse response = new CarInsuranceBean.GetInsuranceStartTimeResponse();

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
                response.data.biStartTimeValue = Time2Kit.parseMillisecondByShowDate(sdf, result.data.biStartTime);
                response.data.ciStartTimeValue = Time2Kit.parseMillisecondByShowDate(sdf, result.data.ciStartTime);
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
        CarInsuranceBean.GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoResponse response = new CarInsuranceBean.GetInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoResponse();

        String insuranceByArea = getInsuranceByArea(actionBean);
        CarInsuranceBean.GetInsuranceCompanyResponse getInsuranceCompanyResponse = JsonKit.json2Bean(insuranceByArea, CarInsuranceBean.GetInsuranceCompanyResponse.class);

        if (getInsuranceCompanyResponse == null || getInsuranceCompanyResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceByArea;
        }

        response.data = new CarInsuranceBean.InsuranceCompanyAndInsuranceStartTimeAndInsuranceInfo();

        response.data.insuranceCompanies = getInsuranceCompanyResponse.data;

        String insuranceStartTime = getInsuranceStartTime(actionBean);
        CarInsuranceBean.GetInsuranceStartTimeResponse getInsuranceStartTimeResponse = JsonKit.json2Bean(insuranceStartTime, CarInsuranceBean.GetInsuranceStartTimeResponse.class);

        if (getInsuranceStartTimeResponse == null || getInsuranceStartTimeResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceStartTime;
        }

        response.data.startTimeInfo = getInsuranceStartTimeResponse.data;

        String insuranceInfo = getInsuranceInfo(actionBean);
        CarInsuranceBean.GetInsuranceInfoResponse getInsuranceInfoResponse = JsonKit.json2Bean(insuranceInfo, CarInsuranceBean.GetInsuranceInfoResponse.class);

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
        CarInsuranceBean.GetPremiumRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetPremiumRequest.class);
        CarInsuranceBean.GetPremiumResponse response = new CarInsuranceBean.GetPremiumResponse();

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
        CustCar oneByResponseNo = carRecordDao.findOneByCarCode(request.carInfo.licenseNo);

        CustCar custCar = new CustCar();
        custCar.car_code = request.carInfo.licenseNo;
        custCar.name = request.personInfo.ownerName;
        custCar.code = request.personInfo.ownerID;
        custCar.phone = request.personInfo.ownerMobile;
        custCar.frame_no = request.carInfo.frameNo;
        custCar.engine_no = request.carInfo.engineNo;
        custCar.vehicle_fgw_code = request.carInfo.vehicleFgwCode;
        custCar.vehicle_fgw_name = request.carInfo.vehicleFgwName;
        custCar.parent_veh_name = request.carInfo.parentVehName;
        custCar.brand_code = request.carInfo.brandCode;
        custCar.brand_name = request.carInfo.brandName;
        custCar.engine_desc = request.carInfo.engineDesc;
        custCar.new_car_price = request.carInfo.newCarPrice;
        custCar.purchase_price_tax = request.carInfo.purchasePriceTax;
        custCar.import_flag = request.carInfo.importFlag;
        custCar.seat = request.carInfo.seat;
        custCar.standard_name = request.carInfo.standardName;
        custCar.is_trans = request.carInfo.isTrans;
        custCar.remark = request.carInfo.remark;
        custCar.response_no = request.carInfo.responseNo;
        custCar.updated_at = String.valueOf(time);

        if (oneByResponseNo == null) {
            custCar.created_at = String.valueOf(time);
            carRecordDao.addCarRecord(custCar);
        } else {
            carRecordDao.updateCarRecordByCarCode(custCar);
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
        CarInsuranceBean.GetInsuranceInfoResponse getInsuranceInfoResponse = JsonKit.json2Bean(insuranceInfo, CarInsuranceBean.GetInsuranceInfoResponse.class);

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
                response.data = new CarInsuranceBean.GetPremiumDetail();
                response.data.insurancePolicies = new ArrayList<>();

                BigDecimal total = new BigDecimal("0.00");
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                for (ExtendCarInsurancePolicy.InsurancePolicy datum : result.data) {

                    BigDecimal bi;
                    if (StringKit.isNumeric(datum.biPremium)) {
                        bi = new BigDecimal(datum.biPremium);
                    } else {
                        bi = new BigDecimal("0.00");
                    }

                    datum.biPremium = decimalFormat.format(bi.doubleValue());

                    BigDecimal ci;
                    if (StringKit.isNumeric(datum.ciPremium)) {
                        ci = new BigDecimal(datum.ciPremium);
                    } else {
                        ci = new BigDecimal("0.00");
                    }

                    datum.ciPremium = decimalFormat.format(ci.doubleValue());

                    if (StringKit.isNumeric(datum.carshipTax)) {
                        datum.carshipTax = decimalFormat.format(new BigDecimal(datum.carshipTax).doubleValue());
                        datum.carshipTaxText = "¥" + datum.carshipTax;
                    } else {
                        datum.carshipTax = "0.00";
                        datum.carshipTaxText = "¥" + datum.carshipTax;
                    }

                    BigDecimal bigDecimal = new BigDecimal(datum.carshipTax);
                    bigDecimal = bigDecimal.add(bi).add(ci);
                    total = total.add(bigDecimal);

                    datum.totalPremium = decimalFormat.format(bigDecimal.doubleValue());
                    datum.totalPremiumText = "¥" + datum.totalPremium;

                    CarInsuranceBean.InsurancePolicy insurancePolicy = new CarInsuranceBean.InsurancePolicy(datum);

                    insurancePolicy.coverageList = dealInsurancePolicyInfoForShowList(datum.coverageList);
                    boolean b = checkCommitEqualsUltimate(checkCoverageListResult.coverageList, datum.coverageList);
                    insurancePolicy.isChanged = b ? "0" : "1";

                    for (CarInsuranceBean.InsuranceInfo info : insurancePolicy.coverageList) {
                        info.insuredPremiumText = "¥" + info.insuredPremium;
                    }

                    insurancePolicy.productName = "";

                    ProductBean ciProduct = productClient.getProductByCode(request.insurerCode + "_CAR_COMPULSORY");

                    if (ciProduct == null) {
                        return json(BaseResponse.CODE_FAILURE, "获取参考保费错误", response);
                    }

                    insurancePolicy.companyLogo = fileClient.getFileUrl("property_key_" + datum.insurerCode);

                    insurancePolicy.productName = ciProduct.displayName;

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
        CarInsuranceBean.GetPremiumCalibrateRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetPremiumCalibrateRequest.class);
        CarInsuranceBean.GetPremiumCalibrateResponse response = new CarInsuranceBean.GetPremiumCalibrateResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetPremiumCalibrateRequest getPremiumCalibrateRequest = new ExtendCarInsurancePolicy.GetPremiumCalibrateRequest();

        getPremiumCalibrateRequest.refId = request.refId;
        // 代理人下单，必须带
        getPremiumCalibrateRequest.agentMobile = request.agentMobile;
        getPremiumCalibrateRequest.payType = request.payType;
        getPremiumCalibrateRequest.invoiceType = request.invoiceType;
        getPremiumCalibrateRequest.remittingTax = request.remittingTax;


        getPremiumCalibrateRequest.thpBizID = getThpBizID();
        getPremiumCalibrateRequest.cityCode = request.cityCode;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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

        if (!StringKit.isNumeric(request.personInfo.applicantIdType)) {
            return json(BaseResponse.CODE_FAILURE, "投保人证件类型错误", response);
        }

        Integer integer1 = Integer.valueOf(request.personInfo.ownerIdType);
        Date birthDayByCode1 = CardCodeKit.getBirthDayByCode(integer1, request.personInfo.ownerID);
        if (birthDayByCode1 == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少车主证件信息或身份证号码不合法", response);
        }

        int sexByCode1 = CardCodeKit.getSexByCode(integer1, request.personInfo.ownerID);
        if (sexByCode1 == -1) {
            return json(BaseResponse.CODE_FAILURE, "缺少车主证件信息或身份证号码不合法", response);
        }

        request.personInfo.ownerBirthday = String.valueOf(birthDayByCode1.getTime());
        request.personInfo.ownerSex = String.valueOf(sexByCode1);

        Integer integer2 = Integer.valueOf(request.personInfo.applicantIdType);
        Date birthDayByCode2 = CardCodeKit.getBirthDayByCode(integer2, request.personInfo.applicantID);
        if (birthDayByCode2 == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少投保人证件信息或身份证号码不合法", response);
        }

        int sexByCode2 = CardCodeKit.getSexByCode(integer2, request.personInfo.applicantID);
        if (sexByCode2 == -1) {
            return json(BaseResponse.CODE_FAILURE, "缺少投保人证件信息或身份证号码不合法", response);
        }

        request.personInfo.applicantBirthday = String.valueOf(birthDayByCode2.getTime());
        request.personInfo.applicantSex = String.valueOf(sexByCode2);

        Integer integer3 = Integer.valueOf(request.personInfo.insuredIdType);
        Date birthDayByCode3 = CardCodeKit.getBirthDayByCode(integer3, request.personInfo.insuredID);
        if (birthDayByCode3 == null) {
            return json(BaseResponse.CODE_FAILURE, "缺少被保险人证件信息或身份证号码不合法", response);
        }

        int sexByCode3 = CardCodeKit.getSexByCode(integer3, request.personInfo.insuredID);
        if (sexByCode3 == -1) {
            return json(BaseResponse.CODE_FAILURE, "缺少被保险人证件信息或身份证号码不合法", response);
        }

        request.personInfo.insuredBirthday = String.valueOf(birthDayByCode3.getTime());
        request.personInfo.insuredSex = String.valueOf(sexByCode3);

        if (!request.personInfo.isEnable()) {
            return json(BaseResponse.CODE_FAILURE, "缺少人员信息或身份证号码不合法", response);
        }

        getPremiumCalibrateRequest.personInfo = request.personInfo;

        if (request.coverageList == null || request.coverageList.isEmpty()) {
            return json(BaseResponse.CODE_FAILURE, "缺少险别", response);
        }

        String insuranceInfo = getInsuranceInfo(actionBean);
        CarInsuranceBean.GetInsuranceInfoResponse getInsuranceInfoResponse = JsonKit.json2Bean(insuranceInfo, CarInsuranceBean.GetInsuranceInfoResponse.class);

        if (getInsuranceInfoResponse == null || getInsuranceInfoResponse.code != BaseResponse.CODE_SUCCESS) {
            return insuranceInfo;
        }

        CheckCoverageListResult checkCoverageListResult = checkCoverageList(getInsuranceInfoResponse.data, request.coverageList);

        if (!checkCoverageListResult.result) {
            return json(BaseResponse.CODE_FAILURE, checkCoverageListResult.message, response);
        }

        long current = System.currentTimeMillis();
        if (checkCoverageListResult.hasCommercialInsurance) {
            Long biBeginDate = formatterDate(request.biBeginDateValue);
            if (biBeginDate == null || current >= biBeginDate) {
                return json(BaseResponse.CODE_FAILURE, "商业险起保日期错误或时间早于" + sdf.format(new Date(current + 24 * 60 * 60 * 1000)), response);
            }
            getPremiumCalibrateRequest.biBeginDate = sdf.format(new Date(biBeginDate));
        }

        if (checkCoverageListResult.hasCompulsoryInsurance) {
            Long ciBeginDate = formatterDate(request.ciBeginDateValue);
            if (ciBeginDate == null || current >= ciBeginDate) {
                return json(BaseResponse.CODE_FAILURE, "强险起保日期错误或时间早于" + sdf.format(new Date(current + 24 * 60 * 60 * 1000)), response);
            }
            getPremiumCalibrateRequest.ciBeginDate = sdf.format(new Date(ciBeginDate));
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
                response.data = new CarInsuranceBean.GetPremiumCalibrateDetail();

                BigDecimal ci = new BigDecimal("0.0");
                BigDecimal bi = new BigDecimal("0.0");
                BigDecimal carshipTax = new BigDecimal("0.0");
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                StringBuilder stringBuilder = new StringBuilder();
                boolean flag = false;
                response.data.insurancePolicyPremiumDetails = new ArrayList<>();
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                for (ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail datum : result.data) {
                    datum.biBeginDateValue = Time2Kit.parseMillisecondByShowDate(dateSdf, datum.biBeginDate);
                    String s1 = nextYearMillisecond(datum.biBeginDateValue);
                    if (StringKit.isInteger(s1) && s1 != null) {
                        datum.biInsuranceTermText = dateSdf.format(new Date(Long.valueOf(datum.biBeginDateValue))) + "-" + dateSdf.format(new Date(Long.valueOf(s1)));
                    }

                    datum.ciBeginDateValue = Time2Kit.parseMillisecondByShowDate(dateSdf, datum.ciBeginDate);
                    s1 = nextYearMillisecond(datum.ciBeginDateValue);
                    if (StringKit.isInteger(s1) && s1 != null) {
                        datum.ciInsuranceTermText = dateSdf.format(new Date(Long.valueOf(datum.ciBeginDateValue))) + "-" + dateSdf.format(new Date(Long.valueOf(s1)));
                    }

                    datum.productName = "";

                    ProductBean ciProduct = productClient.getProductByCode(request.insurerCode + "_CAR_COMPULSORY");

                    if (ciProduct == null) {
                        return json(BaseResponse.CODE_FAILURE, "获取精准保费错误", response);
                    }

                    datum.productName = ciProduct.displayName;

                    for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : datum.coverageList) {
                        if (StringKit.isNumeric(insurancePolicyInfo.insuredPremium)) {
                            if (StringKit.equals(insurancePolicyInfo.coverageCode, CustWarrantyCar.COVERAGE_CODE_FORCEPREMIUM)) {
                                ci = ci.add(new BigDecimal(insurancePolicyInfo.insuredPremium));
                                datum.hasCompulsoryInsurance = true;
                            } else {
                                bi = bi.add(new BigDecimal(insurancePolicyInfo.insuredPremium));
                                datum.hasCommercialInsurance = true;
                            }
                        }
                    }

                    if (flag) {
                        stringBuilder.append("\n");
                    }

                    stringBuilder.append(dealInsurancePolicyInfoForShowString(datum.coverageList));
                    flag = true;

                    if (StringKit.isNumeric(datum.carshipTax)) {
                        datum.carshipTax = decimalFormat.format(new BigDecimal(datum.carshipTax).doubleValue());
                    } else {
                        datum.carshipTax = "0.00";
                    }
                    datum.carshipTaxText = "¥" + datum.carshipTax;
                    carshipTax = carshipTax.add(new BigDecimal(datum.carshipTax));

                    CarInsuranceBean.InsurancePolicyPremiumDetail insurancePolicyPremiumDetail = new CarInsuranceBean.InsurancePolicyPremiumDetail(datum);

                    insurancePolicyPremiumDetail.companyLogo = fileClient.getFileUrl("property_key_" + datum.insurerCode);

                    insurancePolicyPremiumDetail.coverageList = dealInsurancePolicyInfoForShowList(datum.coverageList);
                    boolean b = checkCommitEqualsUltimate(checkCoverageListResult.coverageList, datum.coverageList);
                    insurancePolicyPremiumDetail.isChanged = b ? "0" : "1";

                    for (CarInsuranceBean.InsuranceInfo info : insurancePolicyPremiumDetail.coverageList) {
                        info.insuredPremiumText = "¥" + info.insuredPremium;
                    }

                    response.data.insurancePolicyPremiumDetails.add(insurancePolicyPremiumDetail);
                }

                response.data.biInsuredPremium = bi.toString();
                response.data.ciInsuredPremium = ci.toString();
                response.data.ciInsuredPremiumText = "¥" + ci.toString();
                response.data.biInsuredPremiumText = "¥" + bi.toString();
                BigDecimal add = bi.add(ci).add(carshipTax);
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
        CarInsuranceBean.PremiumCalibrateAndApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.PremiumCalibrateAndApplyUnderwritingRequest.class);
        CarInsuranceBean.ApplyUnderwritingResponse response = new CarInsuranceBean.ApplyUnderwritingResponse();

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

        if (StringKit.isEmail(request.applyUnderwriting.policyEmail)) {
            applyUnderwritingRequest.policyEmail = request.applyUnderwriting.policyEmail;
        } else {
            return json(BaseResponse.CODE_PARAM_ERROR, "电子邮箱地址不正确", response);
        }

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

                if (StringKit.equals(applyState, CustWarranty.APPLY_UNDERWRITING_SUCCESS)) {
                    payState = CustWarrantyCost.PAY_STATUS_WAIT;
                } else if (StringKit.equals(applyState, CustWarranty.APPLY_UNDERWRITING_PROCESSING)) {
                    payState = CustWarrantyCost.APPLY_UNDERWRITING_PROCESSING;
                }

            } else {
                // 核保失败
                payState = CustWarrantyCost.APPLY_UNDERWRITING_FAILURE;
                if (response.data == null) {
                    response.data = new ExtendCarInsurancePolicy.ApplyUnderwriting();
                }
            }

            ProductBean ciProduct = productClient.getProductByCode(request.applyUnderwriting.insurerCode + "_CAR_COMPULSORY");

            if (ciProduct == null) {
                return json(BaseResponse.CODE_FAILURE, "申请核保失败", response);
            }

            ProductBean biProduct = productClient.getProductByCode(request.applyUnderwriting.insurerCode + "_CAR_BUSINESS");

            if (biProduct == null) {
                return json(BaseResponse.CODE_FAILURE, "申请核保失败", response);
            }

            BigDecimal ciIntegral = new BigDecimal(request.applyUnderwriting.cIntegral);
            BigDecimal biIntegral = new BigDecimal(request.applyUnderwriting.bIntegral);

            String warrantyStatus = CustWarranty.POLICY_STATUS_PENDING;

            InsurancePolicyAndParticipantForCarInsurance insurancePolicyAndParticipantForCarInsurance = new InsurancePolicyAndParticipantForCarInsurance();
            String time = String.valueOf(System.currentTimeMillis());

            AgentBean agentInfoByPersonIdManagerUuid = null;
            if (actionBean.userType == 4 && StringKit.isInteger(actionBean.userId)) {
                agentInfoByPersonIdManagerUuid = agentClient.getAgentInfoByPersonIdManagerUuid(actionBean.managerUuid, Long.valueOf(actionBean.userId));
            }

            // FORCEPREMIUM 强险
            if (request.applyUnderwriting.hasCompulsoryInsurance) {

                if (result.data.ciProposalNo == null) {
                    result.data.ciProposalNo = "";
                }

                CustWarranty ciProposal = new CustWarranty();
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
                    ciProposal.warranty_from = CustWarranty.SOURCE_ONLINE;
                } else {
                    ciProposal.warranty_from = CustWarranty.SOURCE_SELF;
                }

                ciProposal.type = CustWarranty.POLICY_TYPE_CAR;
                ciProposal.warranty_status = warrantyStatus;
                ciProposal.pay_category_id = String.valueOf(ciProduct.payCategoryId);
                ciProposal.integral = String.valueOf(ciIntegral.doubleValue());
                ciProposal.state = "1";
                ciProposal.created_at = time;
                ciProposal.updated_at = time;

                ciProposal.plan_id = "0";
                ciProposal.product_id = String.valueOf(ciProduct.id);
                ciProposal.ins_company_id = String.valueOf(ciProduct.insuranceCoId);
                ciProposal.is_settlement = "0";

                applyUnderwritingRequest.addresseeName = request.applyUnderwriting.addresseeName;
                applyUnderwritingRequest.addresseeMobile = request.applyUnderwriting.addresseeMobile;

                ciProposal.express_email = applyUnderwritingRequest.policyEmail;
                ciProposal.express_address = applyUnderwritingRequest.addresseeDetails;
                ciProposal.express_province_code = applyUnderwritingRequest.addresseeProvince;
                ciProposal.express_city_code = applyUnderwritingRequest.addresseeCity;
                ciProposal.express_county_code = applyUnderwritingRequest.addresseeCounty;

                insurancePolicyAndParticipantForCarInsurance.ciProposal = ciProposal;

                // 存支付信息
                CustWarrantyCost ciCustWarrantyCost = new CustWarrantyCost();
                ciCustWarrantyCost.warranty_uuid = ciProposal.warranty_uuid;
                ciCustWarrantyCost.pay_time = ciProposal.start_time;
                ciCustWarrantyCost.phase = "1";
                ciCustWarrantyCost.is_settlement = "0";
                ciCustWarrantyCost.premium = request.applyUnderwriting.ciInsuredPremium;
                ciCustWarrantyCost.tax_money = request.applyUnderwriting.ciCarShipTax;
                ciCustWarrantyCost.pay_status = payState;
                ciCustWarrantyCost.created_at = time;
                ciCustWarrantyCost.updated_at = time;

                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCost = ciCustWarrantyCost;

                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCar = new CustWarrantyCar(ciProposal.warranty_uuid,
                        response.data.bizID,
                        response.data.thpBizID,
                        CustWarrantyCar.INSURANCE_TYPE_STRONG,
                        time, JsonKit.bean2Json(request.applyUnderwriting.coverageList),
                        JsonKit.bean2Json(request.applyUnderwriting.spAgreements),
                        response.data.bjCodeFlag,
                        request.premiumCalibrate.carInfo,
                        request.premiumCalibrate.personInfo);

                // 被保险人
                insurancePolicyAndParticipantForCarInsurance.ciInsured = new CustWarrantyPerson(
                        ciProposal.warranty_uuid,
                        CustWarrantyPerson.TYPE_INSURED,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        ciEndDateValue,
                        request.premiumCalibrate.personInfo);

                // 投保人
                insurancePolicyAndParticipantForCarInsurance.ciPolicyholder = new CustWarrantyPerson(
                        ciProposal.warranty_uuid,
                        CustWarrantyPerson.TYPE_POLICYHOLDER,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        ciEndDateValue,
                        request.premiumCalibrate.personInfo);

                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerage = new CustWarrantyBrokerage(ciProposal.warranty_uuid,
                        actionBean.managerUuid,
                        ciProposal.channel_id,
                        ciProposal.agent_id,
                        time);

                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerage.setCarIntegral(ciIntegral);
                BigDecimal bigDecimal = new BigDecimal(0);
                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerage.setBrokerage(bigDecimal, bigDecimal, bigDecimal, bigDecimal, bigDecimal);
                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerage.setBrokerageRate(bigDecimal, bigDecimal, bigDecimal, bigDecimal, bigDecimal);

//                if (StringKit.isInteger(ciProposal.product_id)) {
//                    ProductBrokerageBean productBrokerageBean = new ProductBrokerageBean();
//                    productBrokerageBean.productId = Long.valueOf(ciProposal.product_id);
//                    productBrokerageBean.managerUuid = ciProposal.manager_uuid;
//                    productBrokerageBean.payTimes = 1;
//
//                    if (StringKit.isInteger(ciProposal.channel_id)) {
//                        productBrokerageBean.channelId = Long.valueOf(ciProposal.channel_id);
//                    }
//
//                    if (StringKit.isInteger(ciProposal.agent_id)) {
//                        productBrokerageBean.agentId = Long.valueOf(ciProposal.agent_id);
//                    }
//
//                    ProductBrokerageInfoBean brokerage = productClient.getBrokerage(productBrokerageBean);
//
//                    BigDecimal managerBrokerage = new BigDecimal("0.00");
//                    BigDecimal channelBrokerage = new BigDecimal("0.00");
//                    BigDecimal agentBrokerage = new BigDecimal("0.00");
//
//                    BigDecimal managerRate = new BigDecimal("0.00");
//                    BigDecimal channelRate = new BigDecimal("0.00");
//                    BigDecimal agentRate = new BigDecimal("0.00");
//
//                    if (brokerage != null) {
//                        managerRate = new BigDecimal(brokerage.platformBrokerage).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN);
//                        channelRate = new BigDecimal(brokerage.channelBrokerage).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN);
//                        agentRate = new BigDecimal(brokerage.agentBrokerage).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN);
//
//                        managerBrokerage = managerRate.multiply(ciInsuredPremium);
//                        channelBrokerage = channelRate.multiply(ciInsuredPremium);
//                        agentBrokerage = agentRate.multiply(ciInsuredPremium);
//                    }
//
//                    insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerageModel.setBrokerage(ciIntegral, ciIntegral, managerBrokerage, channelBrokerage, agentBrokerage);
//                    insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerageModel.setBrokerageRate(cIntegralRate, cIntegralRate, managerRate, channelRate, agentRate);
//                }
            }

            if (request.applyUnderwriting.hasCommercialInsurance) {

                if (result.data.biProposalNo == null) {
                    result.data.biProposalNo = "";
                }
                // 商业险
                CustWarranty biProposal = new CustWarranty();
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
                    biProposal.warranty_from = CustWarranty.SOURCE_ONLINE;
                } else {
                    biProposal.warranty_from = CustWarranty.SOURCE_SELF;
                }

                biProposal.type = CustWarranty.POLICY_TYPE_CAR;
                biProposal.warranty_status = warrantyStatus;
                biProposal.pay_category_id = String.valueOf(biProduct.payCategoryId);
                biProposal.integral = String.valueOf(biIntegral.doubleValue());
                biProposal.state = "1";
                biProposal.created_at = time;
                biProposal.updated_at = time;

                biProposal.plan_id = "0";
                biProposal.product_id = String.valueOf(biProduct.id);
                biProposal.ins_company_id = String.valueOf(biProduct.insuranceCoId);

                biProposal.is_settlement = "0";

                biProposal.express_email = applyUnderwritingRequest.policyEmail;
                biProposal.express_address = applyUnderwritingRequest.addresseeDetails;
                biProposal.express_province_code = applyUnderwritingRequest.addresseeProvince;
                biProposal.express_city_code = applyUnderwritingRequest.addresseeCity;
                biProposal.express_county_code = applyUnderwritingRequest.addresseeCounty;

                insurancePolicyAndParticipantForCarInsurance.biProposal = biProposal;

                CustWarrantyCost biCustWarrantyCost = new CustWarrantyCost();
                biCustWarrantyCost.warranty_uuid = biProposal.warranty_uuid;
                biCustWarrantyCost.pay_time = biProposal.start_time;
                biCustWarrantyCost.phase = "1";
                biCustWarrantyCost.is_settlement = "0";
                biCustWarrantyCost.premium = request.applyUnderwriting.biInsuredPremium;
                biCustWarrantyCost.tax_money = request.applyUnderwriting.biCarShipTax;
                biCustWarrantyCost.pay_status = payState;
                biCustWarrantyCost.created_at = time;
                biCustWarrantyCost.updated_at = time;

                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCost = biCustWarrantyCost;

                // 存保单车辆信息
                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCar = new CustWarrantyCar(biProposal.warranty_uuid,
                        response.data.bizID,
                        response.data.thpBizID,
                        CustWarrantyCar.INSURANCE_TYPE_COMMERCIAL,
                        time, JsonKit.bean2Json(request.applyUnderwriting.coverageList),
                        JsonKit.bean2Json(request.applyUnderwriting.spAgreements),
                        response.data.bjCodeFlag,
                        request.premiumCalibrate.carInfo,
                        request.premiumCalibrate.personInfo);

                // 存保单人员信息
                insurancePolicyAndParticipantForCarInsurance.biInsured = new CustWarrantyPerson(
                        biProposal.warranty_uuid,
                        CustWarrantyPerson.TYPE_INSURED,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        biEndDateValue,
                        request.premiumCalibrate.personInfo);


                insurancePolicyAndParticipantForCarInsurance.biPolicyholder = new CustWarrantyPerson(
                        biProposal.warranty_uuid,
                        CustWarrantyPerson.TYPE_POLICYHOLDER,
                        "1",
                        time,
                        request.applyUnderwriting.biBeginDateValue,
                        biEndDateValue,
                        request.premiumCalibrate.personInfo);

                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerage = new CustWarrantyBrokerage(biProposal.warranty_uuid,
                        actionBean.managerUuid,
                        biProposal.channel_id,
                        biProposal.agent_id,
                        time);

                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerage.setCarIntegral(biIntegral);
                BigDecimal bigDecimal = new BigDecimal(0);
                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerage.setBrokerage(bigDecimal, bigDecimal, bigDecimal, bigDecimal, bigDecimal);
                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerage.setBrokerageRate(bigDecimal, bigDecimal, bigDecimal, bigDecimal, bigDecimal);

//                if (StringKit.isInteger(biProposal.product_id)) {
//                    ProductBrokerageBean productBrokerageBean = new ProductBrokerageBean();
//                    productBrokerageBean.productId = Long.valueOf(biProposal.product_id);
//                    productBrokerageBean.managerUuid = biProposal.manager_uuid;
//                    productBrokerageBean.payTimes = 1;
//
//                    if (StringKit.isInteger(biProposal.channel_id)) {
//                        productBrokerageBean.channelId = Long.valueOf(biProposal.channel_id);
//                    }
//
//                    if (StringKit.isInteger(biProposal.agent_id)) {
//                        productBrokerageBean.agentId = Long.valueOf(biProposal.agent_id);
//                    }
//
//                    ProductBrokerageInfoBean brokerage = productClient.getBrokerage(productBrokerageBean);
//
//                    BigDecimal managerBrokerage = new BigDecimal("0.00");
//                    BigDecimal channelBrokerage = new BigDecimal("0.00");
//                    BigDecimal agentBrokerage = new BigDecimal("0.00");
//
//                    BigDecimal managerRate = new BigDecimal("0.00");
//                    BigDecimal channelRate = new BigDecimal("0.00");
//                    BigDecimal agentRate = new BigDecimal("0.00");
//
//                    if (brokerage != null) {
//                        managerRate = new BigDecimal(brokerage.platformBrokerage).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN);
//                        channelRate = new BigDecimal(brokerage.channelBrokerage).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN);
//                        agentRate = new BigDecimal(brokerage.agentBrokerage).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN);
//
//                        managerBrokerage = managerRate.multiply(biInsuredPremium);
//                        channelBrokerage = channelRate.multiply(biInsuredPremium);
//                        agentBrokerage = agentRate.multiply(biInsuredPremium);
//                    }
//
//                    insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerageModel.setBrokerage(biIntegral, biIntegral, managerBrokerage, channelBrokerage, agentBrokerage);
//                    insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerageModel.setBrokerageRate(bIntegralRate, bIntegralRate, managerRate, channelRate, agentRate);
//                }
            }

            int i = custWarrantyDao.addInsurancePolicyAndParticipantForCarInsurance(insurancePolicyAndParticipantForCarInsurance);

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
        CarInsuranceBean.PremiumCalibrateAndApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.PremiumCalibrateAndApplyUnderwritingRequest.class);
        CarInsuranceBean.PremiumCalibrateAndApplyUnderwritingResponse response = new CarInsuranceBean.PremiumCalibrateAndApplyUnderwritingResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        actionBean.body = JsonKit.bean2Json(request.premiumCalibrate);

        String premiumCalibrate = getPremiumCalibrate(actionBean);
        CarInsuranceBean.GetPremiumCalibrateResponse getPremiumCalibrateResponse = JsonKit.json2Bean(premiumCalibrate, CarInsuranceBean.GetPremiumCalibrateResponse.class);

        if (getPremiumCalibrateResponse == null || getPremiumCalibrateResponse.code != BaseResponse.CODE_SUCCESS) {
            return premiumCalibrate;
        }

        if (getPremiumCalibrateResponse.data != null && !getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.isEmpty()) {
            if (StringKit.isEmpty(getPremiumCalibrateResponse.data.ciInsuredPremium) || !StringKit.isNumeric(getPremiumCalibrateResponse.data.ciInsuredPremium)) {
                getPremiumCalibrateResponse.data.ciInsuredPremium = "0";
            }
            request.applyUnderwriting.ciInsuredPremium = getPremiumCalibrateResponse.data.ciInsuredPremium;

            if (StringKit.isEmpty(getPremiumCalibrateResponse.data.biInsuredPremium) || !StringKit.isNumeric(getPremiumCalibrateResponse.data.biInsuredPremium)) {
                getPremiumCalibrateResponse.data.biInsuredPremium = "0";
            }
            request.applyUnderwriting.biInsuredPremium = getPremiumCalibrateResponse.data.biInsuredPremium;

            // request.applyUnderwriting.bjCodeFlag = getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.get(0).;
            boolean flag = false;
            for (CarInsuranceBean.InsurancePolicyPremiumDetail insurancePolicyPremiumDetail : getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails) {
                if (StringKit.equals(insurancePolicyPremiumDetail.insurerCode, request.applyUnderwriting.insurerCode)) {
                    request.applyUnderwriting.bizID = insurancePolicyPremiumDetail.bizID;
                    request.applyUnderwriting.bjCodeFlag = insurancePolicyPremiumDetail.bjCodeFlag;
                    request.applyUnderwriting.channelCode = insurancePolicyPremiumDetail.channelCode;
                    request.applyUnderwriting.ciBeginDateValue = insurancePolicyPremiumDetail.ciBeginDateValue;
                    request.applyUnderwriting.biBeginDateValue = insurancePolicyPremiumDetail.biBeginDateValue;
                    request.applyUnderwriting.coverageList = insurancePolicyPremiumDetail.coverageList;
                    request.applyUnderwriting.spAgreements = insurancePolicyPremiumDetail.spAgreement;

                    if (StringKit.isEmpty(insurancePolicyPremiumDetail.carshipTax) || !StringKit.isNumeric(insurancePolicyPremiumDetail.carshipTax)) {
                        insurancePolicyPremiumDetail.carshipTax = "0";
                    }
                    request.applyUnderwriting.carshipTax = insurancePolicyPremiumDetail.carshipTax;

                    if (StringKit.isEmpty(insurancePolicyPremiumDetail.integral) || !StringKit.isNumeric(insurancePolicyPremiumDetail.integral)) {
                        insurancePolicyPremiumDetail.integral = "0";
                    }
                    request.applyUnderwriting.integral = insurancePolicyPremiumDetail.integral;

                    request.applyUnderwriting.hasCompulsoryInsurance = insurancePolicyPremiumDetail.hasCompulsoryInsurance;
                    request.applyUnderwriting.hasCommercialInsurance = insurancePolicyPremiumDetail.hasCommercialInsurance;
                    if (StringKit.isEmpty(insurancePolicyPremiumDetail.cIntegral) || !StringKit.isNumeric(insurancePolicyPremiumDetail.cIntegral)) {
                        insurancePolicyPremiumDetail.cIntegral = "0";
                    }
                    request.applyUnderwriting.cIntegral = insurancePolicyPremiumDetail.cIntegral;

                    if (StringKit.isEmpty(insurancePolicyPremiumDetail.bIntegral) || !StringKit.isNumeric(insurancePolicyPremiumDetail.bIntegral)) {
                        insurancePolicyPremiumDetail.bIntegral = "0";
                    }
                    request.applyUnderwriting.bIntegral = insurancePolicyPremiumDetail.bIntegral;

                    BigDecimal carShipTax = new BigDecimal(request.applyUnderwriting.carshipTax);

                    BigDecimal ciInsuredPremium = new BigDecimal(getPremiumCalibrateResponse.data.ciInsuredPremium);
                    BigDecimal biInsuredPremium = new BigDecimal(getPremiumCalibrateResponse.data.biInsuredPremium);
                    BigDecimal ciCarShipTax = new BigDecimal("0.00");
                    BigDecimal biCarShipTax = new BigDecimal("0.00");
                    if (request.applyUnderwriting.hasCompulsoryInsurance && request.applyUnderwriting.hasCommercialInsurance) {
                        ciCarShipTax = carShipTax;
                        ciInsuredPremium = ciInsuredPremium.add(ciCarShipTax);
                    } else if (!request.applyUnderwriting.hasCompulsoryInsurance && request.applyUnderwriting.hasCommercialInsurance) {
                        biCarShipTax = carShipTax;
                        biInsuredPremium = biInsuredPremium.add(biCarShipTax);
                    } else if (request.applyUnderwriting.hasCompulsoryInsurance) {
                        ciCarShipTax = carShipTax;
                        ciInsuredPremium = ciInsuredPremium.add(ciCarShipTax);
                    } else {
                        return json(BaseResponse.CODE_FAILURE, "投保失败", response);
                    }

                    request.applyUnderwriting.ciInsuredPremium = String.valueOf(ciInsuredPremium.doubleValue());
                    request.applyUnderwriting.biInsuredPremium = String.valueOf(biInsuredPremium.doubleValue());
                    request.applyUnderwriting.ciCarShipTax = String.valueOf(ciCarShipTax.doubleValue());
                    request.applyUnderwriting.biCarShipTax = String.valueOf(biCarShipTax.doubleValue());

                    flag = true;
                    break;
                }
            }

            if (flag) {
                response.data = new CarInsuranceBean.PremiumCalibrateAndApplyUnderwriting();
                actionBean.body = JsonKit.bean2Json(request);
                CarInsuranceBean.ApplyUnderwritingResponse applyUnderwritingResponse = JsonKit.json2Bean(applyUnderwriting(actionBean), CarInsuranceBean.ApplyUnderwritingResponse.class);

                if (applyUnderwritingResponse != null && applyUnderwritingResponse.code == BaseResponse.CODE_SUCCESS && applyUnderwritingResponse.data != null) {
                    response.data.applyUnderwriting = applyUnderwritingResponse.data;
                    response.data.insurancePolicyPremiumDetails = getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails;
                    return json(BaseResponse.CODE_SUCCESS, "申请核保成功", response);
                } else {
                    if (applyUnderwritingResponse == null) {
                        return json(BaseResponse.CODE_FAILURE, "申请核保失败", response);
                    } else {
                        response.code = applyUnderwritingResponse.code;
                        response.message = applyUnderwritingResponse.message;
                        response.data.insurancePolicyPremiumDetails = getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails;
                        return json(response);
                    }
                }
            } else {
                return json(BaseResponse.CODE_FAILURE, "insurerCode参数有误", response);
            }
        } else {
            return premiumCalibrate;
        }
    }

    /**
     * 获取保险公司投保声明
     * FINISH: 2018/7/4
     *
     * @param actionBean 请求参数
     * @return 响应json
     */
    public String getInsuranceStatement(ActionBean actionBean) {
        CarInsuranceBean.GetInsuranceStatementRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetInsuranceStatementRequest.class);
        CarInsuranceBean.GetInsuranceStatementResponse response = new CarInsuranceBean.GetInsuranceStatementResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetInsuranceStatementRequest getInsuranceStatementRequest = new ExtendCarInsurancePolicy.GetInsuranceStatementRequest();
        getInsuranceStatementRequest.insurerCodes = request.insurerCode;

        ExtendCarInsurancePolicy.GetInsuranceStatementResponse result = new CarInsuranceHttpRequest<>(get_insurance_statement, getInsuranceStatementRequest, ExtendCarInsurancePolicy.GetInsuranceStatementResponse.class).post();

        if (result == null) {
            result = new ExtendCarInsurancePolicy.GetInsuranceStatementResponse();
            dealNullResponse(result);
        }

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                if (result.data != null && !result.data.isEmpty()) {

                    ExtendCarInsurancePolicy.InsuranceStatement insuranceStatement = result.data.get(0);
                    response.data = insuranceStatement.statementContent;

                    str = json(BaseResponse.CODE_SUCCESS, "获取支付链接成功", response);
                } else {
                    str = json(BaseResponse.CODE_FAILURE, "获取投保说明失败", response);
                }
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", response);
        }

        return str;
    }

    /**
     * 获取支付连接
     * FINISH: 2018/4/10
     *
     * @param actionBean 请求体
     * @return 响应json
     */
    public String getPayLink(ActionBean actionBean) {
        CarInsuranceBean.GetPayLinkRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.GetPayLinkRequest.class);
        CarInsuranceBean.GetPayLinkResponse response = new CarInsuranceBean.GetPayLinkResponse();

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
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status = CustWarrantyCost.PAY_STATUS_CANCEL;
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.payMoney = "0.00";
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo = "";
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo = "";
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time = String.valueOf(System.currentTimeMillis());
                    updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status = CustWarranty.POLICY_STATUS_INVALID;
                    int update = custWarrantyDao.updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance);

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
        CarInsuranceBean.VerifyPhoneCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.VerifyPhoneCodeRequest.class);
        CarInsuranceBean.VerifyPhoneCodeResponse response = new CarInsuranceBean.VerifyPhoneCodeResponse();

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
                if (StringKit.equals(applyState, CustWarranty.APPLY_UNDERWRITING_SUCCESS)) {
                    warrantyStatus = CustWarrantyCost.PAY_STATUS_WAIT;
                } else if (StringKit.equals(applyState, CustWarranty.APPLY_UNDERWRITING_PROCESSING)) {
                    warrantyStatus = CustWarrantyCost.APPLY_UNDERWRITING_PROCESSING;
                }
            } else {
                warrantyStatus = CustWarranty.POLICY_STATUS_INVALID;
                response.data = new ExtendCarInsurancePolicy.PhoneCode();
            }

            UpdateInsurancePolicyProPolicyNoForCarInsurance insurance = new UpdateInsurancePolicyProPolicyNoForCarInsurance();
            insurance.bizId = request.bizID;
            insurance.biProposalNo = result.data.biProposalNo;
            insurance.ciProposalNo = result.data.ciProposalNo;
            insurance.warrantyStatus = warrantyStatus;
            int update = custWarrantyDao.updateInsurancePolicyProPolicyNoForCarInsurance(insurance);

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
        CarInsuranceBean.ReGetPhoneVerifyCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.ReGetPhoneVerifyCodeRequest.class);
        CarInsuranceBean.ReGetPhoneVerifyCodeResponse response = new CarInsuranceBean.ReGetPhoneVerifyCodeResponse();

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
        CarInsuranceBean.ResolveIdentityCardRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.ResolveIdentityCardRequest.class);
//        CarInsurance.ResolveIdentityCardRequest request = new CarInsurance.ResolveIdentityCardRequest();
        CarInsuranceBean.ResolveIdentityCardResponse response = new CarInsuranceBean.ResolveIdentityCardResponse();

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
                str = json(BaseResponse.CODE_FAILURE, "识别图片失败(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "识别图片失败(" + result.msgCode + ")", response);
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
        CarInsuranceBean.ResolveDrivingLicenseRequest request = JsonKit.json2Bean(actionBean.body, CarInsuranceBean.ResolveDrivingLicenseRequest.class);
        CarInsuranceBean.ResolveDrivingLicenseResponse response = new CarInsuranceBean.ResolveDrivingLicenseResponse();

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
//                if (!StringKit.isEmpty(request.imgJustBase64) && result.data != null) {
//                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
//                    request1.base64 = request.imgJustBase64;
//                    request1.fileKey = MD5Kit.MD5Digest(result.data.engineNo + result.data.frameNo + result.data.fileNumber + "1");
//                    request1.fileName = result.data.fileNumber + "1";
//                    FileUpload.getInstance().uploadByBase64(request1);
//                }
//
//                if (!StringKit.isEmpty(request.imgBackBase64) && result.data != null) {
//                    FileUpload.UploadByBase64Request request1 = new FileUpload.UploadByBase64Request();
//                    request1.base64 = request.imgBackBase64;
//                    request1.fileKey = MD5Kit.MD5Digest(result.data.engineNo + result.data.frameNo + result.data.fileNumber + "2");
//                    request1.fileName = result.data.fileNumber + "2";
//                    FileUpload.getInstance().uploadByBase64(request1);
//                }
                str = json(BaseResponse.CODE_SUCCESS, "行驶证信息获取成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, "识别图片失败(" + result.msgCode + ")", response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "识别图片失败(" + result.msgCode + ")", response);
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

        String applyState = StringKit.equals(request.state, "1") ? CustWarranty.APPLY_UNDERWRITING_SUCCESS : CustWarranty.APPLY_UNDERWRITING_FAILURE;

        UpdateInsurancePolicyStatusForCarInsurance insurance = new UpdateInsurancePolicyStatusForCarInsurance();

        String payState = "";
        String warrantyStatus = "";
        if (StringKit.equals(applyState, CustWarranty.APPLY_UNDERWRITING_SUCCESS)) {
            payState = CustWarrantyCost.PAY_STATUS_WAIT;
            warrantyStatus = CustWarranty.POLICY_STATUS_PENDING;
        } else if (StringKit.equals(applyState, CustWarranty.APPLY_UNDERWRITING_PROCESSING)) {
            payState = CustWarrantyCost.PAY_STATUS_WAIT;
            warrantyStatus = CustWarranty.POLICY_STATUS_PENDING;
        } else if (StringKit.equals(applyState, CustWarranty.APPLY_UNDERWRITING_FAILURE)) {
            payState = CustWarrantyCost.PAY_STATUS_CANCEL;
            warrantyStatus = CustWarranty.POLICY_STATUS_INVALID;
        }

        insurance.pay_status = payState;
        insurance.warranty_status = warrantyStatus;
        insurance.biProposalNo = request.data.biProposalNo;
        insurance.ciProposalNo = request.data.ciProposalNo;
        insurance.bizId = request.data.bizID;
        insurance.thpBizID = request.data.thpBizID;

        if (!StringKit.isEmpty(request.data.bizID) || !StringKit.isEmpty(request.data.thpBizID)) {
            int update = custWarrantyDao.updateInsurancePolicyStatusForCarInsurance(insurance);
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
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status = CustWarrantyCost.PAY_STATUS_CANCEL;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.payMoney = "0.00";
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo = "";
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo = "";
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time = String.valueOf(System.currentTimeMillis());
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status = CustWarranty.POLICY_STATUS_INVALID;
            } else {
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status = CustWarrantyCost.PAY_STATUS_SUCCESS;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.payMoney = request.data.payMoney;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo = request.data.ciPolicyNo;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo = request.data.biPolicyNo;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = Time2Kit.parseMillisecondByShowDate(sdf, request.data.payTime);
                if (StringKit.isEmpty(time)) {
                    time = String.valueOf(System.currentTimeMillis());
                }
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time = time;
                updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status = CustWarranty.POLICY_STATUS_WAITING;

                List<CustWarrantyCar> warrantyUuidByBizId = carInfoDao.findWarrantyUuidByBizId(request.data.bizID);

                CustWarrantyCost custWarrantyCostModel = new CustWarrantyCost();
                for (CustWarrantyCar custWarrantyCar : warrantyUuidByBizId) {
                    if (!StringKit.isEmpty(custWarrantyCar.warranty_uuid)) {
                        custWarrantyCostModel.warranty_uuid = custWarrantyCar.warranty_uuid;
                        CustWarranty insurancePolicyDetailByWarrantyUuid = custWarrantyDao.findInsurancePolicyDetailByWarrantyUuid(custWarrantyCar.warranty_uuid);
                        if (insurancePolicyDetailByWarrantyUuid == null) {
                            continue;
                        }
                        List<CustWarrantyCost> custWarrantyCost = custWarrantyCostDao.findCustWarrantyCost(custWarrantyCostModel);
                        if (custWarrantyCost == null) {
                            continue;
                        }

                        BigDecimal premium = new BigDecimal(0);

                        for (CustWarrantyCost warrantyCostModel : custWarrantyCost) {
                            if (warrantyCostModel != null) {
                                if (!StringKit.isEmpty(insurancePolicyDetailByWarrantyUuid.channel_id)) {
                                    TaskResultDataBean taskResultDataBean1 = new TaskResultDataBean();
                                    taskResultDataBean1.dataType = "2";
                                    taskResultDataBean1.dataAmount = warrantyCostModel.premium;
                                    taskResultDataBean1.userType = "1";
                                    taskResultDataBean1.userId = insurancePolicyDetailByWarrantyUuid.channel_id;
                                    taskResultDataBean1.time = time;
                                    taskResultDataBean1.sign = SignatureTools.sign(taskResultDataBean1.toString(), SignatureTools.SIGN_TASK_RSA_PRIVATE_KEY);
                                    taskResultClient.updateTaskResult(taskResultDataBean1);

                                    TaskResultDataBean taskResultDataBean2 = new TaskResultDataBean();
                                    taskResultDataBean2.dataType = "2";
                                    taskResultDataBean2.dataAmount = warrantyCostModel.premium;
                                    taskResultDataBean2.userType = "1";
                                    taskResultDataBean2.userId = insurancePolicyDetailByWarrantyUuid.channel_id;
                                    taskResultDataBean2.time = time;
                                    taskResultDataBean2.sign = SignatureTools.sign(taskResultDataBean2.toString(), SignatureTools.SIGN_TASK_RSA_PRIVATE_KEY);
                                    taskResultClient.updateTaskResult(taskResultDataBean2);

                                    TaskResultDataBean taskResultDataBean3 = new TaskResultDataBean();
                                    taskResultDataBean3.dataType = "4";
                                    taskResultDataBean3.dataAmount = "2";
                                    taskResultDataBean3.userType = "1";
                                    taskResultDataBean3.userId = insurancePolicyDetailByWarrantyUuid.channel_id;
                                    taskResultDataBean3.time = time;
                                    taskResultDataBean3.sign = SignatureTools.sign(taskResultDataBean3.toString(), SignatureTools.SIGN_TASK_RSA_PRIVATE_KEY);
                                    taskResultClient.updateTaskResult(taskResultDataBean3);
                                }

                                if (!StringKit.isEmpty(insurancePolicyDetailByWarrantyUuid.agent_id)) {
                                    TaskResultDataBean taskResultDataBean1 = new TaskResultDataBean();
                                    taskResultDataBean1.dataType = "2";
                                    taskResultDataBean1.dataAmount = warrantyCostModel.premium;
                                    taskResultDataBean1.userType = "2";
                                    taskResultDataBean1.userId = insurancePolicyDetailByWarrantyUuid.agent_id;
                                    taskResultDataBean1.time = time;
                                    taskResultDataBean1.sign = SignatureTools.sign(taskResultDataBean1.toString(), SignatureTools.SIGN_TASK_RSA_PRIVATE_KEY);
                                    taskResultClient.updateTaskResult(taskResultDataBean1);

                                    TaskResultDataBean taskResultDataBean2 = new TaskResultDataBean();
                                    taskResultDataBean2.dataType = "2";
                                    taskResultDataBean2.dataAmount = warrantyCostModel.premium;
                                    taskResultDataBean2.userType = "2";
                                    taskResultDataBean2.userId = insurancePolicyDetailByWarrantyUuid.agent_id;
                                    taskResultDataBean2.time = time;
                                    taskResultDataBean2.sign = SignatureTools.sign(taskResultDataBean2.toString(), SignatureTools.SIGN_TASK_RSA_PRIVATE_KEY);
                                    taskResultClient.updateTaskResult(taskResultDataBean2);

                                    TaskResultDataBean taskResultDataBean3 = new TaskResultDataBean();
                                    taskResultDataBean3.dataType = "4";
                                    taskResultDataBean3.dataAmount = "2";
                                    taskResultDataBean3.userType = "2";
                                    taskResultDataBean3.userId = insurancePolicyDetailByWarrantyUuid.agent_id;
                                    taskResultDataBean3.time = time;
                                    taskResultDataBean3.sign = SignatureTools.sign(taskResultDataBean3.toString(), SignatureTools.SIGN_TASK_RSA_PRIVATE_KEY);
                                    taskResultClient.updateTaskResult(taskResultDataBean3);
                                }

                                if (StringKit.isNumeric(warrantyCostModel.premium)) {
                                    premium = premium.add(new BigDecimal(warrantyCostModel.premium));
                                }
                            }
                        }

                        Double custWarrantyBrokerageCarIntegral = custWarrantyBrokerageDao.findCustWarrantyBrokerageCarIntegral(insurancePolicyDetailByWarrantyUuid.warranty_uuid);

                        if (custWarrantyBrokerageCarIntegral == null) {
                            continue;
                        }

                        BigDecimal integral = new BigDecimal(custWarrantyBrokerageCarIntegral);

                        if (StringKit.isInteger(insurancePolicyDetailByWarrantyUuid.product_id)) {
                            ProductBrokerageBean productBrokerageBean = new ProductBrokerageBean();
                            productBrokerageBean.productId = Long.valueOf(insurancePolicyDetailByWarrantyUuid.product_id);
                            productBrokerageBean.managerUuid = insurancePolicyDetailByWarrantyUuid.manager_uuid;
                            productBrokerageBean.payTimes = 1;

                            if (StringKit.isInteger(insurancePolicyDetailByWarrantyUuid.channel_id)) {
                                productBrokerageBean.channelId = Long.valueOf(insurancePolicyDetailByWarrantyUuid.channel_id);
                            }

                            if (StringKit.isInteger(insurancePolicyDetailByWarrantyUuid.agent_id)) {
                                productBrokerageBean.agentId = Long.valueOf(insurancePolicyDetailByWarrantyUuid.agent_id);
                            }

                            ProductBrokerageInfoBean brokerage = productClient.getBrokerage(productBrokerageBean);

                            BigDecimal managerBrokerage = new BigDecimal("0.00");
                            BigDecimal channelBrokerage = new BigDecimal("0.00");
                            BigDecimal agentBrokerage = new BigDecimal("0.00");

                            BigDecimal managerRate = new BigDecimal("0.00");
                            BigDecimal channelRate = new BigDecimal("0.00");
                            BigDecimal agentRate = new BigDecimal("0.00");

                            if (brokerage != null) {
                                managerRate = new BigDecimal(brokerage.platformBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);
                                channelRate = new BigDecimal(brokerage.channelBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);
                                agentRate = new BigDecimal(brokerage.agentBrokerage).divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_DOWN);

                                managerBrokerage = managerRate.multiply(premium);
                                channelBrokerage = channelRate.multiply(premium);
                                agentBrokerage = agentRate.multiply(premium);
                            }

                            BigDecimal rate = new BigDecimal("0.0000");
                            if (premium.compareTo(BigDecimal.ZERO) > 0) {
                                rate = integral.divide(premium, 6, BigDecimal.ROUND_HALF_DOWN);
                            }

                            CustWarrantyBrokerage custWarrantyBrokerage = new CustWarrantyBrokerage();

                            custWarrantyBrokerage.warranty_uuid = custWarrantyCar.warranty_uuid;
                            custWarrantyBrokerage.updated_at = String.valueOf(System.currentTimeMillis());
                            custWarrantyBrokerage.setBrokerage(integral, integral, managerBrokerage, channelBrokerage, agentBrokerage);
                            custWarrantyBrokerage.setBrokerageRate(rate, rate, managerRate, channelRate, agentRate);

                            custWarrantyBrokerageDao.updateCustWarrantyBrokerageForCar(custWarrantyBrokerage);
                        }

                    }
                }

            }

            int update = custWarrantyDao.updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance);

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
        updateInsurancePolicyExpressInfoForCarInsurance.addresseeDetails = request.data.addresseeDetails;
        updateInsurancePolicyExpressInfoForCarInsurance.addresseeProvince = request.data.addresseeProvince;
        updateInsurancePolicyExpressInfoForCarInsurance.addresseeCity = request.data.addresseeCity;
        updateInsurancePolicyExpressInfoForCarInsurance.addresseeCounty = request.data.addresseeCounty;

        int update = custWarrantyDao.updateInsurancePolicyExpressInfoForCarInsurance(updateInsurancePolicyExpressInfoForCarInsurance);

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
    private String dealResultAndResponse(CarInsuranceBean.GetCarInfoResponse response, ExtendCarInsurancePolicy.GetCarInfoResponse result) {
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
                String calculateDateByShowDate = Time2Kit.parseMillisecondByShowDate(sdf, result.data.firstRegisterDate);

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
    private List<CarInsuranceBean.InsuranceInfo> dealCoverageList(List<ExtendCarInsurancePolicy.InsuranceInfoDetail> data) {
        List<CarInsuranceBean.InsuranceInfo> list = new ArrayList<>();
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
            CarInsuranceBean.InsuranceInfo insuranceInfo = new CarInsuranceBean.InsuranceInfo();
            insuranceInfo.coverageCode = datum.coverageCode;

            insuranceInfo.dealInsuranceInfoSort();

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

        list.sort(new Comparator<CarInsuranceBean.InsuranceInfo>() {
            @Override
            public int compare(CarInsuranceBean.InsuranceInfo o1, CarInsuranceBean.InsuranceInfo o2) {
                return o1.sort - o2.sort;
            }
        });

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
        boolean verify = false;
        if (!StringKit.isEmpty(signToken)) {
            String[] split = signToken.split("\\*");

            if (split.length == 2) {
                boolean frameNo = SignatureTools.verify(carInfo.frameNo, split[0], SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);
                boolean engineNo = SignatureTools.verify(carInfo.engineNo, split[1], SignatureTools.SIGN_CAR_RSA_PUBLIC_KEY);
                verify = frameNo || engineNo;
            }
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
    private CheckCoverageListResult checkCoverageList(List<CarInsuranceBean.InsuranceInfo> source, List<CarInsuranceBean.InsuranceInfo> checkList) {
        CheckCoverageListResult checkCoverageListResult = new CheckCoverageListResult();
        checkCoverageListResult.result = true;
        checkCoverageListResult.message = "";

        checkCoverageListResult.coverageList = new ArrayList<>();
        Map<String, CarInsuranceBean.InsuranceInfo> map = new LinkedHashMap<>();

        for (CarInsuranceBean.InsuranceInfo insuranceInfo : source) {
            map.put(insuranceInfo.coverageCode, insuranceInfo);
            if (StringKit.equals(insuranceInfo.hasExcessOption, "1")) {
                map.put("M" + insuranceInfo.coverageCode, insuranceInfo);
            }
        }


        for (CarInsuranceBean.InsuranceInfo insuranceInfo : checkList) {
            // 校验提交的数据的选项是否符合规定
            CarInsuranceBean.InsuranceInfo sourceInfo = map.get(insuranceInfo.coverageCode);
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
            if (StringKit.equals(insuranceInfoDetail.coverageCode, CustWarrantyCar.COVERAGE_CODE_Z2) && StringKit.equals(insuranceInfoDetail.insuredAmount, "Y")) {
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
            if (StringKit.equals(insuranceInfoDetail.coverageCode, CustWarrantyCar.COVERAGE_CODE_F) && StringKit.equals(insuranceInfoDetail.insuredAmount, "Y")) {
                boolean flag = false;
                CarInsuranceBean.InsuranceInfo whole = map.get(insuranceInfoDetail.coverageCode);
                for (String s : whole.sourceOption) {
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
                if (!checkCoverageListResult.hasCompulsoryInsurance && StringKit.equals(insuranceInfo.coverageCode, CustWarrantyCar.COVERAGE_CODE_FORCEPREMIUM)) {
                    checkCoverageListResult.hasCompulsoryInsurance = true;
                } else if (!checkCoverageListResult.hasCommercialInsurance && !StringKit.equals(insuranceInfo.coverageCode, CustWarrantyCar.COVERAGE_CODE_FORCEPREMIUM)) {
                    checkCoverageListResult.hasCommercialInsurance = true;
                }
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

    public static class CheckCoverageListResult {

        // 是否有效
        public boolean result;

        // 错误信息
        String message;

        // 校验正常后的实际提交险别列表
        List<ExtendCarInsurancePolicy.InsuranceInfoDetail> coverageList;

        boolean hasCompulsoryInsurance = false;
        boolean hasCommercialInsurance = false;

        // 获取玻璃险的对应type，只有确定玻璃险提交数据正确才有意义
        public String getFType(String text) {
            switch (text) {
                case "国产":
                    return "1";
                case "进口":
                    return "2";
                default:
                    return "";
            }
        }

        // 获取玻璃险的对应type，只有确定玻璃险提交数据正确才有意义
        public String getFText(String text) {
            switch (text) {
                case "1":
                    return "国产";
                case "2":
                    return "进口";
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
    private String dealInsurancePolicyInfoForShowString(List<ExtendCarInsurancePolicy.InsurancePolicyInfo> list) {
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
    private List<CarInsuranceBean.InsuranceInfo> dealInsurancePolicyInfoForShowList(List<ExtendCarInsurancePolicy.InsurancePolicyInfo> list) {
        Map<String, ExtendCarInsurancePolicy.InsurancePolicyInfo> map = new HashMap<>();
        for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : list) {
            if (insurancePolicyInfo.coverageCode.startsWith("M")) {
                map.put(insurancePolicyInfo.coverageCode.substring(1, insurancePolicyInfo.coverageCode.length()), insurancePolicyInfo);
            }
        }

        List<CarInsuranceBean.InsuranceInfo> result = new ArrayList<>();
        for (ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo : list) {
            CarInsuranceBean.InsuranceInfo insuranceInfo = new CarInsuranceBean.InsuranceInfo();

            insuranceInfo.coverageCode = insurancePolicyInfo.coverageCode;
            insuranceInfo.coverageName = insurancePolicyInfo.coverageName;
            insuranceInfo.insuredAmount = insurancePolicyInfo.insuredAmount;
            insuranceInfo.insuredPremium = insurancePolicyInfo.insuredPremium;

            insuranceInfo.dealInsuranceInfoSort();

            if (!insurancePolicyInfo.coverageCode.startsWith("M")) {
                ExtendCarInsurancePolicy.InsurancePolicyInfo insurancePolicyInfo1 = map.get(insurancePolicyInfo.coverageCode);

                if (insurancePolicyInfo1 == null) {
                    insuranceInfo.isExcessOption = "0";
                } else {
                    insuranceInfo.isExcessOption = "1";

                    if (StringKit.isNumeric(insurancePolicyInfo.insuredPremium) && StringKit.isNumeric(insurancePolicyInfo1.insuredPremium)) {
                        insuranceInfo.insuredPremium = new DecimalFormat("#0.00").format(new BigDecimal(insurancePolicyInfo.insuredPremium).add(new BigDecimal(insurancePolicyInfo1.insuredPremium)).doubleValue());
                    }
                }

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

                if (StringKit.equals(insurancePolicyInfo.coverageCode, CustWarrantyCar.COVERAGE_CODE_FORCEPREMIUM)) {
                    result.add(0, insuranceInfo);
                } else {
                    result.add(insuranceInfo);
                }
            }
        }

        result.sort((o1, o2) -> o1.sort - o2.sort);

        return result;
    }


    /**
     * 检查提交的投保险别列表是否与实际的投保险别列表一致
     *
     * @param commit   提交的投保险别列表
     * @param ultimate 实际的投保险别列表
     * @return 是否一致
     */
    private boolean checkCommitEqualsUltimate(List<ExtendCarInsurancePolicy.InsuranceInfoDetail> commit, List<ExtendCarInsurancePolicy.InsurancePolicyInfo> ultimate) {

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
            return Time2Kit.parseMillisecondByShowDate(sdf, date);
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
                applyState = CustWarranty.APPLY_UNDERWRITING_SUCCESS;
            } else if (synchFlag == 1) {
                applyState = CustWarranty.APPLY_UNDERWRITING_PROCESSING;
            } else {
                applyState = "synchFlag = " + synchFlag;
            }
        }
        return applyState;
    }

}