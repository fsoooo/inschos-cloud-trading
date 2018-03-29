package com.inschos.cloud.trading.access.rpc.service.consume;

import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/20.
 */
@Component
public class AccountConsumeService extends BaseConsumeService {

    private final String beanName = "rpcAccountService";

    public boolean isLogin(String token){

        Boolean isLogin = invoke("isLogin", boolean.class, token);

        return isLogin!=null && isLogin;
    }

    public String findOne(String token){

        String xx = invoke("findOne", String.class, token);

        return xx;
    }



    @Override
    protected String getBeanName() {
        return beanName;
    }

}
