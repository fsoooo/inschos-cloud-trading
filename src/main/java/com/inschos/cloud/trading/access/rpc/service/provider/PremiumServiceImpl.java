package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.access.rpc.bean.PremiumBean;
import com.inschos.cloud.trading.access.rpc.service.PremiumService;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.cloud.trading.data.dao.CustWarrantyCostDao;
import com.inschos.cloud.trading.data.dao.InsurancePolicyDao;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    public String getPremiumByChannelIdForManagerSystem(PremiumBean bean) {
        String result = "0.00";
        if (bean != null) {
            InsurancePolicyModel insurance = new InsurancePolicyModel();
            if (bean.channelId != null) {
                insurance.channel_id = bean.channelId;
            } else {
                insurance.channel_id = "-1";
            }
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;
            result = custWarrantyCostDao.getTotalPremium(insurance);
        }
        return result;
    }

    @Override
    public String getPremiumCountByChannelIdForManagerSystem(PremiumBean bean) {
        String result = "0";
        if (bean != null) {
            InsurancePolicyModel insurance = new InsurancePolicyModel();
            if (bean.channelId != null) {
                insurance.channel_id = bean.channelId;
            } else {
                insurance.channel_id = "-1";
            }
            insurance.start_time = bean.startTime;
            insurance.end_time = bean.endTime;
            result = String.valueOf(insurancePolicyDao.findEffectiveInsurancePolicyCountByChannelIdAndTime(insurance));
        }
        return result;
    }
}
