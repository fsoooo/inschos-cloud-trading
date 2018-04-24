package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.AccountUuidBean;
import com.inschos.cloud.trading.access.rpc.bean.ChannelIdBean;
import com.inschos.cloud.trading.access.rpc.service.PremiumService;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public String getPremiumByChannelIdForManagerSystem(ChannelIdBean bean) {
        String result = "0.00";
        if (bean != null) {
            CustWarrantyCostModel insurance = new CustWarrantyCostModel();
            if (bean.channelId != null) {
                insurance.channel_id = bean.channelId;
            } else {
                return "0.00";
            }
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;
            result = custWarrantyCostDao.findCustWarrantyCostTotalByChannelId(insurance);
        }
        return result;
    }

    @Override
    public String getPremiumByAccountUuidForManagerSystem(AccountUuidBean bean) {
        String result = "0.00";
        if (bean != null) {
            CustWarrantyCostModel insurance = new CustWarrantyCostModel();
            if (bean.accountUuid != null) {
                insurance.account_uuid = bean.accountUuid;
            } else {
                return "0.00";
            }
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;
            result = custWarrantyCostDao.findCustWarrantyCostTotalByAccountUuid(insurance);
        }
        return result;
    }

    @Override
    public String getPremiumCountByChannelIdForManagerSystem(ChannelIdBean bean) {
        String result = "0";
        if (bean != null) {
            InsurancePolicyModel insurance = new InsurancePolicyModel();
            if (bean.channelId != null) {
                insurance.channel_id = bean.channelId;
            } else {
                return "0";
            }
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;
            result = String.valueOf(insurancePolicyDao.findEffectiveInsurancePolicyCountByChannelIdAndTime(insurance));
        }
        return result;
    }
}
