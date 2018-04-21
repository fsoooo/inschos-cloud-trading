package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.BrokerageBean;
import com.inschos.cloud.trading.access.rpc.bean.PremiumBean;
import com.inschos.cloud.trading.access.rpc.service.BrokerageService;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.cloud.trading.data.dao.CustWarrantyBrokerageDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
            InsurancePolicyModel insurance = new InsurancePolicyModel();
            insurance.channel_id = bean.channelId;
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;
            final int pageSize = 100;
            insurance.page = new Page();
            insurance.page.lastId = 0;
            insurance.page.offset = pageSize;

            boolean hasNextPage;
            do {
                List<InsurancePolicyModel> effectiveInsurancePolicyListByChannelId = insurancePolicyDao.findEffectiveInsurancePolicyByChannelIdAndTime(insurance);
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

                hasNextPage = effectiveInsurancePolicyListByChannelId != null && effectiveInsurancePolicyListByChannelId.size() >= pageSize;

                if (hasNextPage) {
                    insurance.page.lastId = Long.valueOf(effectiveInsurancePolicyListByChannelId.get(effectiveInsurancePolicyListByChannelId.size() - 1).id);
                }

            } while (hasNextPage);
        }
        L.log.debug("================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================" + bigDecimal.doubleValue() + "");
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return String.valueOf(decimalFormat.format(bigDecimal.doubleValue()));
    }
}
