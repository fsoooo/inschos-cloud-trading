package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.CustWarrantyPolicyholderCountBean;

/**
 * 创建日期：2018/4/24 on 15:30
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyService {

    String getPolicyholderCountByTimeOrAccountId(CustWarrantyPolicyholderCountBean custWarrantyPolicyholderCountBean);

}
