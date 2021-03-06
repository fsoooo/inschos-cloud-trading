package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.dock.bean.InsureBean;

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
    /** 通过缴别ID获取缴别信息 */
    PayCategoryBean getOnePayCategory(long pagCategoryId);
    /** 通过产品ID获取所有缴别 */
    List<PayCategoryBean> getListPayCategory(long productId);

    //保费试算的rpc
    public ProductBean getPremium(InsureBean search);

    //保费试算的rpc
    public ProductBean getPremium(ProductBean search);

    List<ProductCategory> getCategoryList(String level);

    List<InsuranceCo> getProductCoList(String managerUuid);

    List<ProductBean> getListProduct(String name, String managerUuid);

    ProductBean getProductByCode(String code);

    ProductPayCategoryBean getProductPayCategory(long id);

    List<ProductPayCategoryBean> listProductPayCategory(List<String> id);

    List<ProductBean> getProductByAutomobileList(String managerUuid);

}
