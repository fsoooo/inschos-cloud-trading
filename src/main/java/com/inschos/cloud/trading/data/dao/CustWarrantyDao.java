package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.*;
import com.inschos.cloud.trading.model.*;
import com.inschos.cloud.trading.model.fordao.*;
import com.inschos.common.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018/3/22 on 16:52
 * 描述：保单信息
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyDao extends BaseDao {

    @Autowired
    private CustWarrantyMapper custWarrantyMapper;

    @Autowired
    private CustWarrantyPersonMapper custWarrantyPersonMapper;

    @Autowired
    private CarInfoMapper carInfoMapper;

    @Autowired
    private CustWarrantyCostMapper custWarrantyCostMapper;

    @Autowired
    private CustWarrantyBrokerageMapper custWarrantyBrokerageMapper;

    public int addInsurancePolicyAndParticipantForCarInsurance(InsurancePolicyAndParticipantForCarInsurance insurancePolicyAndParticipantForCarInsurance) {
        if (insurancePolicyAndParticipantForCarInsurance != null) {
            int add = 1;
            if (insurancePolicyAndParticipantForCarInsurance.ciProposal != null) {
                add = addInsurancePolicy(insurancePolicyAndParticipantForCarInsurance.ciProposal);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biProposal != null) {
                add = addInsurancePolicy(insurancePolicyAndParticipantForCarInsurance.biProposal);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciPolicyholder != null) {
                add = custWarrantyPersonMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.ciPolicyholder);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biPolicyholder != null) {
                add = custWarrantyPersonMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.biPolicyholder);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciInsured != null) {
                add = custWarrantyPersonMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.ciInsured);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biInsured != null) {
                add = custWarrantyPersonMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.biInsured);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCar != null) {
                add = carInfoMapper.addCarInfo(insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCar);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCar != null) {
                add = carInfoMapper.addCarInfo(insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCar);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCost != null) {
                add = custWarrantyCostMapper.addCustWarrantyCost(insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCost);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCost != null) {
                add = custWarrantyCostMapper.addCustWarrantyCost(insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCost);
            }

            if (add <= 0) {
                rollBack();
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerage != null && insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCost != null) {
                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerage.cost_id = insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCost.id;
                add = custWarrantyBrokerageMapper.addCustWarrantyBrokerage(insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerage);
            }

            if (add <= 0) {
                rollBack();
            }

            if (insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerage != null && insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCost != null) {
                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerage.cost_id = insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCost.id;
                add = custWarrantyBrokerageMapper.addCustWarrantyBrokerage(insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerage);
            }

            if (add <= 0) {
                rollBack();
            }

            return add;
        } else {
            return 1;
        }
    }

    /**
     * 投保 保单
     *
     * @param policyModel 保单
     * @param costModels  缴费单
     * @return
     */
    public int insure(CustWarranty policyModel, List<CustWarrantyCost> costModels) {
        int flag = 0;

        flag = custWarrantyMapper.addInsurancePolicy(policyModel);
        if (flag > 0) {

            for (CustWarrantyPerson participantModel : policyModel.insured_list) {
                flag = custWarrantyPersonMapper.addInsuranceParticipant(participantModel);
                if (flag == 0) {
                    rollBack();
                    break;
                }
            }

            if (flag > 0) {
                for (CustWarrantyCost costModel : costModels) {

                    flag = custWarrantyCostMapper.addCustWarrantyCost(costModel);

                    if (flag == 0) {
                        rollBack();
                        break;
                    }

                }
            }

        }

        return flag;
    }


    public int updatePayResult(CustWarranty custWarranty,CustWarrantyCost cost,CustWarrantyBrokerage brokerage){
        int flag = 0;
        if(custWarranty!=null){
            flag = custWarrantyMapper.updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(custWarranty);
            if(cost!=null && flag>0){
                flag = custWarrantyCostMapper.updatePayResult(cost);
                if(flag>0 && brokerage!=null){
                    flag = custWarrantyBrokerageMapper.addCustWarrantyBrokerage(brokerage);
                }
                if (flag==0){
                    rollBack();
                }
            }
        }
        return flag;
    }


    public CustWarranty findExistsValid(CustWarranty search) {
        return search != null ? custWarrantyMapper.findExistsValid(search) : null;
    }

    public int updateInsurancePolicyProPolicyNoForCarInsurance(UpdateInsurancePolicyProPolicyNoForCarInsurance updateInsurancePolicyProPolicyNoForCarInsurance) {
        int update = 1;
        List<CustWarrantyCar> byBizId = carInfoMapper.findByBizId(updateInsurancePolicyProPolicyNoForCarInsurance.bizId);
        if (byBizId != null && !byBizId.isEmpty()) {
            String time = String.valueOf(System.currentTimeMillis());
            for (CustWarrantyCar custWarrantyCar : byBizId) {
                CustWarranty custWarranty = new CustWarranty();
                custWarranty.warranty_uuid = custWarrantyCar.warranty_uuid;
                if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_STRONG)) {
                    custWarranty.pre_policy_no = updateInsurancePolicyProPolicyNoForCarInsurance.ciProposalNo;
                } else if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_COMMERCIAL)) {
                    custWarranty.pre_policy_no = updateInsurancePolicyProPolicyNoForCarInsurance.biProposalNo;
                }
                custWarranty.warranty_status = updateInsurancePolicyProPolicyNoForCarInsurance.warrantyStatus;
                custWarranty.updated_at = time;
                update = custWarrantyMapper.updateInsurancePolicyProPolicyNoByWarrantyUuid(custWarranty);

                if (update <= 0) {
                    rollBack();
                    break;
                }

                custWarrantyCar.bj_code_flag = updateInsurancePolicyProPolicyNoForCarInsurance.bjCodeFlag;
                custWarrantyCar.updated_at = String.valueOf(System.currentTimeMillis());

                update = carInfoMapper.updateBjCodeFlagByWarrantyUuid(custWarrantyCar);

                if (update <= 0) {
                    rollBack();
                    break;
                }
            }
        }
        return update;
    }

    public int addInsurancePolicy(CustWarranty custWarranty) {
        return custWarrantyMapper.addInsurancePolicy(custWarranty);
    }

    public List<CustWarrantyCar> getCarInfoList(String bizId, String thpBizID) {
        if (!StringKit.isEmpty(bizId)) {
            return carInfoMapper.findWarrantyUuidByBizId(bizId);
        } else if (!StringKit.isEmpty(thpBizID)) {
            return carInfoMapper.findWarrantyUuidByThpBizID(thpBizID);
        }
        return null;
    }

    public int updateInsurancePolicyStatusForCarInsurance(UpdateInsurancePolicyStatusForCarInsurance updateInsurancePolicyStatusForCarInsurance) {
        int update = 1;
        List<CustWarrantyCar> custWarrantyCars = getCarInfoList(updateInsurancePolicyStatusForCarInsurance.bizId, updateInsurancePolicyStatusForCarInsurance.thpBizID);
        if (custWarrantyCars == null || custWarrantyCars.isEmpty()) {
            return -1;
        }

        String time = String.valueOf(System.currentTimeMillis());
        for (CustWarrantyCar custWarrantyCar : custWarrantyCars) {
            CustWarranty custWarranty = new CustWarranty();

            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();

            custWarrantyCost.warranty_uuid = custWarrantyCar.warranty_uuid;

            custWarranty.warranty_uuid = custWarrantyCar.warranty_uuid;
            if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_STRONG)) {
                custWarranty.pre_policy_no = updateInsurancePolicyStatusForCarInsurance.ciProposalNo;
            } else if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_COMMERCIAL)) {
                custWarranty.pre_policy_no = updateInsurancePolicyStatusForCarInsurance.biProposalNo;
            }
            custWarrantyCost.pay_status = updateInsurancePolicyStatusForCarInsurance.pay_status;
            custWarranty.warranty_status = updateInsurancePolicyStatusForCarInsurance.warranty_status;

            custWarranty.updated_at = time;
            custWarrantyCost.updated_at = time;

            update = updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(custWarranty);

            if (update <= 0) {
                rollBack();
                break;
            }

            update = custWarrantyCostMapper.updateCustWarrantyCostPayStatusForCarInsuranceByWarrantyUuid(custWarrantyCost);

            if (update <= 0) {
                rollBack();
                break;
            }
        }

        return update;
    }

    public int updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(CustWarranty custWarranty) {
        return custWarrantyMapper.updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(custWarranty);
    }

    public int updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance(UpdateInsurancePolicyStatusAndWarrantyCodeForCarInsurance updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance) {
        int update = 1;
        List<CustWarrantyCar> custWarrantyCars = getCarInfoList(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.bizId, updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.thpBizID);
        if (custWarrantyCars == null || custWarrantyCars.isEmpty()) {
            return -1;
        }

        String ciUuid = null;
        String biUuid = null;

        for (CustWarrantyCar custWarrantyCar : custWarrantyCars) {
            if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_STRONG)) {
                ciUuid = custWarrantyCar.warranty_uuid;
            } else if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_COMMERCIAL)) {
                biUuid = custWarrantyCar.warranty_uuid;
            }
        }

        BigDecimal ciPremium = new BigDecimal("0.00");
        if (!StringKit.isEmpty(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo) && !StringKit.isEmpty(ciUuid)) {
            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
            custWarrantyCost.warranty_uuid = ciUuid;
            List<CustWarrantyCost> custWarrantyCostByWarrantyUuid = custWarrantyCostMapper.findCustWarrantyCost(custWarrantyCost);
            if (custWarrantyCostByWarrantyUuid != null && !custWarrantyCostByWarrantyUuid.isEmpty()) {
                for (CustWarrantyCost warrantyCostModel : custWarrantyCostByWarrantyUuid) {
                    ciPremium = ciPremium.add(new BigDecimal(warrantyCostModel.premium));
                }
            }
        }

        BigDecimal biPremium = new BigDecimal("0.00");
        if (!StringKit.isEmpty(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo) && !StringKit.isEmpty(biUuid)) {
            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();
            custWarrantyCost.warranty_uuid = biUuid;
            List<CustWarrantyCost> custWarrantyCostByWarrantyUuid = custWarrantyCostMapper.findCustWarrantyCost(custWarrantyCost);
            if (custWarrantyCostByWarrantyUuid != null && !custWarrantyCostByWarrantyUuid.isEmpty()) {
                for (CustWarrantyCost warrantyCostModel : custWarrantyCostByWarrantyUuid) {
                    biPremium = biPremium.add(new BigDecimal(warrantyCostModel.premium));
                }
            }
        }

        BigDecimal ciMoney = new BigDecimal("0.00");
        BigDecimal biMoney = new BigDecimal("0.00");
        BigDecimal total = new BigDecimal(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.payMoney);

        if (!StringKit.isEmpty(ciUuid) && !StringKit.isEmpty(biUuid)) {
            if (total.compareTo(ciPremium) >= 0) {
                ciMoney = total.subtract(ciPremium);
                biMoney = total.subtract(ciMoney);
            } else {
                ciMoney = total;
            }
        } else if (!StringKit.isEmpty(ciUuid)) {
            ciMoney = total;
        } else if (!StringKit.isEmpty(biUuid)) {
            biMoney = total;
        } else {
            // 保单没找到啊（理论上不可能）
            ciMoney = total;
        }

        String time = String.valueOf(System.currentTimeMillis());
        for (CustWarrantyCar custWarrantyCar : custWarrantyCars) {
            CustWarranty custWarranty = new CustWarranty();

            CustWarrantyCost custWarrantyCost = new CustWarrantyCost();

            custWarranty.warranty_uuid = custWarrantyCar.warranty_uuid;

            custWarrantyCost.warranty_uuid = custWarrantyCar.warranty_uuid;
            custWarrantyCost.actual_pay_time = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time;

            if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_STRONG)) {
                custWarranty.warranty_code = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo;
                custWarrantyCost.pay_money = ciMoney.toString();
            } else if (StringKit.equals(custWarrantyCar.insurance_type, CustWarrantyCar.INSURANCE_TYPE_COMMERCIAL)) {
                custWarranty.warranty_code = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo;
                custWarrantyCost.pay_money = biMoney.toString();
            }
            custWarrantyCost.pay_status = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status;
            custWarranty.warranty_status = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status;

            custWarranty.updated_at = time;
            custWarrantyCost.updated_at = time;

            update = updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(custWarranty);

            if (update <= 0) {
                rollBack();
                break;
            }

            update = updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(custWarrantyCost);

            if (update <= 0) {
                rollBack();
                break;
            }
        }

        return update;
    }

    public int updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(CustWarranty custWarranty) {
        return custWarrantyMapper.updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(custWarranty);
    }

    public int updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(custWarrantyCost);
    }

    public int updateInsurancePolicyExpressInfoForCarInsurance(UpdateInsurancePolicyExpressInfoForCarInsurance updateInsurancePolicyExpressInfoForCarInsurance) {
        int update = 1;
        List<CustWarrantyCar> custWarrantyCars = getCarInfoList(updateInsurancePolicyExpressInfoForCarInsurance.bizId, updateInsurancePolicyExpressInfoForCarInsurance.thpBizID);
        if (custWarrantyCars == null || custWarrantyCars.isEmpty()) {
            return -1;
        }

        String time = String.valueOf(System.currentTimeMillis());
        for (CustWarrantyCar custWarrantyCar : custWarrantyCars) {
            CustWarranty custWarranty = new CustWarranty();
            custWarranty.warranty_uuid = custWarrantyCar.warranty_uuid;
            custWarranty.express_no = updateInsurancePolicyExpressInfoForCarInsurance.expressNo;
            custWarranty.express_company_name = updateInsurancePolicyExpressInfoForCarInsurance.expressCompanyName;
            custWarranty.delivery_type = updateInsurancePolicyExpressInfoForCarInsurance.deliveryType;
            custWarranty.express_address = updateInsurancePolicyExpressInfoForCarInsurance.addresseeDetails;
            custWarranty.express_province_code = updateInsurancePolicyExpressInfoForCarInsurance.addresseeProvince;
            custWarranty.express_city_code = updateInsurancePolicyExpressInfoForCarInsurance.addresseeCity;
            custWarranty.express_county_code = updateInsurancePolicyExpressInfoForCarInsurance.addresseeCounty;

            custWarranty.updated_at = time;

            update = updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(custWarranty);

            if (update <= 0) {
                rollBack();
                break;
            }
        }

        return update;
    }

    public int updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(CustWarranty custWarranty) {
        return custWarrantyMapper.updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(custWarranty);
    }


    public CustWarranty findInsurancePolicyDetailByWarrantyUuid(String warrantyUuid) {
        return custWarrantyMapper.findInsurancePolicyDetailByWarrantyUuid(warrantyUuid);
    }

    public long findEffectiveInsurancePolicyCountByChannelIdAndTime(CustWarranty custWarranty) {
        return custWarrantyMapper.findEffectiveInsurancePolicyCountByChannelIdAndTime(custWarranty);
    }

    public long findInsurancePolicyListCountByTimeAndAccountUuid(CustWarranty custWarranty) {
        return custWarrantyMapper.findInsurancePolicyListCount(custWarranty);
    }

    public List<PolicyListCount> findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(CustWarranty custWarranty) {
        return custWarrantyMapper.findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(custWarranty);
    }

    public int findEffectiveInsurancePolicyCountByAgentAndTime(CustWarranty search) {
        return search != null ? custWarrantyMapper.findEffectiveInsurancePolicyCountByAgentAndTime(search) : 0;
    }

    public List<CustWarranty> findInsurancePolicyListByActualPayTime(CustWarranty custWarranty) {
        if (custWarranty == null || (StringKit.isEmpty(custWarranty.manager_uuid) && StringKit.isEmpty(custWarranty.agent_id))) {
            return new ArrayList<>();
        }
        return custWarrantyMapper.findInsurancePolicyListByActualPayTime(custWarranty);
    }

    public long findInsurancePolicyCountByActualPayTime(CustWarranty custWarranty) {
        if (custWarranty == null || (StringKit.isEmpty(custWarranty.manager_uuid) && StringKit.isEmpty(custWarranty.agent_id))) {
            return 0;
        }
        return custWarrantyMapper.findInsurancePolicyCountByActualPayTime(custWarranty);
    }

    public List<CustWarranty> findInsurancePolicyListForOnlineStore(CustWarranty custWarranty) {
        return custWarrantyMapper.findInsurancePolicyListForOnlineStore(custWarranty);
    }

    public long findInsurancePolicyCountForOnlineStore(CustWarranty custWarranty) {
        return custWarrantyMapper.findInsurancePolicyCountForOnlineStore(custWarranty);
    }

    public List<CustWarranty> findInsurancePolicyListForManagerSystem(CustWarranty custWarranty) {
        return custWarrantyMapper.findInsurancePolicyListForManagerSystem(custWarranty);
    }

    public long findInsurancePolicyCountForManagerSystem(CustWarranty custWarranty) {
        return custWarrantyMapper.findInsurancePolicyCountForManagerSystem(custWarranty);
    }

    public int findCountByAUuidWarrantyStatus(CustWarranty search) {
        return search != null ? custWarrantyMapper.findCountByAgentWarrantyStatus(search) : 0;
    }

    public int findCountByAUuidCostStatus(CustWarranty search) {
        return search != null ? custWarrantyMapper.findCountByAgentCostStatus(search) : 0;
    }

    public List<CustWarranty> findInsuranceRecordListByManagerUuid(CustWarranty search) {
        return custWarrantyMapper.findInsuranceRecordListByManagerUuid(search);
    }

    public long findInsuranceRecordCountByManagerUuid(CustWarranty search) {
        return custWarrantyMapper.findInsuranceRecordCountByManagerUuid(search);
    }

    public CustWarranty findPremiumCountByCustomerManager(CustWarranty search) {
        return search != null ? custWarrantyMapper.findPremiumCountByCustomerManager(search) : null;
    }

    public CustWarranty findByProposalNo(CustWarranty search){
        return search != null ? custWarrantyMapper.findByProposalNo(search) : null;
    }


}
