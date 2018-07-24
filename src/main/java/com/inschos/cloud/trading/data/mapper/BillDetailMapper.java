package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.BillDetail;

import java.util.List;

/**
 * 创建日期：2018/6/25 on 16:01
 * 描述：
 * 作者：zhangyunhe
 */
public interface BillDetailMapper {

    int addBillDetail(BillDetail billDetail);

    int deleteBillDetailById (String id);

    BillDetail findBillDetailById (String id);

    List<BillDetail> findBillDetailByBillUuid (BillDetail billDetail);

    long findBillDetailCountByBillUuid (BillDetail billDetail);

    BillDetail findBillDetailByWarrantyUuids (BillDetail billDetail);

}
