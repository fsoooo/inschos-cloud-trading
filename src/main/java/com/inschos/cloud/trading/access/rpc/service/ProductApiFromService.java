package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.ProductApiFromBean;

/**
 * author   meiming_mm@163.com
 * date     2018/7/26
 * version  v1.0.0
 */
public interface ProductApiFromService {

    ProductApiFromBean getApiFrom(String code);

}
