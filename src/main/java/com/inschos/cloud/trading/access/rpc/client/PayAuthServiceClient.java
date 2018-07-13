package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.dock.api.PayAuthService;
import com.inschos.dock.bean.*;
import hprose.client.HproseHttpClient;
import org.springframework.stereotype.Component;

/**
 * author   meiming_mm@163.com
 * date     2018/7/13
 * version  v1.0.0
 */
@Component
public class PayAuthServiceClient {

    private final String uri = "/rpc/agent";

    private PayAuthService getService(String productKey) {
        // TODO: 2018/7/13
        String host = null;

        return new HproseHttpClient(host + uri).useService(PayAuthService.class);
    }


    public RpcResponse<RspBankApplyBean> bankApplyAuth(String productKey, BankBean bean) {
        try {
            PayAuthService service = getService(productKey);
            return service != null ? service.bankApplyAuth(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 银行鉴权确认
     *
     * @param bean
     * @return
     */
    public RpcResponse<RspBankConfirmBean> bankConfirmAuth(String productKey, BankConfirmBean bean) {
        try {
            PayAuthService service = getService(productKey);
            return service != null ? service.bankConfirmAuth(bean) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }


}
