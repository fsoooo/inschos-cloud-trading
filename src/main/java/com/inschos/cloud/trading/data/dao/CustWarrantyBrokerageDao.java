package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CustWarrantyBrokerageMapper;
import com.inschos.cloud.trading.data.mapper.InsurancePolicyMapper;
import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.Page;
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

    public Double findCustWarrantyBrokerageTotal(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        return custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerageModel);
    }

    /**
     * 获取指定内容的有效订单，并返回总保费
     *
     * @param insurance
     */
    public String getTotalBrokerage(InsurancePolicyModel insurance) {
        final int pageSize = 100;
        insurance.page = new Page();
        insurance.page.lastId = 0;
        insurance.page.offset = pageSize;

        BigDecimal amount = new BigDecimal("0.00");
        boolean hasNextPage;
        do {
            List<InsurancePolicyModel> effectiveInsurancePolicyListByChannelId = insurancePolicyMapper.findEffectiveInsurancePolicyByChannelIdAndTime(insurance);
            if (effectiveInsurancePolicyListByChannelId != null && !effectiveInsurancePolicyListByChannelId.isEmpty()) {
                CustWarrantyBrokerageModel custWarrantyBrokerageModel = new CustWarrantyBrokerageModel();
                for (InsurancePolicyModel insurancePolicyModel : effectiveInsurancePolicyListByChannelId) {
                    custWarrantyBrokerageModel.warranty_uuid = insurancePolicyModel.warranty_uuid;
                    Double aDouble = findCustWarrantyBrokerageTotal(custWarrantyBrokerageModel);
                    if (aDouble != null) {
                        amount = amount.add(new BigDecimal(aDouble));
                    }
                }
            }

            hasNextPage = effectiveInsurancePolicyListByChannelId != null && effectiveInsurancePolicyListByChannelId.size() >= pageSize;

            if (hasNextPage) {
                insurance.page.lastId = Long.valueOf(effectiveInsurancePolicyListByChannelId.get(effectiveInsurancePolicyListByChannelId.size() - 1).id);
            }

        } while (hasNextPage);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(amount.doubleValue());
    }

}
