package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.AccountBean;
import com.inschos.cloud.trading.access.rpc.service.AccountService;
import hprose.client.HproseHttpClient;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/20.
 */
@Component
public class AccountClient {

    private final String remoteUrl = "http://localhost:9600/test.php";

    private HproseHttpClient client = new HproseHttpClient(remoteUrl);

    public AccountBean getAccount(String token){

        // TODO: 2018/4/12  rpc  获取 account

        if(true){
            AccountBean accountBean = new AccountBean();
            accountBean.managerUuid = "2";
            accountBean.accountUuid = "1";
            accountBean.userId = 1;
            accountBean.userType = 1;
            return accountBean;

        }
        AccountService accountService = client.useService(AccountService.class);
        return accountService.getAccount(token);
    }


}
