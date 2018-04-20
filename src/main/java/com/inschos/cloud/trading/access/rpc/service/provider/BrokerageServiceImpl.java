package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.BrokerageBean;
import com.inschos.cloud.trading.access.rpc.bean.PremiumBean;
import com.inschos.cloud.trading.access.rpc.service.BrokerageService;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.cloud.trading.data.dao.CustWarrantyBrokerageDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建日期：2018/4/20 on 16:44
 * 描述：
 * 作者：zhangyunhe
 */
@Service
public class BrokerageServiceImpl implements BrokerageService {

    @Autowired
    private InsurancePolicyDao insurancePolicyDao;

    @Autowired
    private CustWarrantyBrokerageDao custWarrantyBrokerageDao;

    @Override
    public String getBrokerageByChannelIdForManagerSystem(BrokerageBean bean) {
        BigDecimal bigDecimal = new BigDecimal("0.00");
        if (bean != null) {
            List<InsurancePolicyModel> effectiveInsurancePolicyListByChannelId = insurancePolicyDao.findEffectiveInsurancePolicyListByChannelId(bean.channelId);
            if (effectiveInsurancePolicyListByChannelId != null && !effectiveInsurancePolicyListByChannelId.isEmpty()) {
                CustWarrantyBrokerageModel custWarrantyBrokerageModel = new CustWarrantyBrokerageModel();
                for (InsurancePolicyModel insurancePolicyModel : effectiveInsurancePolicyListByChannelId) {
                    custWarrantyBrokerageModel.warranty_uuid = insurancePolicyModel.warranty_uuid;
                    Double aDouble = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotal(custWarrantyBrokerageModel);
                    if (aDouble != null) {
                        bigDecimal = bigDecimal.add(new BigDecimal(aDouble));
                    }
                }
            }
        }
        L.log.debug("================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================" + bigDecimal.doubleValue() + "");
        return String.valueOf(bigDecimal.doubleValue());
    }
}
