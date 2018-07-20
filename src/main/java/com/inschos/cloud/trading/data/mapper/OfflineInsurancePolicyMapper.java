package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.OfflineInsurancePolicyModel;

import java.util.List;

/**
 * 创建日期：2018/5/29 on 18:33
 * 描述：
 * 作者：zhangyunhe
 */
public interface OfflineInsurancePolicyMapper {

    long addOfflineInsurancePolicy(OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    OfflineInsurancePolicyModel findOfflineInsurancePolicyByWarrantyCode(String warrantyCode);

    OfflineInsurancePolicyModel findOfflineInsurancePolicyByWarrantyUuid(String warrantyUuid);

    List<OfflineInsurancePolicyModel> findOfflineInsurancePolicyListForManagerSystem(OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    long findOfflineInsurancePolicyCountForManagerSystem(OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    int updateSettlementAndBillUuidByWarrantyUuid(OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    int updateBillUuidByWarrantyUuid(OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    OfflineInsurancePolicyModel findBrokerageByWarrantyUuid(OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    List<OfflineInsurancePolicyModel> findCompletePayListByManagerUuid (OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    long findCompletePayCountByManagerUuid (OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    long updatePayStatusByWarrantyUuid (OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    long updateStateByWarrantyUuid (OfflineInsurancePolicyModel offlineInsurancePolicyModel);

}
