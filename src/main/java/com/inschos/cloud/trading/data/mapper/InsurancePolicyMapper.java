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

    int updateInsurancePolicyProPolicyNoByWarrantyId (InsurancePolicyModel insurancePolicyModel);

    InsurancePolicyModel findInsurancePolicyDetailByPrivateCode(String privateCode);

    List<InsurancePolicyModel> findInsurancePolicyListByUserIdAndStatus(InsurancePolicyModel insurancePolicyModel);

    List<InsurancePolicyModel> findInsurancePolicyListByOtherInfo(InsurancePolicyModel insurancePolicyModel);

    int updateInsurancePolicyUnionOrderCode(InsurancePolicyModel insurancePolicyModel);

    int updateInsurancePolicyWarrantyCode(InsurancePolicyModel insurancePolicyModel);

    String findInsurancePolicyPrivateCodeByUnionOrderCode(String unionOrderCode);

}
