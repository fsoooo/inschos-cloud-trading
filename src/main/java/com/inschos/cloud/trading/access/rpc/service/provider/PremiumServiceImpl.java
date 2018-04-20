package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.GetPremiumByChannelIdForManagerSystem;
import com.inschos.cloud.trading.access.rpc.service.PremiumService;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建日期：2018/4/20 on 16:51
 * 描述：
 * 作者：zhangyunhe
 */
@Service
public class PremiumServiceImpl implements PremiumService {

    @Autowired
    private InsurancePolicyDao insurancePolicyDao;

    @Autowired
    private CustWarrantyCostDao custWarrantyCostDao;

    @Override
    public String getPremiumByChannelIdForManagerSystem(GetPremiumByChannelIdForManagerSystem bean) {
        List<InsurancePolicyModel> effectiveInsurancePolicyListByChannelId = insurancePolicyDao.findEffectiveInsurancePolicyListByChannelId(bean.channelId);
        BigDecimal bigDecimal = new BigDecimal("0.00");
        if (effectiveInsurancePolicyListByChannelId != null && !effectiveInsurancePolicyListByChannelId.isEmpty()) {
            CustWarrantyCostModel custWarrantyCostModel = new CustWarrantyCostModel();
            for (InsurancePolicyModel insurancePolicyModel : effectiveInsurancePolicyListByChannelId) {
                custWarrantyCostModel.warranty_uuid = insurancePolicyModel.warranty_uuid;
                Double aDouble = custWarrantyCostDao.findCustWarrantyCostTotal(custWarrantyCostModel);
                if (aDouble != null) {
                    bigDecimal = bigDecimal.add(new BigDecimal(aDouble));
                }
            }
        }
        return String.valueOf(bigDecimal.doubleValue());
    }
}
