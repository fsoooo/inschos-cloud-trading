package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.PolicyListCountModel;

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

    List<InsurancePolicyModel> findInsurancePolicyListByWarrantyStatusStringOrSearch(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountByWarrantyStatus(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountByWarrantyStatusString(InsurancePolicyModel insurancePolicyModel);

    InsurancePolicyModel findInsurancePolicyDetailByWarrantyUuid(String warrantyUuid);

    List<InsurancePolicyModel> findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelId(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType1(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType1(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType2(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType2(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType3(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType3(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType4(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType4(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType1Status1(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType1Status1(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType2Status1(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType2Status1(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType3Status1(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType3Status1(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchType4Status1(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountBySearchType4Status1(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelIdCount(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListBySearchOrTimeOrChannelId(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyListBySearchOrTimeOrChannelIdCount(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findEffectiveInsurancePolicyListByChannelId(String channelId);

    List<InsurancePolicyModel> findEffectiveInsurancePolicyByChannelIdAndTime(InsurancePolicyModel insurancePolicyModel);

    long findEffectiveInsurancePolicyCountByChannelIdAndTime(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyListCount(InsurancePolicyModel insurancePolicyModel);

    List<PolicyListCountModel> findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(InsurancePolicyModel insurancePolicyModel);

    Double getTotalPremiumByAccountUuidAndTime(InsurancePolicyModel insurancePolicyModel);
}
