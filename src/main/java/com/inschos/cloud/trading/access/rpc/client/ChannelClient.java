package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.AccountBean;
import com.inschos.cloud.trading.access.rpc.bean.ChannelBean;
import com.inschos.cloud.trading.access.rpc.service.AccountService;
import com.inschos.cloud.trading.access.rpc.service.ChannelService;
import com.inschos.cloud.trading.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/6/19 on 19:34
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class ChannelClient {

    @Value("${rpc.remote.channel.host}")
    private String host;

    private final String uri = "/rpc/channel";

    private ChannelService getService() {
        return new HproseHttpClient(host + uri).useService(ChannelService.class);
    }

    public List<ChannelBean> getChannelListByIds(List<String> ids) {
        try {
            ChannelService service = getService();
            return service != null ? service.getChannelListByIds(ids) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

}
