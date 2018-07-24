package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.Bill;

import java.util.List;

/**
 * 创建日期：2018/6/25 on 14:20
 * 描述：
 * 作者：zhangyunhe
 */
public interface BillMapper {

    int addBill(Bill bill);

    int updateBillSettlementAndMoneyAndTimeByBillUuid(Bill bill);

    int updateBillMoneyByBillUuid(Bill bill);

    int updateSettlementByBillUuid(Bill bill);

    int deleteBill(Bill bill);

    Bill findBillByBillUuid(String bill_uuid);

    Bill findBillByBillName(Bill bill);

    List<Bill> findBillByManagerUuid(Bill bill);

    long findBillCountByManagerUuid(Bill bill);

    List<Bill> findBillByInsuranceCompany(Bill bill);

}
