package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.AccountUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.ManagerUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.PolicyholderCountBean;
import com.inschos.cloud.trading.access.rpc.service.CustWarrantyService;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
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
}
