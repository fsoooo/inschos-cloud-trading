package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.*;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 11:09
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyCostMapper {

    List<CustWarrantyCost> findCustWarrantyCost(CustWarrantyCost custWarrantyCost);

    int addCustWarrantyCost(CustWarrantyCost custWarrantyCost);

    int updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(CustWarrantyCost custWarrantyCost);

    int updateCustWarrantyCostPayStatusForCarInsuranceByWarrantyUuid(CustWarrantyCost custWarrantyCost);

    int updatePayStatusByWarrantyUuidPhase(CustWarrantyCost custWarrantyCost);

    int updatePayResult(CustWarrantyCost cost);

    Double findCustWarrantyCostTotal(CustWarrantyCost custWarrantyCost);

    List<CustWarranty> findInsurancePolicyListForInsuring(CustWarranty custWarranty);

    long findInsurancePolicyCountForInsuring(CustWarranty custWarranty);

    List<PremiumStatistic> findCustWarrantyCostStatistic(CustWarrantyCost custWarrantyCost);

    List<BrokerageStatisticList> findInsurancePolicyBrokerageStatisticList(CustWarrantyCost custWarrantyCost);

    long findInsurancePolicyBrokerageStatisticListCount(CustWarrantyCost custWarrantyCost);

    //获取第一期的缴费情况
    CustWarrantyCost findFirstPhase(CustWarrantyCost search);

    List<CustWarranty> findInsurancePolicyBillListForManagerSystem(CustWarranty custWarranty);

    long findInsurancePolicyBillCountForManagerSystem(CustWarranty custWarranty);

    int updateSettlementAndBillUuidByCostId(CustWarrantyCost custWarrantyCost);

    int updateBillUuidByCostId(CustWarrantyCost custWarrantyCost);

    CustWarrantyBrokerage findBrokerageByCostId(CustWarrantyCost custWarrantyCost);

    List<CustWarrantyCost> findCompletePayListByManagerUuid (CustWarrantyCost custWarrantyCost);

    long findCompletePayCountByManagerUuid (CustWarrantyCost custWarrantyCost);
}
