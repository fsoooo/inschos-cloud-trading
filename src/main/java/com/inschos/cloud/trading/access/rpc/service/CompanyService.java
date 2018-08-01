package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.InsuranceCompanyBean;

import java.util.List;

/**
 * 创建日期：2018/6/27 on 15:18
 * 描述：
 * 作者：zhangyunhe
 */
public interface CompanyService {

    List<InsuranceCompanyBean> getListInsuranceCompany(InsuranceCompanyBean insuranceCompanyBean);

    InsuranceCompanyBean getCompany(long id);

    List<InsuranceCompanyBean> getCompanyList(List<String> companyId);

    List<InsuranceCompanyBean> getCompanyAll();
}
