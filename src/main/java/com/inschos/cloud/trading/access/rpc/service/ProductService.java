package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.MyBean;
import com.inschos.cloud.trading.access.rpc.bean.MyBean2;
import com.inschos.cloud.trading.access.rpc.bean.ProductInfo;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 14:51
 * 描述：
 * 作者：zhangyunhe
 */
public interface ProductService {

    List<ProductInfo> listProduct();

    ProductInfo product_byId(String id);

    String addCompany(List<MyBean> list);

    String addProduct(List<MyBean2> list);

    List<MyBean> listCompany();



//    String addCompany(MyBean[] list);

}
