package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.BrokerageStatisticModel;
import com.inschos.cloud.trading.model.CustWarrantyBrokerageModel;

import java.util.List;

/**
 * 创建日期：2018/3/28 on 17:11
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyBrokerageMapper {

    int addCustWarrantyBrokerage (CustWarrantyBrokerageModel custWarrantyBrokerageModel);

    List<CustWarrantyBrokerageModel> findCustWarrantyBrokerageByWarrantyUuid (String warrantyUuid);

    Double findCustWarrantyBrokerageTotal (CustWarrantyBrokerageModel custWarrantyBrokerageModel);

    Double findIncomeByManagerUuidAndAccountUuid (CustWarrantyBrokerageModel custWarrantyBrokerageModel);

    List<BrokerageStatisticModel> findCustWarrantyBrokerageStatistic (CustWarrantyBrokerageModel custWarrantyBrokerageModel);

    List<BrokerageStatisticModel> findStatisticByAgent (CustWarrantyBrokerageModel custWarrantyBrokerageModel);

    Double findCustWarrantyBrokerageCarIntegral (String warranty_uuid);

    int updateCustWarrantyBrokerageForCar (CustWarrantyBrokerageModel custWarrantyBrokerageModel);
}
