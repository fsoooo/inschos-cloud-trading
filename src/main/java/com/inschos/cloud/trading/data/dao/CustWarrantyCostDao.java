package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.InsurancePolicyMapper;
import com.inschos.cloud.trading.model.*;
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
    public InsurancePolicyMapper insurancePolicyMapper;

    public List<CustWarrantyCostModel> findCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findCustWarrantyCost(custWarrantyCostModel);
    }

    public int addCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.addCustWarrantyCost(custWarrantyCostModel);
    }

    public String findCustWarrantyCostTotalByAccountUuid(CustWarrantyCostModel custWarrantyCostModel) {
        BigDecimal amount = new BigDecimal("0.00");

        if (custWarrantyCostModel != null && !StringKit.isEmpty(custWarrantyCostModel.account_uuid)) {
            Double custWarrantyCostTotalByAccountUuidOrChannelId = custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCostModel);

            if (custWarrantyCostTotalByAccountUuidOrChannelId != null) {
                amount = new BigDecimal(custWarrantyCostTotalByAccountUuidOrChannelId);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyCostTotalByChannelId(CustWarrantyCostModel custWarrantyCostModel) {
        BigDecimal amount = new BigDecimal("0.00");

        if (custWarrantyCostModel != null && !StringKit.isEmpty(custWarrantyCostModel.channel_id)) {
            Double custWarrantyCostTotalByAccountUuidOrChannelId = custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCostModel);

            if (custWarrantyCostTotalByAccountUuidOrChannelId != null) {
                amount = new BigDecimal(custWarrantyCostTotalByAccountUuidOrChannelId);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyCostTotalByManagerUuid(CustWarrantyCostModel custWarrantyCostModel) {
        BigDecimal amount = new BigDecimal("0.00");

        if (custWarrantyCostModel != null && !StringKit.isEmpty(custWarrantyCostModel.manager_uuid)) {
            Double custWarrantyCostTotalByAccountUuidOrChannelId = custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCostModel);

            if (custWarrantyCostTotalByAccountUuidOrChannelId != null) {
                amount = new BigDecimal(custWarrantyCostTotalByAccountUuidOrChannelId);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public List<PremiumStatisticModel> findCustWarrantyCostStatistic(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findCustWarrantyCostStatistic(custWarrantyCostModel);
    }

    public List<BrokerageStatisticListModel> findInsurancePolicyBrokerageStatisticList(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findInsurancePolicyBrokerageStatisticList(custWarrantyCostModel);
    }

    public long findInsurancePolicyBrokerageStatisticListCount(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findInsurancePolicyBrokerageStatisticListCount(custWarrantyCostModel);
    }

    /**
     * warranty_uuid 获取第一期的缴费情况
     * @param custWarrantyCostModel
     * @return
     */
    public CustWarrantyCostModel findFirstPhase(CustWarrantyCostModel custWarrantyCostModel){
        return custWarrantyCostMapper.findFirstPhase(custWarrantyCostModel);
    }

    public int updatePayStatusByWarrantyUuidPhase(CustWarrantyCostModel costModel) {
        return costModel != null ? custWarrantyCostMapper.updatePayStatusByWarrantyUuidPhase(costModel) : 0;
    }
    public List<InsurancePolicyModel> findInsurancePolicyBillListForManagerSystem(InsurancePolicyModel insurancePolicyModel) {
        return custWarrantyCostMapper.findInsurancePolicyBillListForManagerSystem(insurancePolicyModel);
    }

    public long findInsurancePolicyBillCountForManagerSystem(InsurancePolicyModel insurancePolicyModel) {
        return custWarrantyCostMapper.findInsurancePolicyBillCountForManagerSystem(insurancePolicyModel);
    }

    public int updateSettlementAndBillUuidByCostId(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.updateSettlementAndBillUuidByCostId(custWarrantyCostModel);
    }

    public int updateBillUuidByCostId(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.updateBillUuidByCostId(custWarrantyCostModel);
    }

    public CustWarrantyBrokerageModel findBrokerageByCostId(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findBrokerageByCostId(custWarrantyCostModel);
    }

    public List<CustWarrantyCostModel> findCompletePayListByManagerUuid(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findCompletePayListByManagerUuid(custWarrantyCostModel);
    }

    public long findCompletePayCountByManagerUuid(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findCompletePayCountByManagerUuid(custWarrantyCostModel);
    }

}
