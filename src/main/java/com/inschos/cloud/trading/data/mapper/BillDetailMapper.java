package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.BillDetailModel;

import java.util.List;

/**
 * 创建日期：2018/6/25 on 16:01
 * 描述：
 * 作者：zhangyunhe
 */
public interface BillDetailMapper {

    int addBillDetail(BillDetailModel billDetailModel);

    int deleteBillDetailById (String id);

    BillDetailModel findBillDetailById (String id);

    List<BillDetailModel> findBillDetailByBillUuid (BillDetailModel billDetailModel);

    long findBillDetailCountByBillUuid (BillDetailModel billDetailModel);

}
