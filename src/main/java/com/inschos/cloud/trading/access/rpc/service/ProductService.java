package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.assist.kit.L;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 14:51
 * 描述：
 * 作者：zhangyunhe
 */
public interface ProductService {

    List<ProductBean> getPlatformProductAll(String managerUuid, int categoryId);

    ProductBean getProduct(long productId);

    ProductBrokerageInfoBean getBrokerage(ProductBrokerageBean search);

    List<ProductBean> getProductList(List<String> ids);

    List<ProductCategory> getCategoryList(String level);

    List<InsuranceCo> getProductCoList(String managerUuid);

    List<InsuranceCompanyBean> getListInsuranceCompany(InsuranceCompanyBean insuranceCompanyBean);

    List<ProductBean> getListProduct(String name, String managerUuid);
}
