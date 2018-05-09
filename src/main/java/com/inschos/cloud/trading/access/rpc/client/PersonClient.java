package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.AgentBean;
import com.inschos.cloud.trading.access.rpc.service.PersonService;
import com.inschos.cloud.trading.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/4/26 on 14:13
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class PersonClient {

    @Value("${rpc.remote.agent.host}")
    private String host;

    private final String uri = "/rpc/agent";

    private PersonService getService() {
        return new HproseHttpClient(host + uri).useService(PersonService.class);
    }

    public AgentBean getAgentInfoByPersonIdManagerUuid(String managerUuid, String personId) {
        try {
            PersonService service = getService();
            return service != null ? service.getAgentInfoByPersonIdManagerUuid(managerUuid, personId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

}
