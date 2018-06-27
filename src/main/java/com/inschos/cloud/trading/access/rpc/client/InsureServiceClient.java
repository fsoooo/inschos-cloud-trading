package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.common.assist.kit.L;
import com.inschos.dock.api.InsureService;
import com.inschos.dock.bean.*;
import hprose.client.HproseHttpClient;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/6/25.
 */
@Component
public class InsureServiceClient  {


    private final String uri = "/rpc/agent";

    private InsureService getService(String productKey) {
        // TODO: 2018/6/26  
        String host = null;
        
        return new HproseHttpClient(host + uri).useService(InsureService.class);
    }

    public RpcResponse<RspPreInsureBean> preInsure(InsureBean bean,String productKey) {
        try {
            InsureService service = getService(productKey);
            return service != null ? service.preInsure(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public RpcResponse<RspInsureBean> insure(InsureBean bean,String productKey) {
        try {
            InsureService service = getService(productKey);
            return service != null ? service.insure(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public RpcResponse<RspPayBean> pay(PayBean bean,String productKey) {
        try {
            InsureService service = getService(productKey);
            return service != null ? service.pay(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }
}
