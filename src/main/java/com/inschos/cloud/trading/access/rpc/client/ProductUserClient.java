package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.ProductUserBean;
import com.inschos.cloud.trading.access.rpc.service.ProductUserService;
import com.inschos.common.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * author   meiming_mm@163.com
 * date     2018/7/25
 * version  v1.0.0
 */
@Component
public class ProductUserClient {


    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/api/rpc/users";


    private ProductUserService getService() {
        return new HproseHttpClient(host + uri).useService(ProductUserService.class);
    }


    public ProductUserBean getProductUser(String managerUuid) {
        try {
            ProductUserService service = getService();
            return service != null ? service.getUsersByAccountId(managerUuid) : null;
        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }
    public int add(ProductUserBean bean) {
        try {
            ProductUserService service = getService();
            return service != null ? service.addUsers(bean) : 0;
        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return 0;
        }
    }
}
