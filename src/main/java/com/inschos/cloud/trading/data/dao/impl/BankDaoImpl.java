package com.inschos.cloud.trading.data.dao.impl;


import com.inschos.cloud.trading.data.dao.BankDao;
import com.inschos.cloud.trading.data.mapper.BankMapper;
import com.inschos.cloud.trading.model.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/12
 * version  v1.0.0
 */
@Repository
public class BankDaoImpl implements BankDao {

    @Autowired
    private BankMapper bankMapper;


    @Override
    public int add(Bank record) {
        return record != null ? bankMapper.insert(record) : 0;
    }

    @Override
    public int update(Bank update) {
        return update != null ? bankMapper.update(update) : 0;
    }

    /**
     * 修改 删除
     *
     * @param update
     */
    @Override
    public int updateState(Bank update) {
        return bankMapper.updateState(update);
    }

    @Override
    public Bank findOne(long id) {
        return bankMapper.selectOne(id);
    }

    /**
     * account_code bank_code 已存在的记录
     *
     * @param search
     */
    @Override
    public Bank findExistsOne(Bank search) {
        return null;
    }

    @Override
    public List<Bank> findListByAuuid(Bank search) {
        return search != null ? bankMapper.selectListByAuuid(search) : null;
    }
}
