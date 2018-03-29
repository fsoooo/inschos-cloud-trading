package com.inschos.cloud.trading.access.http.controller.action;

import com.inschos.cloud.trading.access.http.controller.action.car.CarInsuranceCommon;
import com.inschos.cloud.trading.access.http.controller.action.car.CarInsuranceHttpRequest;
import com.inschos.cloud.trading.access.http.controller.bean.CarInsurancePolicy;

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

    public void getProvinceCode() {
        CarInsuranceHttpRequest<CarInsurancePolicy.GetProvinceCodeRequest, CarInsurancePolicy.GetProvinceCodeResponse> carInsuranceHttpRequest = new CarInsuranceHttpRequest<>(get_province_code, new CarInsurancePolicy.GetProvinceCodeRequest());
        CarInsurancePolicy.GetProvinceCodeResponse result = carInsuranceHttpRequest.post();
        // 验签
        if (result.verify) {

        } else {

        }
    }

    /**
     * 查询城市
     */
    private static final String get_city_code = CarInsuranceCommon.getServerHost() + "/mdata/cities";

    /**
     * 保险公司支持的地区
     */
    private static final String get_area_by_insurance = CarInsuranceCommon.getServerHost() + "/mdata/areas";

    /**
     * 地区支持的保险公司
     */
    private static final String get_insurance_by_area = CarInsuranceCommon.getServerHost() + "/mdata/insurers";

    /**
     * 险别查询
     */
    private static final String get_insurance_info = CarInsuranceCommon.getServerHost() + "/mdata/risks";

    /**
     * 车辆信息(根据车牌号查询)
     */
    private static final String get_car_info_licence_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByLicenseNo";

    /**
     * 车辆信息(根据车架号查询)
     */
    private static final String get_car_info_frame_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByFrameNo";

    /**
     * 一键修正车辆信息
     */
    private static final String correct_car_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoRevision";

    /**
     * 车型信息
     */
    private static final String get_car_model = CarInsuranceCommon.getServerHost() + "/auto/modelExactness";

    /**
     * 车辆车型信息
     */
    private static final String get_car_model_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleAndModel";

    /**
     * 模糊匹配车型
     */
    private static final String get_car_model_by_key = CarInsuranceCommon.getServerHost() + "/auto/modelMistiness";

    /**
     * 获取投保起期
     */
    private static final String get_insurance_start_time = CarInsuranceCommon.getServerHost() + "/assist/effectiveDate";

    /**
     * 参考报价
     */
    private static final String get_premium = CarInsuranceCommon.getServerHost() + "/main/referenceQuote";

    /**
     * 精准报价
     */
    private static final String get_premium_calibrate = CarInsuranceCommon.getServerHost() + "/main/exactnessQuote";

    /**
     * 车辆定价因子信息接口
     */
    private static final String get_premium_factor = "http://apiplus-test.ztwltech.com/v2.0/assist/quoteFactors";

    /**
     * 申请核保
     */
    private static final String apply_underwriting = CarInsuranceCommon.getServerHost() + "/main/applyUnderwrite";

    /**
     * 回写核保信息（回调接口）
     */
    private static final String get_apply_underwriting_result = "";

    /**
     * 获取支付链接
     */
    private static final String get_pay_link = CarInsuranceCommon.getServerHost() + "/payment/payLink";

    /**
     * 手机号验证码接口
     */
    private static final String get_phone_verify_code = CarInsuranceCommon.getServerHost() + "/assist/sendBjVerifyCode";

    /**
     * 北京重新发送验证码接口
     */
    private static final String re_get_phone_verify_code = "http://api-mock.ztwltech.com/v2.0/assist/resendBjVerifyCode";

    /**
     * 回写保单信息(回调接口)
     */
    private static final String get_insurance_policy = "";

    /**
     * 回写配送信息(回调接口)
     */
    private static final String get_express_info = "";

    /**
     * 保险公司投保声明
     */
    private static final String get_insurance_instruction = CarInsuranceCommon.getServerHost() + "/assist/statement";


}
