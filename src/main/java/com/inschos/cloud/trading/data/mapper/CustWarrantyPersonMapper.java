package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarrantyPerson;
import com.inschos.cloud.trading.model.CustWarranty;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 11:49
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyPersonMapper {

    int addInsuranceParticipant (CustWarrantyPerson custWarrantyPerson);

    List<CustWarrantyPerson> findInsuranceParticipantByWarrantyUuid(CustWarrantyPerson custWarrantyPerson);

    List<CustWarrantyPerson> findInsuranceParticipantInsuredNameByWarrantyUuid(CustWarrantyPerson custWarrantyPerson);

    List<CustWarrantyPerson> findInsuranceParticipantInsuredByWarrantyUuid(CustWarrantyPerson custWarrantyPerson);

    CustWarrantyPerson findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(CustWarrantyPerson custWarrantyPerson);

    List<CustWarranty> findInsurancePolicyListBySearchType3(CustWarranty custWarranty);

    long findInsurancePolicyCountBySearchType3(CustWarranty custWarranty);

    List<CustWarrantyPerson> findInsuranceParticipantInsuredNameByWarrantyUuids(CustWarrantyPerson custWarrantyPerson);
}
