package com.inschos.cloud.trading.data.dao;


import com.inschos.cloud.trading.model.BankAuthorize;

/**
 * author   meiming_mm@163.com
 * date     2018/7/11
 * version  v1.0.0
 */
public interface BankAuthorizeDao {

    int add(BankAuthorize record);

    int update(BankAuthorize update);

    BankAuthorize findOne(long id);

    BankAuthorize findByAUuid(String accountUuid);
}
