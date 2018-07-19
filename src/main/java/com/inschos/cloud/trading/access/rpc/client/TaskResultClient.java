package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.TaskResultDataBean;
import com.inschos.cloud.trading.access.rpc.service.TaskResultService;
import com.inschos.common.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/5/17 on 13:51
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class TaskResultClient {

    @Value("${rpc.remote.task.host}")
    private String host;

    private final String uri = "/rpc/task";


    private TaskResultService getService() {
        return new HproseHttpClient(host + uri).useService(TaskResultService.class);
    }

    public long updateTaskResult(TaskResultDataBean taskResultDataBean) {
        try {
            TaskResultService service = getService();
            return service != null ? service.updateTaskResult(taskResultDataBean) : -1;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return -1;
        }
    }

}
