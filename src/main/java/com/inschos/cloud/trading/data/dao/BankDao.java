package com.inschos.cloud.trading.data.dao;


import com.inschos.cloud.trading.model.Bank;

import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/12
 * version  v1.0.0
 */
public interface BankDao {

    /**
     * 新增
     */
    int add(Bank record);

    /**
     * 修改
     */
    int update(Bank update);

    /**
     * 修改 删除
     */
    int updateState(Bank update);

    /**
     * id查找
     */
    Bank findOne(long id);

    /**
     * account_code bank_code 已存在的记录
     *
     */
    Bank findExistsOne(Bank search);

    /**
     * account_uuid 下的所以银行卡
     */
    List<Bank> findListByAuuid(Bank search);

}
