package com.inschos.cloud.trading.data.mapper;


import com.inschos.cloud.trading.model.BankAuthorize;

/**
 * author   meiming_mm@163.com
 * date     2018/7/11
 * version  v1.0.0
 */
public interface BankAuthorizeMapper {

    int insert(BankAuthorize record);

    int update(BankAuthorize update);

    BankAuthorize selectOne(long id);

    BankAuthorize selectByAUuid(String accountUuid);
}
