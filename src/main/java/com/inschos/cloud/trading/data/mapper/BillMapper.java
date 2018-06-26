package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.BillModel;

import java.util.List;

/**
 * 创建日期：2018/6/25 on 14:20
 * 描述：
 * 作者：zhangyunhe
 */
public interface BillMapper {

    int addBill(BillModel billModel);

    int updateBillSettlementAndMoneyAndTimeByBillUuid(BillModel billModel);

    int updateBillMoneyByBillUuid(BillModel billModel);

    int updateSettlementByBillUuid(BillModel billModel);

    int deleteBill(BillModel billModel);

    BillModel findBillByBillUuid(String bill_uuid);

    List<BillModel> findBillByManagerUuid(BillModel billModel);

    long findBillCountByManagerUuid(BillModel billModel);

    List<BillModel> findBillByInsuranceCompany(BillModel billModel);

}
