package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.BrokerageCommissionMapper;
import com.inschos.cloud.trading.model.BrokerageCommissionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：佣金
 * 作者：zhangyunhe
 */
@Component
public class BrokerageCommissionDao {

    @Autowired
    private BrokerageCommissionMapper brokerageCommissionMapper;

    public int addBrokerageCommission(BrokerageCommissionModel brokerageCommissionModel) {
        return brokerageCommissionMapper.addBrokerageCommission(brokerageCommissionModel);
    }
}
