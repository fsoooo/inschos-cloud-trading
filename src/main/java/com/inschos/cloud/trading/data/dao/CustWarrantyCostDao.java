package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 10:56
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CustWarrantyCostDao extends BaseDao {

    @Autowired
    private CustWarrantyCostMapper custWarrantyCostMapper;

    public List<CustWarrantyCostModel> findCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findCustWarrantyCost(custWarrantyCostModel);
    }

    public Double findCustWarrantyCostTotal (CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.findCustWarrantyCostTotal(custWarrantyCostModel);
    }

    public int addCustWarrantyCost(CustWarrantyCostModel custWarrantyCostModel) {
        return custWarrantyCostMapper.addCustWarrantyCost(custWarrantyCostModel);
    }

}
