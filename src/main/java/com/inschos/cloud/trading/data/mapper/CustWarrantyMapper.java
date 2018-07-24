package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarranty;
import com.inschos.cloud.trading.model.PolicyListCount;

import java.util.List;

/**
 * 创建日期：2018/3/22 on 16:43
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyMapper {

    int addInsurancePolicy(CustWarranty custWarranty);

    int updateInsurancePolicyProPolicyNoByWarrantyUuid(CustWarranty custWarranty);

    int updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(CustWarranty custWarranty);

    int updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(CustWarranty custWarranty);

    int updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(CustWarranty custWarranty);

    CustWarranty findInsurancePolicyDetailByWarrantyUuid(String warrantyUuid);

    long findEffectiveInsurancePolicyCountByChannelIdAndTime(CustWarranty custWarranty);

    long findInsurancePolicyListCount(CustWarranty custWarranty);

    List<PolicyListCount> findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(CustWarranty custWarranty);

    int findEffectiveInsurancePolicyCountByAgentAndTime(CustWarranty search);

    List<CustWarranty> findInsurancePolicyListForOnlineStore(CustWarranty custWarranty);

    long findInsurancePolicyCountForOnlineStore(CustWarranty custWarranty);

    List<CustWarranty> findInsurancePolicyListForManagerSystem(CustWarranty custWarranty);

    long findInsurancePolicyCountForManagerSystem(CustWarranty custWarranty);

    List<CustWarranty> findInsurancePolicyListByActualPayTime(CustWarranty custWarranty);

    long findInsurancePolicyCountByActualPayTime(CustWarranty custWarranty);

    List<CustWarranty> setTest(CustWarranty custWarranty);

    CustWarranty findExistsValid(CustWarranty search);

    int findCountByAgentWarrantyStatus(CustWarranty search);

    int findCountByAgentCostStatus(CustWarranty search);

    List<CustWarranty> findInsuranceRecordListByManagerUuid(CustWarranty search);

    long findInsuranceRecordCountByManagerUuid(CustWarranty search);

    CustWarranty findPremiumCountByCustomerManager(CustWarranty search);

    CustWarranty findByProposalNo(CustWarranty search);
}
