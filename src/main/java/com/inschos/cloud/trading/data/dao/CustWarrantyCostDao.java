package com.inschos.cloud.trading.data.dao;


import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.CustWarrantyMapper;
import com.inschos.cloud.trading.model.*;
import com.inschos.common.assist.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 创建日期：2018/4/19 on 10:56
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyCostDao extends BaseDao {

    @Autowired
    private CustWarrantyCostMapper custWarrantyCostMapper;

    @Autowired
    private CustWarrantyMapper custWarrantyMapper;

    public List<CustWarrantyCost> findCustWarrantyCost(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.findCustWarrantyCost(custWarrantyCost);
    }

    public int addCustWarrantyCost(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.addCustWarrantyCost(custWarrantyCost);
    }

    public String findCustWarrantyCostTotalByAccountUuid(CustWarrantyCost custWarrantyCost) {
        BigDecimal amount = new BigDecimal("0.00");

        if (custWarrantyCost != null && !StringKit.isEmpty(custWarrantyCost.account_uuid)) {
            Double custWarrantyCostTotalByAccountUuidOrChannelId = custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCost);

            if (custWarrantyCostTotalByAccountUuidOrChannelId != null) {
                amount = new BigDecimal(custWarrantyCostTotalByAccountUuidOrChannelId);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyCostTotalByChannelId(CustWarrantyCost custWarrantyCost) {
        BigDecimal amount = new BigDecimal("0.00");

        if (custWarrantyCost != null && !StringKit.isEmpty(custWarrantyCost.channel_id)) {
            Double custWarrantyCostTotalByAccountUuidOrChannelId = custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCost);

            if (custWarrantyCostTotalByAccountUuidOrChannelId != null) {
                amount = new BigDecimal(custWarrantyCostTotalByAccountUuidOrChannelId);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyCostTotalByManagerUuid(CustWarrantyCost custWarrantyCost) {
        BigDecimal amount = new BigDecimal("0.00");

        if (custWarrantyCost != null && !StringKit.isEmpty(custWarrantyCost.manager_uuid)) {
            Double custWarrantyCostTotalByAccountUuidOrChannelId = custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCost);

            if (custWarrantyCostTotalByAccountUuidOrChannelId != null) {
                amount = new BigDecimal(custWarrantyCostTotalByAccountUuidOrChannelId);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public List<PremiumStatistic> findCustWarrantyCostStatistic(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.findCustWarrantyCostStatistic(custWarrantyCost);
    }

    public List<BrokerageStatisticList> findInsurancePolicyBrokerageStatisticList(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.findInsurancePolicyBrokerageStatisticList(custWarrantyCost);
    }

    public long findInsurancePolicyBrokerageStatisticListCount(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.findInsurancePolicyBrokerageStatisticListCount(custWarrantyCost);
    }

    /**
     * warranty_uuid 获取第一期的缴费情况
     * @param custWarrantyCost
     * @return
     */
    public CustWarrantyCost findFirstPhase(CustWarrantyCost custWarrantyCost){
        return custWarrantyCostMapper.findFirstPhase(custWarrantyCost);
    }

    public int updatePayStatusByWarrantyUuidPhase(CustWarrantyCost costModel) {
        return costModel != null ? custWarrantyCostMapper.updatePayStatusByWarrantyUuidPhase(costModel) : 0;
    }
    public List<CustWarranty> findInsurancePolicyBillListForManagerSystem(CustWarranty custWarranty) {
        return custWarrantyCostMapper.findInsurancePolicyBillListForManagerSystem(custWarranty);
    }

    public long findInsurancePolicyBillCountForManagerSystem(CustWarranty custWarranty) {
        return custWarrantyCostMapper.findInsurancePolicyBillCountForManagerSystem(custWarranty);
    }

    public int updateSettlementAndBillUuidByCostId(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.updateSettlementAndBillUuidByCostId(custWarrantyCost);
    }

    public int updateBillUuidByCostId(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.updateBillUuidByCostId(custWarrantyCost);
    }

    public CustWarrantyBrokerage findBrokerageByCostId(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.findBrokerageByCostId(custWarrantyCost);
    }

    public List<CustWarrantyCost> findCompletePayListByManagerUuid(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.findCompletePayListByManagerUuid(custWarrantyCost);
    }

    public long findCompletePayCountByManagerUuid(CustWarrantyCost custWarrantyCost) {
        return custWarrantyCostMapper.findCompletePayCountByManagerUuid(custWarrantyCost);
    }

}
