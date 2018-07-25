package com.inschos.cloud.trading.data.dao.impl;

import com.inschos.cloud.trading.data.dao.BankAuthorizeDao;
import com.inschos.cloud.trading.data.mapper.BankAuthorizeMapper;
import com.inschos.cloud.trading.model.BankAuthorize;
import com.inschos.common.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * author   meiming_mm@163.com
 * date     2018/7/25
 * version  v1.0.0
 */
@Repository
public class BankAuthorizeDaoImpl implements BankAuthorizeDao {

    @Autowired
    private BankAuthorizeMapper bankAuthorizeMapper;

    @Override
    public int add(BankAuthorize record) {
        return record != null ? bankAuthorizeMapper.insert(record) : 0;
    }

    @Override
    public int update(BankAuthorize update) {
        return update != null ? bankAuthorizeMapper.update(update) : 0;
    }

    @Override
    public BankAuthorize findOne(long id) {
        return id>0?bankAuthorizeMapper.selectOne(id):null;
    }

    @Override
    public BankAuthorize findByAUuid(String accountUuid) {
        return StringKit.isEmpty(accountUuid)?null:bankAuthorizeMapper.selectByAUuid(accountUuid);
    }
}
