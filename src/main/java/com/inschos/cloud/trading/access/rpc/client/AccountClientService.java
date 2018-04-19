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
public class AccountClientService extends BaseServiceClient<AccountService> implements AccountService {

    @Value("${rpc.remote.account.host}")
    private String host;
    
    private final String uri = "/rpc/account";


    public AccountBean getAccount(String token){
        try {
            AccountService service = getService(host+uri);
            return service!=null?service.getAccount(token):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }

    public AccountBean findByUuid(String uuid){
        try {
            AccountService service = getService(host+uri);
            return service!=null?service.findByUuid(uuid):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }


}
