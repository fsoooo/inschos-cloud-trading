package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.AccountUuidBean;
import com.inschos.cloud.trading.access.rpc.service.CustWarrantyService;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            long count = insurancePolicyDao.findInsurancePolicyListCountTimeOrAccountId(insurancePolicyModel);
            result = String.valueOf(count);
        }
        return result;
    }
}
