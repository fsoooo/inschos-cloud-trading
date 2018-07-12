package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.*;
import com.inschos.cloud.trading.model.*;
import com.inschos.cloud.trading.model.fordao.*;
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
public class InsurancePolicyDao extends BaseDao {

    @Autowired
    private InsurancePolicyMapper insurancePolicyMapper;

    @Autowired
    private InsuranceParticipantMapper insuranceParticipantMapper;

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
                add = insuranceParticipantMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.ciPolicyholder);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biPolicyholder != null) {
                add = insuranceParticipantMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.biPolicyholder);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciInsured != null) {
                add = insuranceParticipantMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.ciInsured);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biInsured != null) {
                add = insuranceParticipantMapper.addInsuranceParticipant(insurancePolicyAndParticipantForCarInsurance.biInsured);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciCarInfoModel != null) {
                add = carInfoMapper.addCarInfo(insurancePolicyAndParticipantForCarInsurance.ciCarInfoModel);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biCarInfoModel != null) {
                add = carInfoMapper.addCarInfo(insurancePolicyAndParticipantForCarInsurance.biCarInfoModel);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCostModel != null) {
                add = custWarrantyCostMapper.addCustWarrantyCost(insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCostModel);
            }

            if (add <= 0) {
                rollBack();
                return add;
            }

            if (insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCostModel != null) {
                add = custWarrantyCostMapper.addCustWarrantyCost(insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCostModel);
            }

            if (add <= 0) {
                rollBack();
            }

            if (insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerageModel != null && insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCostModel != null) {
                insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerageModel.cost_id = insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyCostModel.id;
                add = custWarrantyBrokerageMapper.addCustWarrantyBrokerage(insurancePolicyAndParticipantForCarInsurance.ciCustWarrantyBrokerageModel);
            }

            if (add <= 0) {
                rollBack();
            }

            if (insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerageModel != null && insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCostModel != null) {
                insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerageModel.cost_id = insurancePolicyAndParticipantForCarInsurance.biCustWarrantyCostModel.id;
                add = custWarrantyBrokerageMapper.addCustWarrantyBrokerage(insurancePolicyAndParticipantForCarInsurance.biCustWarrantyBrokerageModel);
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
    public int insure(InsurancePolicyModel policyModel, List<CustWarrantyCostModel> costModels) {
        int flag = 0;

        flag = insurancePolicyMapper.addInsurancePolicy(policyModel);
        if (flag > 0) {

            for (InsuranceParticipantModel participantModel : policyModel.insured_list) {
                flag = insuranceParticipantMapper.addInsuranceParticipant(participantModel);
                if (flag == 0) {
                    rollBack();
                    break;
                }
            }

            if (flag > 0) {
                for (CustWarrantyCostModel costModel : costModels) {

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

    public InsurancePolicyModel findExistsValid(InsurancePolicyModel search) {
        return search != null ? insurancePolicyMapper.findExistsValid(search) : null;
    }

    public int updateInsurancePolicyProPolicyNoForCarInsurance(UpdateInsurancePolicyProPolicyNoForCarInsurance updateInsurancePolicyProPolicyNoForCarInsurance) {
        int update = 1;
        List<CarInfoModel> byBizId = carInfoMapper.findByBizId(updateInsurancePolicyProPolicyNoForCarInsurance.bizId);
        if (byBizId != null && !byBizId.isEmpty()) {
            String time = String.valueOf(System.currentTimeMillis());
            for (CarInfoModel carInfoModel : byBizId) {
                InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
                insurancePolicyModel.warranty_uuid = carInfoModel.warranty_uuid;
                if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_STRONG)) {
                    insurancePolicyModel.pre_policy_no = updateInsurancePolicyProPolicyNoForCarInsurance.ciProposalNo;
                } else if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_COMMERCIAL)) {
                    insurancePolicyModel.pre_policy_no = updateInsurancePolicyProPolicyNoForCarInsurance.biProposalNo;
                }
                insurancePolicyModel.warranty_status = updateInsurancePolicyProPolicyNoForCarInsurance.warrantyStatus;
                insurancePolicyModel.updated_at = time;
                update = insurancePolicyMapper.updateInsurancePolicyProPolicyNoByWarrantyUuid(insurancePolicyModel);

                if (update <= 0) {
                    rollBack();
                    break;
                }

                carInfoModel.bj_code_flag = updateInsurancePolicyProPolicyNoForCarInsurance.bjCodeFlag;
                carInfoModel.updated_at = String.valueOf(System.currentTimeMillis());

                update = carInfoMapper.updateBjCodeFlagByWarrantyUuid(carInfoModel);

                if (update <= 0) {
                    rollBack();
                    break;
                }
            }
        }
        return update;
    }

    public int addInsurancePolicy(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.addInsurancePolicy(insurancePolicyModel);
    }

    public List<CarInfoModel> getCarInfoList(String bizId, String thpBizID) {
        if (!StringKit.isEmpty(bizId)) {
            return carInfoMapper.findWarrantyUuidByBizId(bizId);
        } else if (!StringKit.isEmpty(thpBizID)) {
            return carInfoMapper.findWarrantyUuidByThpBizID(thpBizID);
        }
        return null;
    }

    public int updateInsurancePolicyStatusForCarInsurance(UpdateInsurancePolicyStatusForCarInsurance updateInsurancePolicyStatusForCarInsurance) {
        int update = 1;
        List<CarInfoModel> carInfoModels = getCarInfoList(updateInsurancePolicyStatusForCarInsurance.bizId, updateInsurancePolicyStatusForCarInsurance.thpBizID);
        if (carInfoModels == null || carInfoModels.isEmpty()) {
            return -1;
        }

        String time = String.valueOf(System.currentTimeMillis());
        for (CarInfoModel carInfoModel : carInfoModels) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();

            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();

            custWarrantyCostModel.warranty_uuid = carInfoModel.warranty_uuid;

            insurancePolicyModel.warranty_uuid = carInfoModel.warranty_uuid;
            if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_STRONG)) {
                insurancePolicyModel.pre_policy_no = updateInsurancePolicyStatusForCarInsurance.ciProposalNo;
            } else if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_COMMERCIAL)) {
                insurancePolicyModel.pre_policy_no = updateInsurancePolicyStatusForCarInsurance.biProposalNo;
            }
            custWarrantyCostModel.pay_status = updateInsurancePolicyStatusForCarInsurance.pay_status;
            insurancePolicyModel.warranty_status = updateInsurancePolicyStatusForCarInsurance.warranty_status;

            insurancePolicyModel.updated_at = time;
            custWarrantyCostModel.updated_at = time;

            update = updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(insurancePolicyModel);

            if (update <= 0) {
                rollBack();
                break;
            }

            update = custWarrantyCostMapper.updateCustWarrantyCostPayStatusForCarInsuranceByWarrantyUuid(custWarrantyCostModel);

            if (update <= 0) {
                rollBack();
                break;
            }
        }

        return update;
    }

    public int updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyStatusForCarInsuranceWarrantyUuid(insurancePolicyModel);
    }

    public int updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance(UpdateInsurancePolicyStatusAndWarrantyCodeForCarInsurance updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance) {
        int update = 1;
        List<CarInfoModel> carInfoModels = getCarInfoList(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.bizId, updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.thpBizID);
        if (carInfoModels == null || carInfoModels.isEmpty()) {
            return -1;
        }

        String ciUuid = null;
        String biUuid = null;

        for (CarInfoModel carInfoModel : carInfoModels) {
            if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_STRONG)) {
                ciUuid = carInfoModel.warranty_uuid;
            } else if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_COMMERCIAL)) {
                biUuid = carInfoModel.warranty_uuid;
            }
        }

        BigDecimal ciPremium = new BigDecimal("0.00");
        if (!StringKit.isEmpty(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo) && !StringKit.isEmpty(ciUuid)) {
            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            custWarrantyCostModel.warranty_uuid = ciUuid;
            List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostMapper.findCustWarrantyCost(custWarrantyCostModel);
            if (custWarrantyCostByWarrantyUuid != null && !custWarrantyCostByWarrantyUuid.isEmpty()) {
                for (CustWarrantyCostModel warrantyCostModel : custWarrantyCostByWarrantyUuid) {
                    ciPremium = ciPremium.add(new BigDecimal(warrantyCostModel.premium));
                }
            }
        }

        BigDecimal biPremium = new BigDecimal("0.00");
        if (!StringKit.isEmpty(updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo) && !StringKit.isEmpty(biUuid)) {
            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            custWarrantyCostModel.warranty_uuid = biUuid;
            List<CustWarrantyCostModel> custWarrantyCostByWarrantyUuid = custWarrantyCostMapper.findCustWarrantyCost(custWarrantyCostModel);
            if (custWarrantyCostByWarrantyUuid != null && !custWarrantyCostByWarrantyUuid.isEmpty()) {
                for (CustWarrantyCostModel warrantyCostModel : custWarrantyCostByWarrantyUuid) {
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
        for (CarInfoModel carInfoModel : carInfoModels) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();

            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();

            insurancePolicyModel.warranty_uuid = carInfoModel.warranty_uuid;

            custWarrantyCostModel.warranty_uuid = carInfoModel.warranty_uuid;
            custWarrantyCostModel.actual_pay_time = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.actual_pay_time;

            if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_STRONG)) {
                insurancePolicyModel.warranty_code = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.ciProposalNo;
                custWarrantyCostModel.pay_money = ciMoney.toString();
            } else if (StringKit.equals(carInfoModel.insurance_type, CarInfoModel.INSURANCE_TYPE_COMMERCIAL)) {
                insurancePolicyModel.warranty_code = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.biProposalNo;
                custWarrantyCostModel.pay_money = biMoney.toString();
            }
            custWarrantyCostModel.pay_status = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.pay_status;
            insurancePolicyModel.warranty_status = updateInsurancePolicyStatusAndWarrantyCodeForCarInsurance.warranty_status;

            insurancePolicyModel.updated_at = time;
            custWarrantyCostModel.updated_at = time;

            update = updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(insurancePolicyModel);

            if (update <= 0) {
                rollBack();
                break;
            }

            update = updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(custWarrantyCostModel);

            if (update <= 0) {
                rollBack();
                break;
            }
        }

        return update;
    }

    public int updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyStatusAndWarrantyCodeForCarInsuranceByWarrantyUuid(insurancePolicyModel);
    }

    public int updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(custWarrantyCostModel);
    }

    public int updateInsurancePolicyExpressInfoForCarInsurance(UpdateInsurancePolicyExpressInfoForCarInsurance updateInsurancePolicyExpressInfoForCarInsurance) {
        int update = 1;
        List<CarInfoModel> carInfoModels = getCarInfoList(updateInsurancePolicyExpressInfoForCarInsurance.bizId, updateInsurancePolicyExpressInfoForCarInsurance.thpBizID);
        if (carInfoModels == null || carInfoModels.isEmpty()) {
            return -1;
        }

        String time = String.valueOf(System.currentTimeMillis());
        for (CarInfoModel carInfoModel : carInfoModels) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
            insurancePolicyModel.warranty_uuid = carInfoModel.warranty_uuid;
            insurancePolicyModel.express_no = updateInsurancePolicyExpressInfoForCarInsurance.expressNo;
            insurancePolicyModel.express_company_name = updateInsurancePolicyExpressInfoForCarInsurance.expressCompanyName;
            insurancePolicyModel.delivery_type = updateInsurancePolicyExpressInfoForCarInsurance.deliveryType;
            insurancePolicyModel.express_address = updateInsurancePolicyExpressInfoForCarInsurance.addresseeDetails;
            insurancePolicyModel.express_province_code = updateInsurancePolicyExpressInfoForCarInsurance.addresseeProvince;
            insurancePolicyModel.express_city_code = updateInsurancePolicyExpressInfoForCarInsurance.addresseeCity;
            insurancePolicyModel.express_county_code = updateInsurancePolicyExpressInfoForCarInsurance.addresseeCounty;

            insurancePolicyModel.updated_at = time;

            update = updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(insurancePolicyModel);

            if (update <= 0) {
                rollBack();
                break;
            }
        }

        return update;
    }

    public int updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyExpressInfoForCarInsuranceByWarrantyUuid(insurancePolicyModel);
    }


    public InsurancePolicyModel findInsurancePolicyDetailByWarrantyUuid(String warrantyUuid) {
        return insurancePolicyMapper.findInsurancePolicyDetailByWarrantyUuid(warrantyUuid);
    }

    public long findEffectiveInsurancePolicyCountByChannelIdAndTime(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findEffectiveInsurancePolicyCountByChannelIdAndTime(insurancePolicyModel);
    }

    public long findInsurancePolicyListCountByTimeAndAccountUuid(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListCount(insurancePolicyModel);
    }

    public List<PolicyListCountModel> findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(insurancePolicyModel);
    }

    public int findEffectiveInsurancePolicyCountByAgentAndTime(InsurancePolicyModel search) {
        return search != null ? insurancePolicyMapper.findEffectiveInsurancePolicyCountByAgentAndTime(search) : 0;
    }

    public List<InsurancePolicyModel> findInsurancePolicyListByActualPayTime(InsurancePolicyModel insurancePolicyModel) {
        if (insurancePolicyModel == null || (StringKit.isEmpty(insurancePolicyModel.manager_uuid) && StringKit.isEmpty(insurancePolicyModel.agent_id))) {
            return new ArrayList<>();
        }
        return insurancePolicyMapper.findInsurancePolicyListByActualPayTime(insurancePolicyModel);
    }

    public long findInsurancePolicyCountByActualPayTime(InsurancePolicyModel insurancePolicyModel) {
        if (insurancePolicyModel == null || (StringKit.isEmpty(insurancePolicyModel.manager_uuid) && StringKit.isEmpty(insurancePolicyModel.agent_id))) {
            return 0;
        }
        return insurancePolicyMapper.findInsurancePolicyCountByActualPayTime(insurancePolicyModel);
    }

    public List<InsurancePolicyModel> findInsurancePolicyListForOnlineStore(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListForOnlineStore(insurancePolicyModel);
    }

    public long findInsurancePolicyCountForOnlineStore(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyCountForOnlineStore(insurancePolicyModel);
    }

    public List<InsurancePolicyModel> findInsurancePolicyListForManagerSystem(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListForManagerSystem(insurancePolicyModel);
    }

    public long findInsurancePolicyCountForManagerSystem(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyCountForManagerSystem(insurancePolicyModel);
    }

    public int findCountByAUuidWarrantyStatus(InsurancePolicyModel search) {
        return search != null ? insurancePolicyMapper.findCountByAgentWarrantyStatus(search) : 0;
    }

    public int findCountByAUuidCostStatus(InsurancePolicyModel search) {
        return search != null ? insurancePolicyMapper.findCountByAgentCostStatus(search) : 0;
    }

    public List<InsurancePolicyModel> findInsuranceRecordListByManagerUuid(InsurancePolicyModel search) {
        return insurancePolicyMapper.findInsuranceRecordListByManagerUuid(search);
    }

    public long findInsuranceRecordCountByManagerUuid(InsurancePolicyModel search) {
        return insurancePolicyMapper.findInsuranceRecordCountByManagerUuid(search);
    }
}
