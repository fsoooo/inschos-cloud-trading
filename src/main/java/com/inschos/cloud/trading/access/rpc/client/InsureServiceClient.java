package com.inschos.cloud.trading.access.rpc.client;


import com.inschos.cloud.trading.access.rpc.bean.InsureDock;
import com.inschos.common.assist.kit.L;
import com.inschos.dock.api.InsureService;
import com.inschos.dock.api.QueryService;
import com.inschos.dock.bean.*;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/6/25.
 */
@Component
public class InsureServiceClient {


    @Value("${rpc.remote.dock.host}")
    private String dockHost;

    private final String uri = "/rpc/insure";

    private InsureService getService(String productKey) {

        String host = dockHost + (InsureDock.isNewProduct(productKey) ? productKey.toLowerCase() : "general");
        return new HproseHttpClient(host + uri).useService(InsureService.class);
    }

    private QueryService getQueryService(String productKey) {
        String host = dockHost + (InsureDock.isNewProduct(productKey) ? productKey.toLowerCase() : "general");
        return new HproseHttpClient(host + uri).useService(QueryService.class);
    }

    public RpcResponse<RspPreInsureBean> preInsure(InsureBean bean, String productKey) {
        try {
            InsureService service = getService(productKey);
            return service != null ? service.preInsure(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public RpcResponse<RspInsureBean> insure(InsureBean bean, String productKey) {
        try {
            InsureService service = getService(productKey);
            return service != null ? service.insure(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public RpcResponse<RspPayBean> pay(PayBean bean, String productKey) {
        try {
            InsureService service = getService(productKey);
            return service != null ? service.pay(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public RpcResponse<RspIssueBean> issue(IssueBean bean, String productKey) {
        try {
            InsureService service = getService(productKey);
            return service != null ? service.issue(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public RpcResponse<String> quote(QuoteBean bean, String productKey) {
        try {
            QueryService service = getQueryService(productKey);
            return service != null ? service.quote(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }


}
