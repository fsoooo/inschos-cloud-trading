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
    private CustWarrantyBrokerageDao custWarrantyBrokerageDao;

    @Override
    public String getBrokerageByChannelIdForManagerSystem(BrokerageBean bean) {
        String result = "0.00";
        if (bean != null) {
            InsurancePolicyModel insurance = new InsurancePolicyModel();
            insurance.channel_id = bean.channelId;
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;

            result = custWarrantyBrokerageDao.getTotalBrokerage(insurance);
        }
        return result;
    }
}
