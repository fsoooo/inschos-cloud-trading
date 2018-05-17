package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.action.StatisticAction;
import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by IceAnt on 2018/5/16.
 */
@Controller
@RequestMapping("/stat")
public class StatisticController {

    @Autowired
    private StatisticAction statisticAction;

    @GetActionBeanAnnotation
    @RequestMapping("/insureListForAgentSelf")
    @ResponseBody
    public String insureListForAgentSelf(ActionBean bean){
        return statisticAction.insureListForAgentSelf(bean);
    }

    @GetActionBeanAnnotation
    @RequestMapping("/insureTotalForAgentSelf")
    @ResponseBody
    public String insureTotalForAgentSelf(ActionBean bean){
        return statisticAction.insureTotalForAgentSelf(bean);
    }

}
