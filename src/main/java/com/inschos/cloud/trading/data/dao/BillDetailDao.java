package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.BillDetailMapper;
import com.inschos.cloud.trading.data.mapper.BillMapper;
import com.inschos.cloud.trading.data.mapper.CustWarrantyCostMapper;
import com.inschos.cloud.trading.data.mapper.OfflineCustWarrantyMapper;
import com.inschos.cloud.trading.model.BillDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private OfflineCustWarrantyMapper offlineCustWarrantyMapper;

    public int addBillDetail(BillDetail billDetail) {
        return billDetailMapper.addBillDetail(billDetail);
    }

    public BillDetail findBillDetailById(String id) {
        return billDetailMapper.findBillDetailById(id);
    }

    public List<BillDetail> findBillDetailByBillUuid(BillDetail billDetail) {
        return billDetailMapper.findBillDetailByBillUuid(billDetail);
    }

    public long findBillDetailCountByBillUuid(BillDetail billDetail) {
        return billDetailMapper.findBillDetailCountByBillUuid(billDetail);
    }

    public BillDetail findBillDetailByWarrantyUuids(BillDetail billDetail) {
        return billDetailMapper.findBillDetailByWarrantyUuids(billDetail);
    }
}
