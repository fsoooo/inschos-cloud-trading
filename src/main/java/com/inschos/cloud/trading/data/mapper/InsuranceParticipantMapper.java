package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.InsuranceParticipantModel;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 11:49
 * 描述：
 * 作者：zhangyunhe
 */
public interface InsuranceParticipantMapper {

    int addInsuranceParticipant (InsuranceParticipantModel insuranceParticipantModel);

    List<InsuranceParticipantModel> findInsuranceParticipantByWarrantyUuid(String warrantyUuid);

    List<InsuranceParticipantModel> findInsuranceParticipantInsuredNameByWarrantyUuid(String warrantyUuid);

    List<InsuranceParticipantModel> findInsuranceParticipantInsuredByWarrantyUuid(String warrantyUuid);

    InsuranceParticipantModel findInsuranceParticipantPolicyHolderNameByWarrantyUuid(String warrantyUuid);

}
