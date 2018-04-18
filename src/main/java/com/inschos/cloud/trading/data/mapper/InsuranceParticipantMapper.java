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

    List<InsuranceParticipantModel> findInsuranceParticipantByWarrantyUuid(InsuranceParticipantModel insuranceParticipantModel);

    List<InsuranceParticipantModel> findInsuranceParticipantInsuredNameByWarrantyUuid(InsuranceParticipantModel insuranceParticipantModel);

    List<InsuranceParticipantModel> findInsuranceParticipantInsuredByWarrantyUuid(InsuranceParticipantModel insuranceParticipantModel);

    InsuranceParticipantModel findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(InsuranceParticipantModel insuranceParticipantModel);

}
