package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.CarAction;
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
@RequestMapping("/trade/")
public class CarController {


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private CarAction carAction;

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_province_code")
    @ResponseBody
    public String getProvinceCode(ActionBean actionBean) {
        return carAction.getProvinceCode(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_city_code")
    @ResponseBody
    public String getCityCode(ActionBean actionBean) {
        return carAction.getCityCode(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_car_info_licence_number")
    @ResponseBody
    public String getCarInfoByLicenceNumber(ActionBean actionBean) {
        return carAction.getCarInfoByLicenceNumber(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_car_model")
    @ResponseBody
    public String getCarModel(ActionBean actionBean) {
        return carAction.getCarModel(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("car_insurance/get_car_model_info")
    @ResponseBody
    public String getCarModelInfo(ActionBean actionBean) {
        return carAction.getCarModelInfo(actionBean);
    }




}
