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
import com.inschos.cloud.trading.extend.car.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
     * 查询省代码
     */
    private static final String get_province_code = CarInsuranceCommon.getServerHost() + "/mdata/provinces";

    // FINISH: 2018/3/30
    public String getProvinceCode(ActionBean actionBean) {
        CarInsurance.GetProvinceCodeRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetProvinceCodeRequest.class);
        CarInsurance.GetProvinceCodeResponse response = new CarInsurance.GetProvinceCodeResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetProvinceCodeRequest getProvinceCodeRequest = new ExtendCarInsurancePolicy.GetProvinceCodeRequest();

        ExtendCarInsurancePolicy.GetProvinceCodeResponse result = new CarInsuranceHttpRequest<>(get_province_code, getProvinceCodeRequest, ExtendCarInsurancePolicy.GetProvinceCodeResponse.class).post();
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
                        // TODO: 2018/4/3  记得给自己的系统存数据
                    } else {
                        str = json(BaseResponse.CODE_FAILURE, "获取省级列表失败", response);
                    }
                } else {
                    str = json(BaseResponse.CODE_SUCCESS, "获取省级列表成功", response);
                    // TODO: 2018/4/3  记得给自己的系统存数据
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
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = new ExtendCarInsurancePolicy.ProvinceCodeDetail();
                response.data.provinceCode = request.provinceCode;
                response.data.city = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取市级列表成功", response);

                // TODO: 2018/4/3  记得给自己的系统存数据
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

//    /**
//     * 保险公司支持的地区
//     */
//    private static final String get_area_by_insurance = CarInsuranceCommon.getServerHost() + "/mdata/areas";
//
//    public String getAreaByInsurance(ActionBean actionBean) {
//        ExtendCarInsurancePolicy.GetProvinceCodeRequest getProvinceCodeRequest = new ExtendCarInsurancePolicy.GetProvinceCodeRequest();
//
//        // TODO: 2018/3/29  从我们的request里面获取这个值
//        // getProvinceCodeRequest.insurerCode
//
//        ExtendCarInsurancePolicy.GetProvinceCodeResponse result = new CarInsuranceHttpRequest<>(get_area_by_insurance, getProvinceCodeRequest, ExtendCarInsurancePolicy.GetProvinceCodeResponse.class).post();
//        // 验签
//        String str;
//        // TODO: 2018/3/29 返回我们的response
//        if (result.verify) {
//
//        } else {
//            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
//        }
//        return "";
//    }

    /**
     * 地区支持的保险公司
     */
    private static final String get_insurance_by_area = CarInsuranceCommon.getServerHost() + "/mdata/insurers";

    // FINISH: 2018/3/31
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
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "获取保险公司成功", response);

                // TODO: 2018/4/8 验证这个保险公司的车险产品是否上架，可售
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

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetInsuranceInfoRequest getInsuranceInfoRequest = new ExtendCarInsurancePolicy.GetInsuranceInfoRequest();

        ExtendCarInsurancePolicy.GetInsuranceInfoResponse result = new CarInsuranceHttpRequest<>(get_insurance_info, getInsuranceInfoRequest, ExtendCarInsurancePolicy.GetInsuranceInfoResponse.class).post();
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = dealCoverageList(result.data);
                str = json(BaseResponse.CODE_SUCCESS, "获取险别列表成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    private List<CarInsurance.InsuranceInfo> dealCoverageList(List<ExtendCarInsurancePolicy.InsuranceInfo> data) {
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

        for (ExtendCarInsurancePolicy.InsuranceInfo datum : data) {
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
            }

            if (StringKit.equals(datum.coverageCode, "Z2")) {
                insuranceInfo.day = "30";
                insuranceInfo.minDay = String.valueOf(Z2_MIN_DAY);
                insuranceInfo.maxDay = String.valueOf(Z2_MAX_DAY);
                insuranceInfo.amount = "50";
                insuranceInfo.minAmount = String.valueOf(Z2_MIN_AMOUNT);
                insuranceInfo.maxAmount = String.valueOf(Z2_MAX_AMOUNT);
            }

            list.add(insuranceInfo);
        }

        return list;
    }

    /**
     * 自动判断根据车架号还是车牌号获取{@link #getCarInfoByLicenceNumber}{@link #getCarInfoByFrameNumber}
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
     * 车辆号码信息(根据车牌号查询)
     */
    private static final String get_car_info_licence_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByLicenseNo";

    // FINISH: 2018/3/30
    public String getCarInfoByLicenceNumber(ActionBean actionBean) {
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
     * 车辆号码信息(根据车架号查询)
     */
    private static final String get_car_info_frame_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByFrameNo";

    // FINISH: 2018/3/30
    public String getCarInfoByFrameNumber(ActionBean actionBean) {
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
     * 车辆信息(结果处理方法){@link #getCarInfoByLicenceNumber} {@link #getCarInfoByFrameNumber}
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
                String frameNo = "null";
                if (result.data.frameNo != null) {
                    frameNo = result.data.frameNo;
                }

                String engineNo = "null";
                if (result.data.engineNo != null) {
                    engineNo = result.data.engineNo;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String calculateDateByShowDate = getCalculateDateByShowDate(sdf, result.data.firstRegisterDate);

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
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }
        return str;
    }

//    /**
//     * 一键修正车辆信息
//     * <p>一键修正接口成本很高，所以限制的调用次数很少，默认10次，可根据实际成单量调整，此接口用于车辆信息接口带出的车架号、发动机号或者初等日期不对的情况，所以要在通过车辆信息接口获取到的信息不准的情况下再调用这个接口。
//     * </p>
//     */
//    private static final String correct_car_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoRevision";
//
//    public String correctCarInfo(ActionBean actionBean) {
//        ExtendCarInsurancePolicy.CorrectCarInfoRequest correctCarInfoRequest = new ExtendCarInsurancePolicy.CorrectCarInfoRequest();
//
//        // TODO: 2018/3/29  从我们的request里面获取这个值
//        // correctCarInfoRequest.licenseNo
//
//        ExtendCarInsurancePolicy.CorrectCarInfoResponse result = new CarInsuranceHttpRequest<>(correct_car_info, correctCarInfoRequest, ExtendCarInsurancePolicy.CorrectCarInfoResponse.class).post();
//        // 验签
//        String str;
//        // TODO: 2018/3/29 返回我们的response
//        if (result.verify) {
//
//        } else {
//            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
//        }
//
//        return "";
//    }

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
     * 车辆号码与车型信息
     */
    private static final String get_car_model_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleAndModel";

    // FINISH: 2018/3/31
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
                String calculateDateByShowDate = getCalculateDateByShowDate(sdf, result.data.firstRegisterDate);

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
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", response);
        }

        return str;
    }

    /**
     * 格式化时间戳用
     *
     * @param sdf      格式
     * @param showDate 时间
     * @return showDate指定sdf的格式
     */
    private String getCalculateDateByShowDate(SimpleDateFormat sdf, String showDate) {
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
     * 模糊匹配车型
     */
    private static final String get_car_model_by_key = CarInsuranceCommon.getServerHost() + "/auto/modelMistiness";

    // FINISH: 2018/3/31
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
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", response);
        }

        return str;
    }

    /**
     * 将获取保险公司、获取起保时间、获取险别列表合并为一个请求
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

        return json(BaseResponse.CODE_SUCCESS, "获取数据成功", response);
    }

//    /**
//     * 将获取保险公司、获取起保时间、获取险别列表、参考报价合并为一个请求
//     *
//     * @param actionBean 请求体
//     * @return 响应json
//     */
    // NOTENABLED: 2018/4/3 目前的接口都是客户端单独请求
//    public String getInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoAndPremium(ActionBean actionBean) {
//        CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumRequest.class);
//        CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumResponse response = new CarInsurance.GetInsuranceCompanyAndInsuranceStartTimeAndPremiumResponse();
//
//        String insuranceByArea = getInsuranceByArea(actionBean);
//        CarInsurance.GetInsuranceCompanyResponse getInsuranceCompanyResponse = JsonKit.json2Bean(insuranceByArea, CarInsurance.GetInsuranceCompanyResponse.class);
//
//        if (getInsuranceCompanyResponse == null || getInsuranceCompanyResponse.code != BaseResponse.CODE_SUCCESS) {
//            return insuranceByArea;
//        }
//
//        response.data.insuranceCompanies = getInsuranceCompanyResponse.data;
//
//        String insuranceStartTime = getInsuranceStartTime(actionBean);
//        CarInsurance.GetInsuranceStartTimeResponse getInsuranceStartTimeResponse = JsonKit.json2Bean(insuranceStartTime, CarInsurance.GetInsuranceStartTimeResponse.class);
//
//        if (getInsuranceStartTimeResponse == null || getInsuranceStartTimeResponse.code != BaseResponse.CODE_SUCCESS) {
//            return insuranceStartTime;
//        }
//
//        response.data.startTimeInfo = getInsuranceStartTimeResponse.data;
//
//        String insuranceInfo = getInsuranceInfo(actionBean);
//        CarInsurance.GetInsuranceInfoResponse getInsuranceInfoResponse = JsonKit.json2Bean(insuranceInfo, CarInsurance.GetInsuranceInfoResponse.class);
//
//        if (getInsuranceInfoResponse == null || getInsuranceInfoResponse.code != BaseResponse.CODE_SUCCESS) {
//            return insuranceInfo;
//        }
//
//        // TODO: 2018/3/31 需要知道险别列表是否必传  getInsuranceInfoResponse.data == null || getInsuranceInfoResponse.data.isEmpty()
//
//        String str;
//        if (getInsuranceCompanyResponse.data == null || getInsuranceCompanyResponse.data.isEmpty()) {
//            response.data.premiumInfo = null;
//            str = json(BaseResponse.CODE_SUCCESS, "获取成功", response);
//        } else {
//
//            ExtendCarInsurancePolicy.InsuranceCompany insuranceCompany = getInsuranceCompanyResponse.data.get(0);
//
//            request.insurerCode = insuranceCompany.insurerCode;
//
//            // TODO: 2018/3/31 处理险别列表
//            // request.coverageList
//
//            actionBean.body = JsonKit.bean2Json(request);
//
//            String premium = getPremium(actionBean);
//            CarInsurance.GetPremiumResponse getPremiumResponse = JsonKit.json2Bean(premium, CarInsurance.GetPremiumResponse.class);
//
//            if (getPremiumResponse == null || getPremiumResponse.code != BaseResponse.CODE_SUCCESS) {
//                return premium;
//            } else {
//                str = json(BaseResponse.CODE_SUCCESS, "获取成功", response);
//            }
//        }
//
//        return str;
//    }

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
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                response.data.biStartTimeValue = getCalculateDateByShowDate(sdf, result.data.biStartTime);
                response.data.ciStartTimeValue = getCalculateDateByShowDate(sdf, result.data.ciStartTime);
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

        // TODO: 2018/4/9 建表后
        // 将车辆信息存入我们自己的数据库
//        CarReconeByResponseNo = carRecordDao.findOneByResponseNo(request.carInfo.responseNo);
//
//        CarRecordModel carRecordModel = new CarRecordModel();
//        carRecordModel.car_code = request.carInfo.licenseNo;
//        carRecordModel.name = request.personInfo.ownerName;
//        carRecordModel.code = request.personInfo.ownerID;
//        carRecordModel.phone = request.personInfo.ownerMobile;
//        carRecordModel.frame_no = request.carInfo.frameNo;
//        carRecordModel.engine_no = request.carInfo.engineNo;
//        carRecordModel.vehicle_fgw_code = request.carInfo.vehicleFgwCode;
//        carRecordModel.vehicle_fgw_name = request.carInfo.vehicleFgwName;
//        carRecordModel.parent_veh_name = request.carInfo.parentVehName;
//        carRecordModel.brand_code = request.carInfo.brandCode;
//        carRecordModel.brand_name = request.carInfo.brandName;
//        carRecordModel.engine_desc = request.carInfo.engineDesc;
//        carRecordModel.new_car_price = request.carInfo.newCarPrice;
//        carRecordModel.purchase_price_tax = request.carInfo.purchasePriceTax;
//        carRecordModel.import_flag = request.carInfo.importFlag;
//        carRecordModel.seat = request.carInfo.seat;
//        carRecordModel.standard_name = request.carInfo.standardName;
//        carRecordModel.is_trans = request.carInfo.isTrans;
//        carRecordModel.remark = request.carInfo.remark;
//        carRecordModel.response_no = request.carInfo.responseNo;
//        carRecordModel.updated_at = String.valueOf(time);
//
//        if (oneByResponseNo == null) {
//            carRecordModel.created_at = String.valueOf(time);
//            carRecordDao.addCarRecord(carRecordModel);
//        } else {
//            carRecordDao.updateCarRecord(carRecordModel);
//        }

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
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", new BaseResponse());
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
            Long aLong = formatterDate(getPremiumCalibrateRequest.carInfo.transDate);
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
        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = new CarInsurance.GetPremiumCalibrateDetail();

                BigDecimal ci = new BigDecimal("0.0");
                BigDecimal bi = new BigDecimal("0.0");
                for (ExtendCarInsurancePolicy.InsurancePolicyPremiumDetail datum : result.data) {
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

                // TODO: 2018/4/9 整理一个
                // response.data.insurancePolicyPremiumDetails = result.data;

                str = json(BaseResponse.CODE_SUCCESS, "获取报价成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, result.msg + "(" + result.msgCode + ")", new BaseResponse());
        }

        return str;
    }

    private CheckCoverageListResult checkCoverageList(List<CarInsurance.InsuranceInfo> source, List<CarInsurance.InsuranceInfo> checkList) {
        CheckCoverageListResult checkCoverageListResult = new CheckCoverageListResult();
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
            ExtendCarInsurancePolicy.InsuranceInfoDetail insuranceInfoDetail = new ExtendCarInsurancePolicy.InsuranceInfoDetail();
            insuranceInfoDetail.coverageCode = insuranceInfo.coverageCode;

            // 是否可以不计免赔
            if (StringKit.equals(map.get(insuranceInfo.coverageCode).coverageCode, "1")) {
                ExtendCarInsurancePolicy.InsuranceInfoDetail excess = new ExtendCarInsurancePolicy.InsuranceInfoDetail();
                excess.coverageCode = "M" + insuranceInfo.coverageCode;

                if (StringKit.equals(insuranceInfo.isExcessOption, "1")) {
                    excess.insuredAmount = "Y";
                } else {
                    excess.insuredAmount = "";
                }
                checkCoverageListResult.coverageList.add(excess);
            }

            // 保额是否符合规则
            List<String> insuredAmountList = map.get(insuranceInfo.coverageCode).insuredAmountList;
            if (insuredAmountList != null && !insuredAmountList.isEmpty()) {
                boolean flag = false;
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
                    checkCoverageListResult.message = insuranceInfo.coverageName + "的保额非法";
                    checkCoverageListResult.coverageList = null;
                    break;
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
                    checkCoverageListResult.message = insuranceInfo.coverageName + "的保额必须是数字，Y或者N";
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
                        checkCoverageListResult.message = insuranceInfo.coverageName + "的天数非法";
                        checkCoverageListResult.coverageList = null;
                        break;
                    }
                } else {
                    checkCoverageListResult.result = false;
                    checkCoverageListResult.message = insuranceInfo.coverageName + "的天数必须是正整数";
                    checkCoverageListResult.coverageList = null;
                    break;
                }

                if (StringKit.isNumeric(insuranceInfo.amount)) {
                    Double aDouble = Double.valueOf(insuranceInfo.amount);
                    if (aDouble > Z2_MAX_AMOUNT || aDouble < Z2_MIN_AMOUNT) {
                        checkCoverageListResult.result = false;
                        checkCoverageListResult.message = insuranceInfo.coverageName + "的保额非法";
                        checkCoverageListResult.coverageList = null;
                        break;
                    }
                } else {
                    checkCoverageListResult.result = false;
                    checkCoverageListResult.message = insuranceInfo.coverageName + "的保额必须是数字";
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
                    checkCoverageListResult.message = insuranceInfo.coverageName + "的保额必须是数字";
                    checkCoverageListResult.coverageList = null;
                    break;
                }
            }

            checkCoverageListResult.coverageList.add(insuranceInfoDetail);
        }

        checkCoverageListResult.result = true;
        checkCoverageListResult.message = "";

        return checkCoverageListResult;
    }

    private static final int Z2_MIN_DAY = 1;
    private static final int Z2_MAX_DAY = 90;
    private static final double Z2_MIN_AMOUNT = 50;
    private static final double Z2_MAX_AMOUNT = 500;

    private static class CheckCoverageListResult {

        public boolean result;

        public String message;

        public List<ExtendCarInsurancePolicy.InsuranceInfoDetail> coverageList;

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
    }

    /**
     * 申请核保
     */
    private static final String apply_underwriting = CarInsuranceCommon.getServerHost() + "/main/applyUnderwrite";

    // FINISH: 2018/4/2
    public String applyUnderwriting(ActionBean actionBean) {
        CarInsurance.ApplyUnderwritingRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ApplyUnderwritingRequest.class);
        CarInsurance.ApplyUnderwritingResponse response = new CarInsurance.ApplyUnderwritingResponse();

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
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

        ExtendCarInsurancePolicy.ApplyUnderwritingResponse result = new CarInsuranceHttpRequest<>(apply_underwriting, applyUnderwritingRequest, ExtendCarInsurancePolicy.ApplyUnderwritingResponse.class).post();
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                response.data = result.data;
                str = json(BaseResponse.CODE_SUCCESS, "申请核保成功", response);

                // TODO: 2018/4/9 存保单
                // TODO: 2018/4/9 存保单车辆信息
                // TODO: 2018/4/9 存保单人员信息
                // FORCEPREMIUM 强险
//                InsurancePolicyModel ciProposal = new InsurancePolicyModel();
//                ciProposal.warranty_uuid = getThpBizID();
//                ciProposal.pro_policy_no = result.data.ciProposalNo;
//                ciProposal.premium = request.ciInsuredPremium;
//                ciProposal.start_time = result.data.;
//                start_time
//
//                InsurancePolicyModel biProposal = new InsurancePolicyModel();
//                biProposal.warranty_uuid = getThpBizID();
//                biProposal.pro_policy_no = result.data.biProposalNo;
//                biProposal.premium = request.biInsuredPremium;
//                start_time


                // public String warranty_code;

//                public String company_id;

//                public String user_id;

//                public String user_type;

//                public String agent_id;

//                public String ditch_id;

//                public String plan_id;

//                public String product_id;

//                public String start_time;

//                /**
//                 * 结束时间
//                 */
//                public String end_time;
//
//                /**
//                 * 保险公司ID
//                 */
//                public String ins_company_id;
//
//                /**
//                 * 购买份数
//                 */
//                public String count;
//
//                /**
//                 * 支付时间
//                 */
//                public String pay_time;
//
//                /**
//                 * 支付方式 1 银联 2 支付宝 3 微信 4现金
//                 */
//                public String pay_way;
//
//                /**
//                 * 分期方式
//                 */
//                public String by_stages_way;
//
//                /**
//                 * 佣金 0表示未结算，1表示已结算
//                 */
//                public String is_settlement;
//
//                /**
//                 * 电子保单下载地址
//                 */
//                public String warranty_url;
//
//                /**
//                 * 保单来源 1 自购 2线上成交 3线下成交 4导入
//                 */
//                public String warranty_from;
//
//                /**
//                 * 保单类型1表示个人保单，2表示团险保单，3表示车险保单
//                 */
//                public String type;
//
//                /**
//                 * 核保状态（01核保中 2核保失败，3核保成功
//                 */
//                public String check_status;
//
//                /**
//                 * 支付状态 0，1支付中2支付失败3支付成功，
//                 */
//                public String pay_status;
//
//                /**
//                 * 保单状态 1待处理 2待支付3待生效 4保障中5可续保，6已失效，7已退保  8已过保
//                 */
//                public String warranty_status;
//
//                /**
//                 * 创建时间
//                 */
//                public String created_at;
//
//                /**
//                 * 结束时间
//                 */
//                public String updated_at;
//
//                /**
//                 * 删除标识 0删除 1可用
//                 */
//                public String state;


//                insurancePolicyDao.addInsurancePolicy();

                if (StringKit.isInteger(result.data.synchFlag)) {
                    int synchFlag = Integer.valueOf(result.data.synchFlag);
                    if (synchFlag == 0) {

                    } else if (synchFlag == 1) {

                    }
                }

                // TODO: 2018/4/3 记得给自己的系统存保单
                // TODO: 2018/4/8 利用保险公司简称代码，获取产品id与保险公司id
                // TODO: 2018/4/8 代理人ID，渠道ID为0则为用户自主购买，计划书ID为0则为用户自主购买，产品ID

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

        if (request == null) {
            return json(BaseResponse.CODE_PARAM_ERROR, "解析错误", response);
        }

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
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

        if (getPremiumCalibrateResponse.data != null && !getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.isEmpty()) {
            // request.applyUnderwriting.bjCodeFlag = getPremiumCalibrateResponse.data.insurancePolicyPremiumDetails.get(0).;
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

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetPremiumFactorRequest getPremiumFactorRequest = new ExtendCarInsurancePolicy.GetPremiumFactorRequest();

        getPremiumFactorRequest.bizID = request.bizID;

        ExtendCarInsurancePolicy.GetPremiumFactorResponse result = new CarInsuranceHttpRequest<>(get_premium_factor, getPremiumFactorRequest, ExtendCarInsurancePolicy.GetPremiumFactorResponse.class).post();
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

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.GetPayLinkRequest getPayLinkRequest = new ExtendCarInsurancePolicy.GetPayLinkRequest();

        getPayLinkRequest.bizID = request.bizID;

        ExtendCarInsurancePolicy.GetPayLinkResponse result = new CarInsuranceHttpRequest<>(get_pay_link, getPayLinkRequest, ExtendCarInsurancePolicy.GetPayLinkResponse.class).post();
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

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.VerifyPhoneCodeRequest verifyPhoneCodeRequest = new ExtendCarInsurancePolicy.VerifyPhoneCodeRequest();

        verifyPhoneCodeRequest.bizID = request.bizID;
        verifyPhoneCodeRequest.verificationCode = request.verificationCode;

        ExtendCarInsurancePolicy.VerifyPhoneCodeResponse result = new CarInsuranceHttpRequest<>(verify_phone_code, verifyPhoneCodeRequest, ExtendCarInsurancePolicy.VerifyPhoneCodeResponse.class).post();
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

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest reGetPhoneVerifyCodeRequest = new ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeRequest();

        reGetPhoneVerifyCodeRequest.bizID = request.bizID;

        ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse result = new CarInsuranceHttpRequest<>(get_phone_verify_code, reGetPhoneVerifyCodeRequest, ExtendCarInsurancePolicy.ReGetPhoneVerifyCodeResponse.class).post();
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

        ExtendCarInsurancePolicy.GetInsuranceInstructionResponse result = new CarInsuranceHttpRequest<>(get_insurance_instruction, getInsuranceInstructionRequest, ExtendCarInsurancePolicy.GetInsuranceInstructionResponse.class).post();
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
     * 解析身份证
     */
    private static final String resolve_identity_card = CarInsuranceCommon.getServerHost() + "/valueadd/analyzingDriving";

    // FINISH: 2018/4/3
    public String resolveIdentityCard(ActionBean actionBean) {
        CarInsurance.ResolveIdentityCardRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ResolveIdentityCardRequest.class);
        CarInsurance.ResolveIdentityCardResponse response = new CarInsurance.ResolveIdentityCardResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.ResolveIdentityCardRequest resolveIdentityCardRequest = new ExtendCarInsurancePolicy.ResolveIdentityCardRequest();

        if (!StringKit.isEmpty(resolveIdentityCardRequest.frontCardUrl) || !StringKit.isEmpty(resolveIdentityCardRequest.frontCardBase64)) {
            resolveIdentityCardRequest.frontCardUrl = request.frontCardUrl;
            resolveIdentityCardRequest.frontCardBase64 = request.frontCardBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少正面信息", new BaseResponse());
        }

        if (!StringKit.isEmpty(resolveIdentityCardRequest.backCardUrl) || !StringKit.isEmpty(resolveIdentityCardRequest.backCardBase64)) {
            resolveIdentityCardRequest.backCardUrl = request.backCardUrl;
            resolveIdentityCardRequest.backCardBase64 = request.backCardBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少背面信息", new BaseResponse());
        }

        ExtendCarInsurancePolicy.ResolveIdentityCardResponse result = new CarInsuranceHttpRequest<>(resolve_driving_license, resolveIdentityCardRequest, ExtendCarInsurancePolicy.ResolveIdentityCardResponse.class).post();

        // TODO: 2018/4/3 记得存咱们的服务器

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                str = json(BaseResponse.CODE_SUCCESS, "身份证信息获取成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
    }

    /**
     * 解析行驶本
     */
    private static final String resolve_driving_license = CarInsuranceCommon.getServerHost() + "/valueadd/analyzingDriving";

    // FINISH: 2018/4/3
    public String resolveDrivingLicense(ActionBean actionBean) {
        CarInsurance.ResolveDrivingLicenseRequest request = JsonKit.json2Bean(actionBean.body, CarInsurance.ResolveDrivingLicenseRequest.class);
        CarInsurance.ResolveDrivingLicenseResponse response = new CarInsurance.ResolveDrivingLicenseResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        ExtendCarInsurancePolicy.ResolveDrivingLicenseRequest resolveDrivingLicenseRequest = new ExtendCarInsurancePolicy.ResolveDrivingLicenseRequest();

        if (!StringKit.isEmpty(resolveDrivingLicenseRequest.imgJustUrl) || !StringKit.isEmpty(resolveDrivingLicenseRequest.imgJustBase64)) {
            resolveDrivingLicenseRequest.imgJustUrl = request.imgJustUrl;
            resolveDrivingLicenseRequest.imgJustBase64 = request.imgJustBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少正面信息", new BaseResponse());
        }

        if (!StringKit.isEmpty(resolveDrivingLicenseRequest.imgBackUrl) || !StringKit.isEmpty(resolveDrivingLicenseRequest.imgBackBase64)) {
            resolveDrivingLicenseRequest.imgBackUrl = request.imgBackUrl;
            resolveDrivingLicenseRequest.imgJustBase64 = request.imgJustBase64;
        } else {
            return json(BaseResponse.CODE_FAILURE, "缺少背面信息", new BaseResponse());
        }

        ExtendCarInsurancePolicy.ResolveDrivingLicenseResponse result = new CarInsuranceHttpRequest<>(resolve_driving_license, resolveDrivingLicenseRequest, ExtendCarInsurancePolicy.ResolveDrivingLicenseResponse.class).post();

        // TODO: 2018/4/3 记得存咱们的服务器

        // 验签
        String str;
        if (result.verify) {
            if (result.state == CarInsuranceResponse.RESULT_OK) {
                str = json(BaseResponse.CODE_SUCCESS, "行驶证信息获取成功", response);
            } else {
                str = json(BaseResponse.CODE_FAILURE, result.msg, response);
            }
        } else {
            str = json(BaseResponse.CODE_FAILURE, "获取数据错误", new BaseResponse());
        }

        return str;
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

}
