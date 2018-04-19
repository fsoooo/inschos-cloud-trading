package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.InsuranceConciseInfo;
import com.inschos.cloud.trading.access.rpc.service.InsuranceService;
import com.inschos.cloud.trading.assist.kit.L;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/4/17 on 11:53
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class InsuranceServiceClient extends BaseClientService {

    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/rpc/account";

    private InsuranceService insuranceService;

    private InsuranceService getInsuranceService() {
        if (insuranceService == null) {
            insuranceService = getService(host + uri, InsuranceService.class);
        }
        return insuranceService;
    }

    public List<InsuranceConciseInfo> insList() {
        try {
            InsuranceService service = getInsuranceService();
            return service != null ? service.insList() : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public InsuranceConciseInfo insList(String product_id) {
        try {
            InsuranceService service = getInsuranceService();
            return service != null ? service.insList(product_id) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }
}
