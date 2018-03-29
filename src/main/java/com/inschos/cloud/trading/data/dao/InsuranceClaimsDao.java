package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.InsuranceClaimsMapper;
import com.inschos.cloud.trading.model.InsuranceClaimsModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 创建日期：2018/3/26 on 10:39
 * 描述：
 * 作者：zhangyunhe
 */
public class InsuranceClaimsDao extends BaseDao{

    @Autowired
    private InsuranceClaimsMapper insuranceClaimsMapper;

    public List<InsuranceClaimsModel> findInsuranceClaimsListByUserId (InsuranceClaimsModel insuranceClaimsModel) {
        return insuranceClaimsMapper.findInsuranceClaimsListByUserId(insuranceClaimsModel);
    }

}
