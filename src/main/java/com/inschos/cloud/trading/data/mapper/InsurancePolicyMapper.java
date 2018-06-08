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

    InsurancePolicyModel findInsurancePolicyDetailByWarrantyUuid(String warrantyUuid);

    long findEffectiveInsurancePolicyCountByChannelIdAndTime(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyListCount(InsurancePolicyModel insurancePolicyModel);

    List<PolicyListCountModel> findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(InsurancePolicyModel insurancePolicyModel);

    int findEffectiveInsurancePolicyCountByAgentAndTime(InsurancePolicyModel search);

    List<InsurancePolicyModel> findInsurancePolicyListForOnlineStore(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountForOnlineStore(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListForManagerSystem(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountForManagerSystem(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListByActualPayTime(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountByActualPayTime(InsurancePolicyModel insurancePolicyModel);
}
