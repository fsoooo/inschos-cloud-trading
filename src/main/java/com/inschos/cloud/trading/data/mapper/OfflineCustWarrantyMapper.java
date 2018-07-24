package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.OfflineCustWarranty;

import java.util.List;

/**
 * 创建日期：2018/5/29 on 18:33
 * 描述：
 * 作者：zhangyunhe
 */
public interface OfflineCustWarrantyMapper {

    long addOfflineInsurancePolicy(OfflineCustWarranty offlineCustWarranty);

    OfflineCustWarranty findOfflineInsurancePolicyByWarrantyCode(String warrantyCode);

    OfflineCustWarranty findOfflineInsurancePolicyByWarrantyUuid(String warrantyUuid);

    List<OfflineCustWarranty> findOfflineInsurancePolicyListForManagerSystem(OfflineCustWarranty offlineCustWarranty);

    long findOfflineInsurancePolicyCountForManagerSystem(OfflineCustWarranty offlineCustWarranty);

    int updateSettlementAndBillUuidByWarrantyUuid(OfflineCustWarranty offlineCustWarranty);

    int updateBillUuidByWarrantyUuid(OfflineCustWarranty offlineCustWarranty);

    OfflineCustWarranty findBrokerageByWarrantyUuid(OfflineCustWarranty offlineCustWarranty);

    List<OfflineCustWarranty> findCompletePayListByManagerUuid (OfflineCustWarranty offlineCustWarranty);

    long findCompletePayCountByManagerUuid (OfflineCustWarranty offlineCustWarranty);

    long updatePayStatusByWarrantyUuid (OfflineCustWarranty offlineCustWarranty);

    long updateStateByWarrantyUuid (OfflineCustWarranty offlineCustWarranty);

}
