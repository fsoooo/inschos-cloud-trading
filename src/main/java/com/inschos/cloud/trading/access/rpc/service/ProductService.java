package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.PayCategoryBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBrokerageBean;
import com.inschos.cloud.trading.access.rpc.bean.ProductBrokerageInfoBean;
import com.inschos.dock.bean.InsureBean;
import com.inschos.cloud.trading.access.rpc.bean.*;

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

    List<ProductCategory> getCategoryList(String level);

    List<InsuranceCo> getProductCoList(String managerUuid);

    List<ProductBean> getListProduct(String name, String managerUuid);

}
