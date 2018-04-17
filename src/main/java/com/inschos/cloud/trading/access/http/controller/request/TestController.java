package com.inschos.cloud.trading.access.http.controller.request;

import com.inschos.cloud.trading.access.rpc.client.AccountClientService;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.L;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IceAnt on 2018/4/16.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AccountClientService accountClientService;

    @RequestMapping("/token")
    @ResponseBody
    public String token(HttpServletRequest request){
        String token = request.getParameter("token");
        L.log.debug("token is {}",token);
        return JsonKit.bean2Json(accountClientService.getAccount(token));
    }
}
