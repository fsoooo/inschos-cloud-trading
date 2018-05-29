package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.OfflineInsurancePolicyModel;

/**
 * 创建日期：2018/5/29 on 18:33
 * 描述：
 * 作者：zhangyunhe
 */
public interface OfflineInsurancePolicyMapper {

    long addOfflineInsurancePolicy(OfflineInsurancePolicyModel offlineInsurancePolicyModel);

    OfflineInsurancePolicyModel findOfflineInsurancePolicyByWarrantyCode(String warrantyCode);

    OfflineInsurancePolicyModel findOfflineInsurancePolicyByWarrantyUuid(String warrantyUuid);
}
