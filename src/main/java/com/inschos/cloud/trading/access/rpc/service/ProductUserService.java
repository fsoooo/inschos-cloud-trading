package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.ProductUserBean;

/**
 * author   meiming_mm@163.com
 * date     2018/7/25
 * version  v1.0.0
 */
public interface ProductUserService {

    ProductUserBean getUsersByAccountId(String accountUuid);

    int addUsers(ProductUserBean bean);

}
