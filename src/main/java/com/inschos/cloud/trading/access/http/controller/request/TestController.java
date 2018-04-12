package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.rpc.consume.AccountConsumeService;
import com.inschos.cloud.trading.annotation.GetActionBeanAnnotation;
import com.inschos.cloud.trading.data.dao.DemoDao;
import com.inschos.cloud.trading.model.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by IceAnt on 2018/3/14.
 */
@Controller
@RequestMapping("/test/")
public class TestController {

    @Autowired
    private DemoDao demoDao;

    @Autowired
    private AccountConsumeService accountConsumeService;

    @GetActionBeanAnnotation(isCheckAccess=false)
    @RequestMapping("/do")
    @ResponseBody
    public String doOne(ActionBean bean){
        Area area = new Area();
        area.id=10;
        area.name = "特效";
        return "test data :" ;
    }


}
