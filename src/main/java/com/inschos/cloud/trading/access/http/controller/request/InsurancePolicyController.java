package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.InsurancePolicyAction;
import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 创建日期：2018/4/12 on 16:58
 * 描述：
 * 作者：zhangyunhe
 */
@Controller
@RequestMapping("/trade/")
public class InsurancePolicyController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private InsurancePolicyAction insurancePolicyAction;

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_policy_list_for_online_store")
    @ResponseBody
    public String getInsurancePolicyListForOnlineStore(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyListForOnlineStore(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_policy_list_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyListForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("download_insurance_policy_list_for_manager_system")
    @ResponseBody
    public String downInsurancePolicyListForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.downInsurancePolicyListForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_policy_detail_for_online_store")
    @ResponseBody
    public String getInsurancePolicyDetailForOnlineStore(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyDetail(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_policy_detail_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyDetailForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyDetail(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_policy_statistic_detail_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyStatisticForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyStatisticForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_policy_statement_list_for_manager_system")
    @ResponseBody
    public String getInsurancePolicyStatementListForManagerSystem(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyStatementListForManagerSystem(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_insurance_policy_list_by_actual_pay_time")
    @ResponseBody
    public String getInsurancePolicyListByActualPayTime(ActionBean actionBean) {
        return insurancePolicyAction.getInsurancePolicyListByActualPayTime(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("offline_insurance_policy_input")
    @ResponseBody
    public String offlineInsurancePolicyInput(ActionBean actionBean) {
        return insurancePolicyAction.offlineInsurancePolicyInput(actionBean);
    }

    @GetActionBeanAnnotation(isCheckAccess = false)
    @RequestMapping("setTest")
    @ResponseBody
    public String setTest(ActionBean actionBean) {
        return insurancePolicyAction.setTest();
    }



}
