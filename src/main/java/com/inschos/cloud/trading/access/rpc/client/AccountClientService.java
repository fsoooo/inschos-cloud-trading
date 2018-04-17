package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.AccountBean;
import com.inschos.cloud.trading.access.rpc.service.AccountService;
import com.inschos.cloud.trading.assist.kit.L;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/20.
 */
@Component
public class AccountClientService extends BaseClientService implements AccountService {

    @Value("${rpc.remote.account.host}")
    private String host;
    
    private final String uri = "/rpc/account";

    private AccountService accountRemoteService;

    private AccountService getAccountRemoteService() {
        if(accountRemoteService ==null){
            accountRemoteService = getService(host + uri,AccountService.class);
        }
        return accountRemoteService;
    }

    public AccountBean getAccount(String token){
        try {
            AccountService service = getAccountRemoteService();
            return service!=null?service.getAccount(token):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }


}
