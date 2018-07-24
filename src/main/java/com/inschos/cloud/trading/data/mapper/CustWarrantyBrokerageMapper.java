package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.BrokerageStatistic;
import com.inschos.cloud.trading.model.CustWarrantyBrokerage;

import java.util.List;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyBrokerageMapper {

    int addCustWarrantyBrokerage (CustWarrantyBrokerage custWarrantyBrokerage);

    List<CustWarrantyBrokerage> findCustWarrantyBrokerageByWarrantyUuid (String warrantyUuid);

    Double findCustWarrantyBrokerageTotal (CustWarrantyBrokerage custWarrantyBrokerage);

    Double findIncomeByManagerUuidAndAccountUuid (CustWarrantyBrokerage custWarrantyBrokerage);

    List<BrokerageStatistic> findCustWarrantyBrokerageStatistic (CustWarrantyBrokerage custWarrantyBrokerage);

    List<BrokerageStatistic> findStatisticByAgent (CustWarrantyBrokerage custWarrantyBrokerage);

    Double findCustWarrantyBrokerageCarIntegral (String warranty_uuid);

    int updateCustWarrantyBrokerageForCar (CustWarrantyBrokerage custWarrantyBrokerage);
}
