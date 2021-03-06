package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.AccountUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.InsuranceRecordBean;
import com.inschos.cloud.trading.access.rpc.bean.ManagerUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.PolicyholderCountBean;
import com.inschos.cloud.trading.model.CustWarranty;

import java.util.List;

/**
 * 创建日期：2018/4/24 on 15:30
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyService {

    String getPolicyholderCountByTimeOrAccountId(AccountUuidBean custWarrantyPolicyholderCountBean);

    List<PolicyholderCountBean> getPolicyholderCountByTimeOrManagerUuid(ManagerUuidBean custWarrantyPolicyholderCountBean);

    /**
     * 保单状态 1投保中, 2待生效,3保障中, 4可续保,5已过保，6已退保 7已失效
     * warranty_status 5已过保
     */
    int getPolicyCountByAgentStatus(CustWarranty search);

    /**
     * 支付状态 200-待核保 201-核保中 202-核保失败 203-待支付 204-支付中 205-支付取消 206-支付成功 207支付失败 210预投保
     * pay_status
     */
    int getPolicyCountByCostStatus(CustWarranty search);

    /**
     * managerUuid，业管uuid
     * personType，人员类型
     * cardType，证件类型
     * cardCode，证件号
     *
     * @param insuranceRecord
     * @return
     */
    InsuranceRecordBean getInsuranceRecord(InsuranceRecordBean insuranceRecord);






}
