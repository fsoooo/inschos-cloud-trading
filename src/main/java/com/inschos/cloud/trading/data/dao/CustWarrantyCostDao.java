package com.inschos.cloud.trading.data.dao;

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

    public Double findCustWarrantyCostTotal (CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCostModel);
    }

    public int addCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.addCustWarrantyCost(custWarrantyCostModel);
    }

    /**
     * 获取指定内容的有效订单，并返回总保费
     *
     * @param insurance
     */
    public String getTotalPremium(InsurancePolicyModel insurance) {
        final int pageSize = 100;
        insurance.page = new Page();
        insurance.page.lastId = 0;
        insurance.page.offset = pageSize;

        BigDecimal amount = new BigDecimal("0.00");
        boolean hasNextPage;
        do {
            List<InsurancePolicyModel> effectiveInsurancePolicyListByChannelId = insurancePolicyMapper.findEffectiveInsurancePolicyByChannelIdAndTime(insurance);
            if (effectiveInsurancePolicyListByChannelId != null && !effectiveInsurancePolicyListByChannelId.isEmpty()) {
                CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
                for (InsurancePolicyModel model : effectiveInsurancePolicyListByChannelId) {
                    custWarrantyCostModel.warranty_uuid = model.warranty_uuid;
                    Double daoCustWarrantyCostTotal = findCustWarrantyCostTotal(custWarrantyCostModel);
                    if (daoCustWarrantyCostTotal != null) {
                        amount = amount.add(new BigDecimal(daoCustWarrantyCostTotal));
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
