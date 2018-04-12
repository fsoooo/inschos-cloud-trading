package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.CarInfoMapper;
import com.inschos.cloud.trading.data.mapper.InsuranceParticipantMapper;
import com.inschos.cloud.trading.data.mapper.InsurancePolicyMapper;
import com.inschos.cloud.trading.model.CarInfoModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.fordao.InsurancePolicyAndParticipantForCarInsurance;
import com.inschos.cloud.trading.model.fordao.UpdateInsurancePolicyProPolicyNoForCarInsurance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                if (StringKit.equals(carInfoModel.insurance_type, "1")) {
                    insurancePolicyModel.pro_policy_no = updateInsurancePolicyProPolicyNoForCarInsurance.ciProposalNo;
                } else if (StringKit.equals(carInfoModel.insurance_type, "2")) {
                    insurancePolicyModel.pro_policy_no = updateInsurancePolicyProPolicyNoForCarInsurance.biProposalNo;
                }
                insurancePolicyModel.check_status = updateInsurancePolicyProPolicyNoForCarInsurance.check_status;
                insurancePolicyModel.updated_at = time;
                update = updateInsurancePolicyProPolicyNoByWarrantyId(insurancePolicyModel);

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

    public int updateInsurancePolicyProPolicyNoByWarrantyId(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyProPolicyNoByWarrantyId(insurancePolicyModel);
    }

    public InsurancePolicyModel findInsurancePolicyDetailByPrivateCode(String privateCode) {
        return insurancePolicyMapper.findInsurancePolicyDetailByPrivateCode(privateCode);
    }

    public int updateInsurancePolicyUnionOrderCode(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyUnionOrderCode(insurancePolicyModel);
    }

    public int updateInsurancePolicyWarrantyCode(InsurancePolicyModel insurancePolicyModel) {
        return insurancePolicyMapper.updateInsurancePolicyWarrantyCode(insurancePolicyModel);
    }

    public String findInsurancePolicyPrivateCodeByUnionOrderCode(String unionOrderCode) {
        return insurancePolicyMapper.findInsurancePolicyPrivateCodeByUnionOrderCode(unionOrderCode);
    }

}
