package com.inschos.cloud.trading.data.mapper;


import com.inschos.cloud.trading.model.Bank;

import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/11
 * version  v1.0.0
 */
public interface BankMapper {

    int insert(Bank record);

    int update(Bank update);

    int updateState(Bank update);

    Bank selectOne(long id);

    Bank selectExistsOne(Bank search);

    List<Bank> selectListByAuuid(Bank search);
}
