package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarrantyChangeRecord;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 15:50
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyChangeRecordMapper {

    int addInsurancePreservation(CustWarrantyChangeRecord custWarrantyChangeRecord);

    List<CustWarrantyChangeRecord> findInsurancePreservationListPrivateCode(CustWarrantyChangeRecord custWarrantyChangeRecord);

}
