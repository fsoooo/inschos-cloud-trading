package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.TradeAction;
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
@RequestMapping("/web")
public class TradeController {

    @Autowired
    private TradeAction tradeAction;

    // 投保
    @GetActionBeanAnnotation
    @RequestMapping("/insure")
    @ResponseBody
    public String insure(ActionBean bean) {
        return tradeAction.insure(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/pre_insure")
    @ResponseBody
    public String preInsure(ActionBean bean) {
        return tradeAction.preInsure(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/pay")
    @ResponseBody
    public String pay(ActionBean bean) {
        return tradeAction.pay(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/quote")
    @ResponseBody
    public String quote(ActionBean bean) {
        return tradeAction.quote(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/query")
    @ResponseBody
    public String query(ActionBean bean) {
        return tradeAction.query(bean);
    }




}
