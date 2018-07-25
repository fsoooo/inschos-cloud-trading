package com.inschos.cloud.trading.data.dao.impl;


import com.inschos.cloud.trading.data.dao.BankDao;
import com.inschos.cloud.trading.data.dao.BaseDao;
import com.inschos.cloud.trading.data.mapper.BankAuthorizeMapper;
import com.inschos.cloud.trading.data.mapper.BankMapper;
import com.inschos.cloud.trading.model.Bank;
import com.inschos.cloud.trading.model.BankAuthorize;
import com.inschos.common.assist.kit.TimeKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/12
 * version  v1.0.0
 */
@Repository
public class BankDaoImpl extends BaseDao implements BankDao {

    @Autowired
    private BankMapper bankMapper;
    @Autowired
    private BankAuthorizeMapper bankAuthorizeMapper;


    @Override
    public int add(Bank record) {
        return record != null ? bankMapper.insert(record) : 0;
    }

    /**
     * 授权成功
     *
     * @param bank
     * @return
     */
    @Override
    public int applyAuth(Bank bank) {
        int exResult = 0;

        Bank search = new Bank();
        search.account_uuid = bank.account_uuid;
        search.bank_code = bank.bank_code;
        Bank existsOne = bankMapper.selectExistsOne(search);

        if (existsOne != null) {
            existsOne.status = Bank.BANK_AUTH_STATUS_OK;
            existsOne.updated_at = TimeKit.currentTimeMillis();
            exResult = bankMapper.update(existsOne);
        } else {

            bank.bank_type = "1";
            bank.created_at = bank.updated_at = TimeKit.currentTimeMillis();
            bank.state = 1;
            exResult =  bankMapper.insert(bank);
        }

        if (exResult > 0) {

            BankAuthorize bankAuthorize = bankAuthorizeMapper.selectByAUuid(bank.account_uuid);
            if (bankAuthorize != null) {
                bankAuthorize.id = bank.id;
                bankAuthorize.updated_at = TimeKit.currentTimeMillis();
                exResult = bankAuthorizeMapper.update(bankAuthorize);
            } else {
                bankAuthorize = new BankAuthorize();
                bankAuthorize.account_uuid = bank.account_uuid;
                bankAuthorize.bank_id = bank.id;
                bankAuthorize.state = 1;
                bankAuthorize.created_at = bank.updated_at = TimeKit.currentTimeMillis();
                exResult = bankAuthorizeMapper.insert(bankAuthorize);
            }
            if(exResult==0){
                rollBack();
            }
        }
        return 0;
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
