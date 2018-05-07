package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CustWarrantyClaimsMapper;
import com.inschos.cloud.trading.model.CustWarrantyClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/3/26 on 10:39
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyClaimsDao extends BaseDao{

    @Autowired
    private CustWarrantyClaimsMapper custWarrantyClaimsMapper;

    public List<CustWarrantyClaims> findInsuranceClaimsListByUserId (CustWarrantyClaims custWarrantyClaims) {
        return custWarrantyClaimsMapper.findInsuranceClaimsListByUserId(custWarrantyClaims);
    }

}
