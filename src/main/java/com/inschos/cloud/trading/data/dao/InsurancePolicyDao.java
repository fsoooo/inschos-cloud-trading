package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.InsurancePolicyMapper;
import com.inschos.cloud.trading.model.InsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/3/22 on 16:52
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class InsurancePolicyDao extends BaseDao {

    @Autowired
    public InsurancePolicyMapper insurancePolicyMapper;

    public InsurancePolicyModel findOneByUserIdAndStatus(String userId, int status) {
        return insurancePolicyMapper.findOneByUserIdAndStatus(userId, status);
    }

}
