package com.inschos.cloud.trading.data.dao;


import com.inschos.cloud.trading.data.mapper.CustWarrantyBrokerageMapper;
import com.inschos.cloud.trading.data.mapper.CustWarrantyMapper;
import com.inschos.cloud.trading.model.BrokerageStatistic;
import com.inschos.cloud.trading.model.CustWarrantyBrokerage;
import com.inschos.common.assist.kit.StringKit;
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
    public CustWarrantyMapper custWarrantyMapper;

    public int addCustWarrantyBrokerage(CustWarrantyBrokerage custWarrantyBrokerage) {
        return custWarrantyBrokerageMapper.addCustWarrantyBrokerage(custWarrantyBrokerage);
    }

    public List<CustWarrantyBrokerage> findCustWarrantyBrokerageByWarrantyUuid (String warrantyUuid){
        return custWarrantyBrokerageMapper.findCustWarrantyBrokerageByWarrantyUuid(warrantyUuid);
    }

    public String findCustWarrantyBrokerageTotalByWarrantyUuid(CustWarrantyBrokerage custWarrantyBrokerage) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerage != null && !StringKit.isEmpty(custWarrantyBrokerage.warranty_uuid)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerage);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyBrokerageTotalByChannelId(CustWarrantyBrokerage custWarrantyBrokerage) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerage != null && !StringKit.isEmpty(custWarrantyBrokerage.channel_id)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerage);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findCustWarrantyBrokerageTotalByManagerUuid(CustWarrantyBrokerage custWarrantyBrokerage) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerage != null && !StringKit.isEmpty(custWarrantyBrokerage.manager_uuid)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerage);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public String findIncomeByManagerUuidAndAccountUuid(CustWarrantyBrokerage custWarrantyBrokerage) {
        BigDecimal amount = new BigDecimal("0.00");
        if (custWarrantyBrokerage != null && !StringKit.isEmpty(custWarrantyBrokerage.manager_uuid) && !StringKit.isEmpty(custWarrantyBrokerage.account_uuid)) {
            Double custWarrantyBrokerageTotal = custWarrantyBrokerageMapper.findIncomeByManagerUuidAndAccountUuid(custWarrantyBrokerage);

            if (custWarrantyBrokerageTotal != null) {
                amount = new BigDecimal(custWarrantyBrokerageTotal);
            }

        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

    public List<BrokerageStatistic> findCustWarrantyBrokerageStatistic (CustWarrantyBrokerage custWarrantyBrokerage){
        return custWarrantyBrokerageMapper.findCustWarrantyBrokerageStatistic(custWarrantyBrokerage);
    }
    public List<BrokerageStatistic> findStatisticByAgent (CustWarrantyBrokerage search){
        return search!=null? custWarrantyBrokerageMapper.findStatisticByAgent(search):null;
    }

    public Double findCustWarrantyBrokerageCarIntegral (String warranty_uuid){
        return custWarrantyBrokerageMapper.findCustWarrantyBrokerageCarIntegral(warranty_uuid);
    }

    public int updateCustWarrantyBrokerageForCar (CustWarrantyBrokerage custWarrantyBrokerage) {
        return custWarrantyBrokerageMapper.updateCustWarrantyBrokerageForCar(custWarrantyBrokerage);
    }
}
