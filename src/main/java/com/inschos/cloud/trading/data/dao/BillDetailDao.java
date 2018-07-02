package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.data.mapper.BillDetailMapper;
import com.inschos.cloud.trading.data.mapper.BillMapper;
import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.OfflineInsurancePolicyMapper;
import com.inschos.cloud.trading.model.BillDetailModel;
import com.inschos.cloud.trading.model.BillModel;
import com.inschos.cloud.trading.model.CustWarrantyCostModel;
import com.inschos.cloud.trading.model.OfflineInsurancePolicyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 创建日期：2018/6/25 on 16:00
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class BillDetailDao extends BaseDao {

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private BillDetailMapper billDetailMapper;

    @Autowired
    private CustWarrantyCostMapper custWarrantyCostMapper;

    @Autowired
    private OfflineInsurancePolicyMapper offlineInsurancePolicyMapper;

    public int addBillDetail(BillDetailModel billDetailModel) {
        return billDetailMapper.addBillDetail(billDetailModel);
    }

    public BillDetailModel findBillDetailById(String id) {
        return billDetailMapper.findBillDetailById(id);
    }

    public List<BillDetailModel> findBillDetailByBillUuid(BillDetailModel billDetailModel) {
        return billDetailMapper.findBillDetailByBillUuid(billDetailModel);
    }

    public long findBillDetailCountByBillUuid(BillDetailModel billDetailModel) {
        return billDetailMapper.findBillDetailCountByBillUuid(billDetailModel);
    }

    public BillDetailModel findBillDetailByWarrantyUuids(BillDetailModel billDetailModel) {
        return billDetailMapper.findBillDetailByWarrantyUuids(billDetailModel);
    }
}
