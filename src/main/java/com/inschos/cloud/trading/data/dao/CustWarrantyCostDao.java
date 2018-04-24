package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.InsurancePolicyMapper;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.Page;
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

}
