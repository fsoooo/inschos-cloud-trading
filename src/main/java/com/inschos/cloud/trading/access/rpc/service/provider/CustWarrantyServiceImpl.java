package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.service.CustWarrantyService;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.assist.kit.WarrantyUuidWorker;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.PolicyListCountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建日期：2018/4/24 on 15:31
 * 描述：
 * 作者：zhangyunhe
 */
@Service
public class CustWarrantyServiceImpl implements CustWarrantyService {

    @Autowired
    private InsurancePolicyDao insurancePolicyDao;

    @Override
    public String getPolicyholderCountByTimeOrAccountId(AccountUuidBean custWarrantyPolicyholderCountBean) {
        String result = "0";
        if (custWarrantyPolicyholderCountBean != null) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.accountUuid)) {
                insurancePolicyModel.account_uuid = custWarrantyPolicyholderCountBean.accountUuid;
            } else {
                return result;
            }

            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.managerUuid)) {
                insurancePolicyModel.manager_uuid = custWarrantyPolicyholderCountBean.managerUuid;
            } else {
                return result;
            }

            insurancePolicyModel.start_time = custWarrantyPolicyholderCountBean.startTime;
            insurancePolicyModel.end_time = custWarrantyPolicyholderCountBean.endTime;
            long count = insurancePolicyDao.findInsurancePolicyListCountByTimeAndAccountUuid(insurancePolicyModel);
            result = String.valueOf(count);
        }
        return result;
    }

    @Override
    public List<PolicyholderCountBean> getPolicyholderCountByTimeOrManagerUuid(ManagerUuidBean custWarrantyPolicyholderCountBean) {
        List<PolicyholderCountBean> result = new ArrayList<>();
        if (custWarrantyPolicyholderCountBean != null) {
            InsurancePolicyModel insurancePolicyModel = new InsurancePolicyModel();
            if (!StringKit.isEmpty(custWarrantyPolicyholderCountBean.managerUuid)) {
                insurancePolicyModel.manager_uuid = custWarrantyPolicyholderCountBean.managerUuid;
            } else {
                return result;
            }

            if (custWarrantyPolicyholderCountBean.productIds == null) {
                return result;
            }

            StringBuilder stringBuilder = new StringBuilder();
            int size = custWarrantyPolicyholderCountBean.productIds.size();
            for (int i = 0; i < size; i++) {
                stringBuilder.append(custWarrantyPolicyholderCountBean.productIds.get(i));
                if (i < size - 1) {
                    stringBuilder.append(",");
                }
            }
            insurancePolicyModel.product_id_string = stringBuilder.toString();

            insurancePolicyModel.start_time = custWarrantyPolicyholderCountBean.startTime;
            insurancePolicyModel.end_time = custWarrantyPolicyholderCountBean.endTime;
            List<PolicyListCountModel> insurancePolicyListCountByTimeAndManagerUuidAndProductId = insurancePolicyDao.findInsurancePolicyListCountByTimeAndManagerUuidAndProductId(insurancePolicyModel);
            Map<String, PolicyListCountModel> map = new HashMap<>();

            for (PolicyListCountModel policyListCountModel : insurancePolicyListCountByTimeAndManagerUuidAndProductId) {
                map.put(policyListCountModel.product_id, policyListCountModel);
            }

            for (String productId : custWarrantyPolicyholderCountBean.productIds) {
                PolicyListCountModel policyListCountModel = map.get(productId);
                PolicyholderCountBean policyholderCountBean;
                if (policyListCountModel == null) {
                    policyholderCountBean = new PolicyholderCountBean(productId, 0);
                } else {
                    policyholderCountBean = new PolicyholderCountBean(policyListCountModel);
                }
                result.add(policyholderCountBean);
            }

        }
        return result;
    }

    @Override
    public int insure(WarrantyInsureBean insureBean) {
        if(insureBean!=null && insureBean.policyholder!=null && !StringKit.isEmpty(insureBean.policyholder.cardCode)){
            InsurancePolicyModel policyModel = new InsurancePolicyModel();
            String warrantyUuid = String.valueOf(WarrantyUuidWorker.getWorker(2, 1).nextId());
            policyModel.warranty_uuid = warrantyUuid;
            policyModel.account_uuid = insureBean.accountUuid;
            policyModel.manager_uuid = insureBean.managerUuid;
            policyModel.product_id = insureBean.productId;
            policyModel.agent_id = insureBean.agentId;
            policyModel.channel_id = insureBean.channelId;
            policyModel.plan_id = insureBean.planId;
            policyModel.start_time = insureBean.startTime;
            policyModel.end_time = insureBean.endTime;
            policyModel.count = String.valueOf(insureBean.count);
            policyModel.by_stages_way = insureBean.payCategoryName;
//            policyModel.pa = insureBean.payCategoryId;
            policyModel.insured_list = new ArrayList<>();
            policyModel.insured_list.add(insureBean.policyholder.toParticipant(warrantyUuid, InsuranceParticipantModel.TYPE_POLICYHOLDER));
            if(insureBean.beneficiary!=null){
                policyModel.insured_list.add(insureBean.beneficiary.toParticipant(warrantyUuid, InsuranceParticipantModel.TYPE_BENEFICIARY));
            }
            if(insureBean.recognizees!=null){
                for (InsurePersonBean recognizee : insureBean.recognizees) {
                    policyModel.insured_list.add(recognizee.toParticipant(warrantyUuid, InsuranceParticipantModel.TYPE_INSURED));
                }
            }

        }
        return 0;
    }


}
