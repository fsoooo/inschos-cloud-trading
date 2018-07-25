package com.inschos.cloud.trading.access.http.controller.request;


import com.inschos.cloud.trading.access.http.controller.action.BankAction;
import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * author   meiming_mm@163.com
 * date     2018/7/12
 * version  v1.0.0
 */
@Controller
@RequestMapping("/web/bank")
public class BankController {

    @Autowired
    private BankAction bankAction;

    @GetActionBeanAnnotation
    @RequestMapping("/add")
    @ResponseBody
    public String add(ActionBean bean){
        return bankAction.add(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/modify")
    @ResponseBody
    public String modify(ActionBean bean){
        return bankAction.modify(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/detail")
    @ResponseBody
    public String detail(ActionBean bean){
        return bankAction.detail(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/remove")
    @ResponseBody
    public String remove(ActionBean bean){
        return bankAction.remove(bean);
    }


    @GetActionBeanAnnotation
    @RequestMapping("/list")
    @ResponseBody
    public String list(ActionBean bean){
        return bankAction.list(bean);
    }


    @GetActionBeanAnnotation
    @RequestMapping("/applyAuth")
    @ResponseBody
    public String applyAuth(ActionBean bean){
        return bankAction.applyAuth(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/confirmAuth")
    @ResponseBody
    public String confirmAuth(ActionBean bean){
        return bankAction.confirmAuth(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/getUsedPayInfo")
    @ResponseBody
    public String getUsedPayInfo(ActionBean bean){
        return bankAction.getUsedPayInfo(bean);
    }





}
