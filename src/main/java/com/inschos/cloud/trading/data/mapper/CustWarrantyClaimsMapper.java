package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarrantyClaims;

import java.util.List;

/**
 * 创建日期：2018/3/26 on 10:39
 * 描述：
 * 作者：zhangyunhe
 */
public interface CustWarrantyClaimsMapper {

    List<CustWarrantyClaims> findInsuranceClaimsListByUserId(CustWarrantyClaims custWarrantyClaims);

}
