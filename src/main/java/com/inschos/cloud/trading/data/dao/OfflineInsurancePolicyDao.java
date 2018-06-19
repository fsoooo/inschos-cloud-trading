package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.OfflineInsurancePolicyMapper;
import com.inschos.cloud.trading.model.OfflineInsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/5/29 on 18:34
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class OfflineInsurancePolicyDao {

    @Autowired
    private OfflineInsurancePolicyMapper offlineInsurancePolicyMapper;

    public long addOfflineInsurancePolicy(OfflineInsurancePolicyModel offlineInsurancePolicyModel) {
        return offlineInsurancePolicyMapper.addOfflineInsurancePolicy(offlineInsurancePolicyModel);
    }

    public OfflineInsurancePolicyModel findOfflineInsurancePolicyByWarrantyCode(String warrantyCode) {
        return offlineInsurancePolicyMapper.findOfflineInsurancePolicyByWarrantyCode(warrantyCode);
    }

    public OfflineInsurancePolicyModel findOfflineInsurancePolicyByWarrantyUuid(String warrantyUuid) {
        return offlineInsurancePolicyMapper.findOfflineInsurancePolicyByWarrantyUuid(warrantyUuid);
    }

    public List<OfflineInsurancePolicyModel> findOfflineInsurancePolicyListForManagerSystem(OfflineInsurancePolicyModel offlineInsurancePolicyModel){
        return offlineInsurancePolicyMapper.findOfflineInsurancePolicyListForManagerSystem(offlineInsurancePolicyModel);
    }

    public long findOfflineInsurancePolicyCountForManagerSystem(OfflineInsurancePolicyModel offlineInsurancePolicyModel){
        return offlineInsurancePolicyMapper.findOfflineInsurancePolicyCountForManagerSystem(offlineInsurancePolicyModel);
    }
}
