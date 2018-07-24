package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.OfflineCustWarrantyMapper;
import com.inschos.cloud.trading.model.OfflineCustWarranty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/5/29 on 18:34
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class OfflineCustWarrantyDao {

    @Autowired
    private OfflineCustWarrantyMapper offlineCustWarrantyMapper;

    public long addOfflineInsurancePolicy(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.addOfflineInsurancePolicy(offlineCustWarranty);
    }

    public int updateSettlementAndBillUuidByWarrantyUuid(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.updateSettlementAndBillUuidByWarrantyUuid(offlineCustWarranty);
    }

    public int updateBillUuidByWarrantyUuid(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.updateBillUuidByWarrantyUuid(offlineCustWarranty);
    }

    public OfflineCustWarranty findOfflineInsurancePolicyByWarrantyCode(String warrantyCode) {
        return offlineCustWarrantyMapper.findOfflineInsurancePolicyByWarrantyCode(warrantyCode);
    }

    public OfflineCustWarranty findOfflineInsurancePolicyByWarrantyUuid(String warrantyUuid) {
        return offlineCustWarrantyMapper.findOfflineInsurancePolicyByWarrantyUuid(warrantyUuid);
    }

    public List<OfflineCustWarranty> findOfflineInsurancePolicyListForManagerSystem(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.findOfflineInsurancePolicyListForManagerSystem(offlineCustWarranty);
    }

    public long findOfflineInsurancePolicyCountForManagerSystem(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.findOfflineInsurancePolicyCountForManagerSystem(offlineCustWarranty);
    }

    public OfflineCustWarranty findBrokerageByWarrantyUuid(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.findBrokerageByWarrantyUuid(offlineCustWarranty);
    }

    public List<OfflineCustWarranty> findCompletePayListByManagerUuid(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.findCompletePayListByManagerUuid(offlineCustWarranty);
    }

    public long findCompletePayCountByManagerUuid(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.findCompletePayCountByManagerUuid(offlineCustWarranty);
    }

    public long updatePayStatusByWarrantyUuid(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.updatePayStatusByWarrantyUuid(offlineCustWarranty);
    }

    public long updateStateByWarrantyUuid(OfflineCustWarranty offlineCustWarranty) {
        return offlineCustWarrantyMapper.updateStateByWarrantyUuid(offlineCustWarranty);
    }
}
