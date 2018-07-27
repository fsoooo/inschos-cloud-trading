package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.ProductApiFromBean;
import com.inschos.cloud.trading.access.rpc.service.ProductApiFromService;
import com.inschos.common.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * author   meiming_mm@163.com
 * date     2018/7/26
 * version  v1.0.0
 */
@Component
public class ProductApiFromClient {
    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/api/rpc/apiFrom";


    private ProductApiFromService getService() {
        return new HproseHttpClient(host + uri).useService(ProductApiFromService.class);
    }


    public ProductApiFromBean getApiFrom(String productCode) {
        try {
            ProductApiFromService service = getService();
            return service != null ? service.getApiFrom(productCode) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }
}
