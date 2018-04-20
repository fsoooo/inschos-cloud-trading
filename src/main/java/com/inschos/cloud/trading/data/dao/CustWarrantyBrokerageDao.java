package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CustWarrantyBrokerageMapper;
import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：佣金
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyBrokerageDao {

    @Autowired
    private CustWarrantyBrokerageMapper custWarrantyBrokerageMapper;

    public int addCustWarrantyBrokerage(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        return custWarrantyBrokerageMapper.addCustWarrantyBrokerage(custWarrantyBrokerageModel);
    }

    public Double findCustWarrantyBrokerageTotal(CustWarrantyBrokerageModel custWarrantyBrokerageModel) {
        return custWarrantyBrokerageMapper.findCustWarrantyBrokerageTotal(custWarrantyBrokerageModel);
    }

}
