package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.BillAction;
import com.inschos.cloud.trading.access.http.controller.action.InsurancePolicyAction;
import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 创建日期：2018/6/25 on 13:52
 * 描述：
 * 作者：zhangyunhe
 */
@Controller
@RequestMapping("/trade/")
public class BillController {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private BillAction billAction;

    @GetActionBeanAnnotation
    @RequestMapping("create_bill")
    @ResponseBody
    public String createBill(ActionBean actionBean) {
        return billAction.createBill(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("add_bill_detail")
    @ResponseBody
    public String addBillDetail(ActionBean actionBean) {
        return billAction.addBillDetail(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_bill_enable_insurance_policy_list")
    @ResponseBody
    public String getBillEnableInsurancePolicyList(ActionBean actionBean) {
        return billAction.getBillEnableInsurancePolicyList(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_bill_list")
    @ResponseBody
    public String getBillList(ActionBean actionBean) {
        return billAction.getBillList(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_bill_info")
    @ResponseBody
    public String getBillInfo(ActionBean actionBean) {
        return billAction.getBillInfo(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("get_bill_detail")
    @ResponseBody
    public String getBillDetail(ActionBean actionBean) {
        return billAction.getBillDetail(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("download_bill_detail")
    @ResponseBody
    public String downloadBillDetail(ActionBean actionBean) {
        return billAction.downloadBillDetail(actionBean);
    }

//    @GetActionBeanAnnotation
//    @RequestMapping("clearing_bill")
//    @ResponseBody
//    public String clearingBill(ActionBean actionBean) {
//        return billAction.clearingBill(actionBean);
//    }
//
//    @GetActionBeanAnnotation
//    @RequestMapping("cancel_clearing_bill")
//    @ResponseBody
//    public String cancelClearingBill(ActionBean actionBean) {
//        return billAction.cancelClearingBill(actionBean);
//    }

    @GetActionBeanAnnotation
    @RequestMapping("delete_bill")
    @ResponseBody
    public String deleteBill(ActionBean actionBean) {
        return billAction.deleteBill(actionBean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("delete_bill_detail")
    @ResponseBody
    public String deleteBillDetail(ActionBean actionBean) {
        return billAction.deleteBillDetail(actionBean);
    }

}
