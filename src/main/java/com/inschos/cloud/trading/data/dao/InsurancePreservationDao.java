package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.InsurancePreservationMapper;
import com.inschos.cloud.trading.model.InsurancePreservationModel;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 15:50
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class InsurancePreservationDao {

    @Autowired
    public InsurancePreservationMapper insurancePreservationMapper;

    public int addInsurancePreservation(InsurancePreservationModel insurancePreservationModel) {
        return insurancePreservationMapper.addInsurancePreservation(insurancePreservationModel);
    }

    // 理赔记录
    public List<InsurancePreservationModel> findInsurancePreservationListPrivateCode (InsurancePreservationModel insurancePreservationModel) {
        return insurancePreservationMapper.findInsurancePreservationListPrivateCode(insurancePreservationModel);
    }

}
