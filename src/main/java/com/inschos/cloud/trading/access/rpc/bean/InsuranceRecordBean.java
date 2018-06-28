package com.inschos.cloud.trading.access.rpc.bean;

import com.inschos.cloud.trading.access.http.controller.bean.InsurancePolicy;
import com.inschos.cloud.trading.access.http.controller.bean.PageBean;
import com.inschos.cloud.trading.model.InsurancePolicyModel;

import java.util.List;

/**
 * 创建日期：2018/6/28 on 15:13
 * 描述：
 * 作者：zhangyunhe
 */
public class InsuranceRecordBean {

    // 业管uuid
    public String managerUuid;
    // 人员类型: 1投保人 2被保人 3受益人
    public String personType;
    // 证件类型：1-身份证，2-护照，3-军官证
    public String cardType;
    // 证件号码
    public String cardCode;
    public String pageSize;
    public String pageNum;
    public String lastId;

    public List<InsurancePolicy.GetInsurancePolicy> data;
    public PageBean page;

}
