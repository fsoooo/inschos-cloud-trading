package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.InsurancePolicyAction;
import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 创建日期：2018/3/22 on 11:05
 * 描述：
 * 作者：zhangyunhe
 */
@Controller
@RequestMapping("/trade/")
public class TradeController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private InsurancePolicyAction mInsurancePolicyAction;

    @GetActionBeanAnnotation
    @RequestMapping("/insurance_policy_list")
    @ResponseBody
    public String findInsurancePolicyListByUserId(ActionBean bean) {
        return "";
    }

    @GetActionBeanAnnotation
    @RequestMapping("/insurance_policy_detail")
    @ResponseBody
    public String findInsurancePolicyDetailByPrivateCode(ActionBean bean) {
        return "";
    }

//    // 保费试算
//    @GetActionBeanAnnotation
//    @RequestMapping("/premium_calculate")
//    @ResponseBody
//    public String premiumCalculate(ActionBean bean) {
//
//        JsonKit.json2Bean(bean.body, );
//
//        return "";
//    }
//
//    // 投保
//    @GetActionBeanAnnotation
//    @RequestMapping("/insure")
//    @ResponseBody
//    public String insure(ActionBean bean) {
//
//        JsonKit.json2Bean(bean.body, );
//
//        return "";
//    }


}
