package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.PolicyListCountModel;
import com.inschos.cloud.trading.model.PremiumStatisticModel;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 11:09
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyCostMapper {

    List<CustWarrantyCostModel> findCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel);

    int addCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel);

    int updateCustWarrantyCostActualPayTimeAndPayMoneyAndPayStatusForCarInsuranceByWarrantyUuid(CustWarrantyCostModel custWarrantyCostModel);

    int updateCustWarrantyCostPayStatusForCarInsuranceByWarrantyUuid(CustWarrantyCostModel custWarrantyCostModel);

    Double findCustWarrantyCostTotal(CustWarrantyCostModel custWarrantyCostModel);

    List<InsurancePolicyModel> findInsurancePolicyListForInsuring(InsurancePolicyModel insurancePolicyModel);

    long findInsurancePolicyCountForInsuring(InsurancePolicyModel insurancePolicyModel);

    List<PremiumStatisticModel> findCustWarrantyCostStatistic(CustWarrantyCostModel custWarrantyCostModel);

}
