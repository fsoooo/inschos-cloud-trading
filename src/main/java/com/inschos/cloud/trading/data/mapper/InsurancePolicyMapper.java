package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.InsurancePolicyModel;

import java.util.List;

/**
 * 创建日期：2018/3/22 on 16:43
 * 描述：
 * 作者：zhangyunhe
 */
public interface InsurancePolicyMapper {

    int addInsurancePolicy(InsurancePolicyModel insurancePolicyModel);

    int updateInsurancePolicyProPolicyNoByWarrantyUuid(InsurancePolicyModel insurancePolicyModel);

    int updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(InsurancePolicyModel insurancePolicyModel);

    int updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(InsurancePolicyModel insurancePolicyModel);

    int updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListByWarrantyStatusOrSearch(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountByWarrantyStatus(InsurancePolicyModel insurancePolicyModel);

    InsurancePolicyModel findInsurancePolicyDetailByWarrantyUuid(String warrantyUuid);

    List<InsurancePolicyModel> findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelId(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelIdCount(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchOrTimeOrChannelId(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyListBySearchOrTimeOrChannelIdCount(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findEffectiveInsurancePolicyListByChannelId(String channelId);

    List<InsurancePolicyModel> findEffectiveInsurancePolicyByChannelIdAndTime(InsurancePolicyModel insurancePolicyModel);

    long findEffectiveInsurancePolicyCountByChannelIdAndTime(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyListCountTimeOrAccountId(InsurancePolicyModel insurancePolicyModel);
}
