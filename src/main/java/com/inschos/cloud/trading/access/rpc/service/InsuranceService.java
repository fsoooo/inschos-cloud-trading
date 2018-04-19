package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.InsuranceConciseInfo;

import java.util.List;

/**
 * 创建日期：2018/4/17 on 11:55
 * 描述：
 * 作者：zhangyunhe
 */
public interface InsuranceService {

    List<InsuranceConciseInfo> ins_list();

    InsuranceConciseInfo ins_list(String product_id);
}
