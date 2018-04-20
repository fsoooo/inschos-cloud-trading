package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.CarInfoMapper;
import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.InsuranceParticipantMapper;
import com.inschos.cloud.trading.data.mapper.InsurancePolicyMapper;
import com.inschos.cloud.trading.model.CarInfoModel;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.fordao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建日期：2018/3/22 on 16:52
 * 描述：保单信息
 * 作者：zhangyunhe
 */
@Component
public class InsurancePolicyDao extends BaseDao {

    @Autowired
    public InsurancePolicyMapper insurancePolicyMapper;

    @Autowired
    public InsuranceParticipantMapper insuranceParticipantMapper;

    @Autowired
    public CarInfoMapper carInfoMapper;

    @Autowired
    public CustWarrantyCostMapper custWarrantyCostMapper;

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

            return add;
        } else {
            return 1;
        }
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

    public List<InsurancePolicyModel> findInsurancePolicyListByWarrantyStatusOrSearch(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListByWarrantyStatusOrSearch(insurancePolicyModel);
    }

    public int addInsurancePolicy(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.addInsurancePolicy(insurancePolicyModel);
    }

    public long findInsurancePolicyCountByWarrantyStatus(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyCountByWarrantyStatus(insurancePolicyModel);
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

        if (ciPremium != null && ciPremium.compareTo(BigDecimal.ZERO) != 0 && biPremium.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal add1 = ciPremium.add(biPremium);
            if (add1.compareTo(total) == 0) {
                ciMoney = ciPremium;
                biMoney = biPremium;
            } else {
                BigDecimal k = biPremium.divide(ciPremium, BigDecimal.ROUND_HALF_UP);
                BigDecimal add = k.add(new BigDecimal(1));
                ciMoney = total.divide(add, BigDecimal.ROUND_HALF_UP);
                biMoney = total.subtract(ciMoney);
            }
        } else if (ciPremium != null && ciPremium.compareTo(BigDecimal.ZERO) == 0 && biPremium.compareTo(BigDecimal.ZERO) != 0) {
            biMoney = total;
        } else if (ciPremium != null && ciPremium.compareTo(BigDecimal.ZERO) != 0 && biPremium.compareTo(BigDecimal.ZERO) == 0) {
            ciMoney = total;
        } else {
            // 理论上不可能
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

    private BigDecimal getBigDecimal(String premium) {
        if (StringKit.isNumeric(premium)) {
            return new BigDecimal(premium);
        } else {
            return null;
        }
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

    public List<InsurancePolicyModel> findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelId(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelId(insurancePolicyModel);
    }

    public long findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelIdCount(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListByWarrantyStatusOrSearchOrTimeOrWarrantyTypeOrWarrantyFromOrChannelIdCount(insurancePolicyModel);
    }

    public InsurancePolicyModel findInsurancePolicyDetailByWarrantyUuid(String warrantyUuid) {
        return insurancePolicyMapper.findInsurancePolicyDetailByWarrantyUuid(warrantyUuid);
    }

    public List<InsurancePolicyModel> findInsurancePolicyListBySearchOrTimeOrChannelId(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListBySearchOrTimeOrChannelId(insurancePolicyModel);
    }

    public long findInsurancePolicyListBySearchOrTimeOrChannelIdCount(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.findInsurancePolicyListBySearchOrTimeOrChannelIdCount(insurancePolicyModel);
    }

    public List<InsurancePolicyModel> findEffectiveInsurancePolicyListByChannelId(String channelId) {
        return insurancePolicyMapper.findEffectiveInsurancePolicyListByChannelId(channelId);
    }

    // NOTENABLED: 2018/4/14
    public int updateInsurancePolicyUnionOrderCode(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyUnionOrderCode(insurancePolicyModel);
    }

    // NOTENABLED: 2018/4/14
    public int updateInsurancePolicyWarrantyCode(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyWarrantyCode(insurancePolicyModel);
    }

    // NOTENABLED: 2018/4/14
    public String findInsurancePolicyPrivateCodeByUnionOrderCode(String unionOrderCode) {
        return insurancePolicyMapper.findInsurancePolicyPrivateCodeByUnionOrderCode(unionOrderCode);
    }

}
