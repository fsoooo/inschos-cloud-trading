package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.InsuranceParticipantMapper;
import com.inschos.cloud.trading.model.InsuranceParticipantModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/3/23 on 14:13
 * 描述：保单参与人员信息（投保人，被保险人，受益人）
 * 作者：zhangyunhe
 */
@Component
public class InsuranceParticipantDao extends BaseDao {

    @Autowired
    private InsuranceParticipantMapper insuranceParticipantMapper;

    public List<InsuranceParticipantModel> findInsuranceParticipantByPrivateCode(String privateCode) {
        return insuranceParticipantMapper.findInsuranceParticipantByPrivateCode(privateCode);
    }


}
