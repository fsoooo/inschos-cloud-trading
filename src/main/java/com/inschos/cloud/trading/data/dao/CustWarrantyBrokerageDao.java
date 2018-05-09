package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.CustWarrantyBrokerageMapper;
import com.inschos.cloud.trading.data.mapper.InsurancePolicyMapper;
import com.inschos.cloud.trading.model.BrokerageStatisticModel;
import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：佣金
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyBrokerageDao {

    @Autowired
    private CustWarrantyBrokerageMapper custWarrantyBrokerageMapper;

    @Autowired
    public InsurancePolicyMapper insurancePolicyMapper;

    public int addCustWarrantyBrokerage(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        return custWarrantyBrokerageMapper.addCustWarrantyBrokerage(custWarrantyBrokerageModel);
    }

    public String findCustWarrantyBrokerageTotalByWarrantyUuid(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerageModel != null && !StringKit.isEmpty(custWarrantyBrokerageModel.warranty_uuid)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerageModel);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyBrokerageTotalByChannelId(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerageModel != null && !StringKit.isEmpty(custWarrantyBrokerageModel.channel_id)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerageModel);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyBrokerageTotalByManagerUuid(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerageModel != null && !StringKit.isEmpty(custWarrantyBrokerageModel.manager_uuid)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerageModel);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findIncomeByManagerUuidAndAccountUuid(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerageModel != null && !StringKit.isEmpty(custWarrantyBrokerageModel.manager_uuid) && !StringKit.isEmpty(custWarrantyBrokerageModel.account_uuid)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findIncomeByManagerUuidAndAccountUuid(custWarrantyBrokerageModel);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public List<BrokerageStatisticModel> findCustWarrantyBrokerageStatistic (CustWarrantyBrokerageModel custWarrantyBrokerageModel){
        return custWarrantyBrokerageMapper.findCustWarrantyBrokerageStatistic(custWarrantyBrokerageModel);
    }
}
