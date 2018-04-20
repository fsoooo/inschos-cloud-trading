package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyBrokerageMapper {

    int addCustWarrantyBrokerage (CustWarrantyBrokerageModel custWarrantyBrokerageModel);

    Double findCustWarrantyBrokerageTotal (CustWarrantyBrokerageModel custWarrantyBrokerageModel);

}
