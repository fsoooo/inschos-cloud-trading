package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.CarInsuranceAction;
import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 创建日期：2018/3/30 on 12:16
 * 描述：
 * 作者：zhangyunhe
 */
@Controller
@RequestMapping("/web/car_insurance/")
public class CarInsuranceController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private CarInsuranceAction carInsuranceAction;

    @GetActionBeanAnnotation
    @RequestMapping("get_province_code")
    @ResponseBody
    public String getProvinceCode(ActionBean actionBean) {
        return carInsuranceAction.getProvinceCode(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_city_code")
    @ResponseBody
    public String getCityCode(ActionBean actionBean) {
        return carInsuranceAction.getCityCode(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_car_info")
    @ResponseBody
    public String getCarInfoByLicenceNumberOrFrameNumber(ActionBean actionBean) {
        return carInsuranceAction.getCarInfoByLicenceNumberOrFrameNumber(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_car_model")
    @ResponseBody
    public String getCarModel(ActionBean actionBean) {
        return carInsuranceAction.getCarModel(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_car_model_info")
    @ResponseBody
    public String getCarModelInfo(ActionBean actionBean) {
        return carInsuranceAction.getCarModelInfo(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_car_model_by_key")
    @ResponseBody
    public String getCarModelByKey(ActionBean actionBean) {
        return carInsuranceAction.getCarModelByKey(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_by_area")
    @ResponseBody
    public String getInsuranceByArea(ActionBean actionBean) {
        return carInsuranceAction.getInsuranceByArea(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_start_time")
    @ResponseBody
    public String getInsuranceStartTime(ActionBean actionBean) {
        return carInsuranceAction.getInsuranceStartTime(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_premium")
    @ResponseBody
    public String getPremium(ActionBean actionBean) {
        return carInsuranceAction.getPremium(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_info")
    @ResponseBody
    public String getInsuranceInfo(ActionBean actionBean) {
        return carInsuranceAction.getInsuranceInfo(actionBean);
    }

//    @GetActionBeanAnnotation
//    @RequestMapping("prepare_take_insure")
//    @ResponseBody
//    // NOTENABLED: 2018/4/3 目前的接口都是客户端单独请求
//    public String getInsuranceCompanyAndInsuranceStartTimeAndPremium(ActionBean actionBean) {
//        return carAction.getInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoAndPremium(actionBean);
//    }

    @GetActionBeanAnnotation
    @RequestMapping("prepare_take_insure")
    @ResponseBody
    public String getInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfo(ActionBean actionBean) {
        return carInsuranceAction.getInsuranceCompanyAndInsuranceStartTimeAndInsuranceInfoActionBean(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_premium_calibrate")
    @ResponseBody
    public String getPremiumCalibrate(ActionBean actionBean) {
        return carInsuranceAction.getPremiumCalibrate(actionBean);
    }

//    @GetActionBeanAnnotation
//    @RequestMapping("get_premium_factor")
//    @ResponseBody
//    public String getPremiumFactor(ActionBean actionBean) {
//        return carInsuranceAction.getPremiumFactor(actionBean);
//    }

//    @GetActionBeanAnnotation
//    @RequestMapping("apply_underwriting")
//    @ResponseBody
//    public String applyUnderwriting(ActionBean actionBean) {
//        return carInsuranceAction.applyUnderwriting(actionBean);
//    }

    @GetActionBeanAnnotation
    @RequestMapping("get_pay_link")
    @ResponseBody
    public String getPayLink(ActionBean actionBean) {
        return carInsuranceAction.getPayLink(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("verify_phone_code")
    @ResponseBody
    public String verifyPhoneCode(ActionBean actionBean) {
        return carInsuranceAction.verifyPhoneCode(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_phone_verify_code")
    @ResponseBody
    public String getPhoneVerifyCode(ActionBean actionBean) {
        return carInsuranceAction.getPhoneVerifyCode(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("direct_insure")
    @ResponseBody
    public String getPremiumCalibrateAndApplyUnderwriting(ActionBean actionBean) {
        return carInsuranceAction.getPremiumCalibrateAndApplyUnderwriting(actionBean);
    }


    @GetActionBeanAnnotation
    @RequestMapping("insurance_statement")
    @ResponseBody
    public String getInsuranceStatement(ActionBean actionBean) {
        return carInsuranceAction.getInsuranceStatement(actionBean);
    }


    @GetActionBeanAnnotation
    @RequestMapping("resolve_identity_card")
    @ResponseBody
    public String resolveIdentityCard(ActionBean actionBean) {
        return carInsuranceAction.resolveIdentityCard(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("resolve_driving_license")
    @ResponseBody
    public String resolveDrivingLicense(ActionBean actionBean) {
        return carInsuranceAction.resolveDrivingLicense(actionBean);
    }

    // 回调接口
    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("send_apply_underwriting_result")
    @ResponseBody
    public String sendApplyUnderwritingResult(ActionBean actionBean) {
        return carInsuranceAction.sendApplyUnderwritingResult(actionBean);
    }

    // 回调接口
    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("send_insurance_policy")
    @ResponseBody
    public String sendInsurancePolicy(ActionBean actionBean) {
        return carInsuranceAction.sendInsurancePolicy(actionBean);
    }

    // 回调接口
    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("send_express_info")
    @ResponseBody
    public String sendExpressInfo(ActionBean actionBean) {
        return carInsuranceAction.sendExpressInfo(actionBean);
    }
}
