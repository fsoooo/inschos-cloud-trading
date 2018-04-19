package com.inschos.cloud.trading.model.fordao;

import com.inschos.cloud.trading.model.CarInfoModel;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;

/**
 * 创建日期：2018/4/11 on 18:24
 * 描述：
 * 作者：zhangyunhe
 */
public class InsurancePolicyAndParticipantForCarInsurance {

    public InsurancePolicyModel ciProposal;
    public InsurancePolicyModel biProposal;
    public InsuranceParticipantModel ciInsured;
    public InsuranceParticipantModel biInsured;
    public InsuranceParticipantModel ciPolicyholder;
    public InsuranceParticipantModel biPolicyholder;
    public CarInfoModel ciCarInfoModel;
    public CarInfoModel biCarInfoModel;
    public CustWarrantyCostModel ciCustWarrantyCostModel;
    public CustWarrantyCostModel biCustWarrantyCostModel;

}
