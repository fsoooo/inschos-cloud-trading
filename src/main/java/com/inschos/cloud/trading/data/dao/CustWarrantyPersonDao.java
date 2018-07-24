package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CustWarrantyPersonMapper;
import com.inschos.cloud.trading.model.CustWarrantyPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 14:13
 * 描述：保单参与人员信息（投保人，被保险人，受益人）
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyPersonDao extends BaseDao {

    @Autowired
    private CustWarrantyPersonMapper custWarrantyPersonMapper;

    public int addInsuranceParticipant(CustWarrantyPerson custWarrantyPerson) {
        return custWarrantyPersonMapper.addInsuranceParticipant(custWarrantyPerson);
    }

    public List<CustWarrantyPerson> findInsuranceParticipantByWarrantyUuid(String warrantyUuid) {
        CustWarrantyPerson custWarrantyPerson = new CustWarrantyPerson();
        custWarrantyPerson.warranty_uuid = warrantyUuid;
        return custWarrantyPersonMapper.findInsuranceParticipantByWarrantyUuid(custWarrantyPerson);
    }

    public List<CustWarrantyPerson> findInsuranceParticipantInsuredNameByWarrantyUuid(String warrantyUuid) {
        CustWarrantyPerson custWarrantyPerson = new CustWarrantyPerson();
        custWarrantyPerson.warranty_uuid = warrantyUuid;
        return custWarrantyPersonMapper.findInsuranceParticipantInsuredNameByWarrantyUuid(custWarrantyPerson);
    }

    public CustWarrantyPerson findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(String warrantyUuid) {
        CustWarrantyPerson custWarrantyPerson = new CustWarrantyPerson();
        custWarrantyPerson.warranty_uuid = warrantyUuid;
        return custWarrantyPersonMapper.findInsuranceParticipantPolicyHolderNameAndMobileByWarrantyUuid(custWarrantyPerson);
    }

    public List<CustWarrantyPerson> findInsuranceParticipantInsuredByWarrantyUuid(String warrantyUuid) {
        CustWarrantyPerson custWarrantyPerson = new CustWarrantyPerson();
        custWarrantyPerson.warranty_uuid = warrantyUuid;
        return custWarrantyPersonMapper.findInsuranceParticipantInsuredByWarrantyUuid(custWarrantyPerson);
    }

    public List<CustWarrantyPerson> findInsuranceParticipantInsuredNameByWarrantyUuids(CustWarrantyPerson custWarrantyPerson) {
        return custWarrantyPersonMapper.findInsuranceParticipantInsuredNameByWarrantyUuids(custWarrantyPerson);
    }

}
