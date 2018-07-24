package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CustWarrantyChangeRecordMapper;
import com.inschos.cloud.trading.model.CustWarrantyChangeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 15:50
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyChangeRecordDao {

    @Autowired
    public CustWarrantyChangeRecordMapper custWarrantyChangeRecordMapper;

    public int addInsurancePreservation(CustWarrantyChangeRecord custWarrantyChangeRecord) {
        return custWarrantyChangeRecordMapper.addInsurancePreservation(custWarrantyChangeRecord);
    }

    // 理赔记录
    public List<CustWarrantyChangeRecord> findInsurancePreservationListPrivateCode (CustWarrantyChangeRecord custWarrantyChangeRecord) {
        return custWarrantyChangeRecordMapper.findInsurancePreservationListPrivateCode(custWarrantyChangeRecord);
    }

}
