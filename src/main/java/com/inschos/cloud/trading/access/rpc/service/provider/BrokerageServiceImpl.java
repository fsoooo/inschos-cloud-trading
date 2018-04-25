package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.ChannelIdBean;
import com.inschos.cloud.trading.access.rpc.bean.IncomeBean;
import com.inschos.cloud.trading.access.rpc.service.BrokerageService;
import com.inschos.cloud.trading.data.dao.CustWarrantyBrokerageDao;
import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public String getBrokerageByChannelIdForManagerSystem(ChannelIdBean bean) {
        String result = "0.00";
        if (bean != null) {
            CustWarrantyBrokerageModel insurance = new CustWarrantyBrokerageModel();
            if (bean.channelId != null) {
                insurance.channel_id = bean.channelId;
            } else {
                return result;
            }
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;

            result = custWarrantyBrokerageDao.findCustWarrantyBrokerageTotalByChannelId(insurance);
        }
        return result;
    }

    @Override
    public String getIncomeByManagerUuidAndAccountUuidForManagerSystem(IncomeBean bean) {
        String result = "0.00";
        if (bean != null) {
            CustWarrantyBrokerageModel insurance = new CustWarrantyBrokerageModel();
            if (bean.accountUuid != null) {
                insurance.account_uuid = bean.accountUuid;
            } else {
                return result;
            }

            if (bean.managerUuid != null) {
                insurance.manager_uuid = bean.managerUuid;
            } else {
                return result;
            }
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;

            result = custWarrantyBrokerageDao.findIncomeByManagerUuidAndAccountUuid(insurance);
        }

        return result;
    }
}
