package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarrantyCostModel;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 11:09
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyCostMapper {

    List<CustWarrantyCostModel> findCustWarrantyCostByWarrantyUuid(CustWarrantyCostModel custWarrantyCostModel);

    int addCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel);

}
