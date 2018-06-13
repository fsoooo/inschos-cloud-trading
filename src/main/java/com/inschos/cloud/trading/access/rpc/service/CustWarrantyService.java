package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.AccountUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.ManagerUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.PolicyholderCountBean;
import com.inschos.cloud.trading.access.rpc.bean.WarrantyInsureBean;

import java.util.List;

/**
 * 创建日期：2018/4/24 on 15:30
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyService {

    String getPolicyholderCountByTimeOrAccountId(AccountUuidBean custWarrantyPolicyholderCountBean);

    List<PolicyholderCountBean> getPolicyholderCountByTimeOrManagerUuid(ManagerUuidBean custWarrantyPolicyholderCountBean);

    int insure(WarrantyInsureBean insureBean);

}
