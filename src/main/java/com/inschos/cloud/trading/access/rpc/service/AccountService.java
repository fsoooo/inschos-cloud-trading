package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.AccountBean;

/**
 * Created by IceAnt on 2018/4/16.
 */
public interface AccountService {

    public AccountBean getAccount(String token);
}
