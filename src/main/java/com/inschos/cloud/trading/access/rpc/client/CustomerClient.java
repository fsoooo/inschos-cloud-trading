package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.CustomerBean;
import com.inschos.cloud.trading.access.rpc.service.CustomerService;
import com.inschos.common.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * author   meiming_mm@163.com
 * date     2018/7/19
 * version  v1.0.0
 */
@Component
public class CustomerClient {

    @Value("${rpc.remote.agent.host}")
    private String host;

    private final String uri = "/rpc/customer";


    private CustomerService getService() {
        return new HproseHttpClient(host + uri).useService(CustomerService.class);
    }


    public int getFileUrl(CustomerBean bean) {
        try {
            CustomerService service = getService();
            return service != null ? service.updateCust(bean) : 0;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return 0;
        }
    }
}
