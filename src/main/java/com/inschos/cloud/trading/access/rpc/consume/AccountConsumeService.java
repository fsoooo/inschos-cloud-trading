package com.inschos.cloud.trading.access.rpc.consume;

import com.inschos.cloud.trading.access.rpc.bean.AccountBean;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/20.
 */
@Component
public class AccountConsumeService extends BaseConsumeService {

    private final String beanName = "rpcAccountService";

    public AccountBean getAccount(String token){

        // TODO: 2018/4/12  rpc  获取 account

        if(true){
            AccountBean accountBean = new AccountBean();
            accountBean.accountUuid = "2";
            accountBean.loginUuid = "1";
            accountBean.userId = 1;
            accountBean.userType = 1;
            return accountBean;

        }
        return null;
    }


    @Override
    protected String getBeanName() {
        return beanName;
    }

}
