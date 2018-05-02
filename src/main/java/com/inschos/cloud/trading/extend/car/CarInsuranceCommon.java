package com.inschos.cloud.trading.extend.car;

/**
 * 创建日期：2018/3/29 on 13:54
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInsuranceCommon {

    private static final String SERVER_HOST = "http://apiplus.ztwltech.com/v2.0";
//    private static final String SERVER_HOST = "http://api-mock.ztwltech.com/apply-mock/v2.0";
    private static final String RECEIVER_SERVER_HOST = "http://http://59.110.136.249:9200/trading/trade/";

    /**
     * 查询省代码
     */
    public static final String get_province_code = CarInsuranceCommon.getServerHost() + "/mdata/provinces";

    /**
     * 查询城市
     */
    public static final String get_city_code = CarInsuranceCommon.getServerHost() + "/mdata/cities";

    /**
     * 地区支持的保险公司
     */
    public static final String get_insurance_by_area = CarInsuranceCommon.getServerHost() + "/mdata/insurers";

    /**
     * 险别查询
     */
    public static final String get_insurance_info = CarInsuranceCommon.getServerHost() + "/mdata/risks";

    /**
     * 车辆号码信息(根据车牌号查询)
     */
    public static final String get_car_info_licence_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByLicenseNo";

    /**
     * 车辆号码信息(根据车架号查询)
     */
    public static final String get_car_info_frame_number = CarInsuranceCommon.getServerHost() + "/auto/vehicleInfoByFrameNo";

    /**
     * 车型信息
     */
    public static final String get_car_model = CarInsuranceCommon.getServerHost() + "/auto/modelExactness";

    /**
     * 车辆号码与车型信息
     */
    public static final String get_car_model_info = CarInsuranceCommon.getServerHost() + "/auto/vehicleAndModel";

    /**
     * 模糊匹配车型
     */
    public static final String get_car_model_by_key = CarInsuranceCommon.getServerHost() + "/auto/modelMistiness";

    /**
     * 获取投保起期
     */
    public static final String get_insurance_start_time = CarInsuranceCommon.getServerHost() + "/assist/effectiveDate";

    /**
     * 参考报价
     */
    public static final String get_premium = CarInsuranceCommon.getServerHost() + "/main/referenceQuote";

    /**
     * 精准报价
     */
    public static final String get_premium_calibrate = CarInsuranceCommon.getServerHost() + "/main/exactnessQuote";

    /**
     * 申请核保
     */
    public static final String apply_underwriting = CarInsuranceCommon.getServerHost() + "/main/applyUnderwrite";

    /**
     * 获取支付链接
     */
    public static final String get_pay_link = CarInsuranceCommon.getServerHost() + "/payment/payLink";

    /**
     * 手机号验证码接口
     */
    public static final String verify_phone_code = CarInsuranceCommon.getServerHost() + "/assist/sendBjVerifyCode";

    /**
     * 获取北京发送验证码接口
     */
    public static final String get_phone_verify_code = CarInsuranceCommon.getServerHost() + "/assist/resendBjVerifyCode";

    /**
     * 解析身份证
     */
    public static final String resolve_identity_card = CarInsuranceCommon.getServerHost() + "/valueadd/analyzingCardInfo";

    /**
     * 解析行驶本
     */
    public static final String resolve_driving_license = CarInsuranceCommon.getServerHost() + "/valueadd/analyzingDriving";

    /**
     * 回写核保信息（回调接口）
     */
    private static final String get_apply_underwriting_result = getReceiverServerHost() + "/send_apply_underwriting_result";

    /**
     * 回写保单信息(回调接口)
     */
    private static final String get_insurance_policy = getReceiverServerHost() + "/send_insurance_policy";

    /**
     * 回写配送信息(回调接口)
     */
    private static final String get_express_info = getReceiverServerHost() + "/send__express_info";

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static String getReceiverServerHost() {
        return RECEIVER_SERVER_HOST;
    }

}
